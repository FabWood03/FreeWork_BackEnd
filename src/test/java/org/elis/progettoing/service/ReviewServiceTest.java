package org.elis.progettoing.service;

import org.elis.progettoing.dto.request.review.ReviewRequestDTO;
import org.elis.progettoing.dto.response.review.ReviewResponseDTO;
import org.elis.progettoing.dto.response.review.ReviewSummary;
import org.elis.progettoing.dto.response.review.ReviewSummaryResponse;
import org.elis.progettoing.exception.InvalidProductOwnerException;
import org.elis.progettoing.exception.entity.*;
import org.elis.progettoing.mapper.implementation.ReviewMapperImpl;
import org.elis.progettoing.models.Review;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.repository.OrderProductRepository;
import org.elis.progettoing.repository.ProductRepository;
import org.elis.progettoing.repository.ReviewRepository;
import org.elis.progettoing.repository.TicketRepository;
import org.elis.progettoing.service.implementation.LocalStorageService;
import org.elis.progettoing.service.implementation.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewMapperImpl reviewMapper;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private LocalStorageService localStorageService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Product product;
    private ReviewRequestDTO reviewRequestDTO;
    private Review review;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        User productOwner = new User();
        productOwner.setId(2L);

        product = new Product();
        product.setId(100L);
        product.setUser(productOwner);

        reviewRequestDTO = new ReviewRequestDTO();
        reviewRequestDTO.setProductId(100L);
        reviewRequestDTO.setRatingQuality(4);
        reviewRequestDTO.setRatingCommunication(3);
        reviewRequestDTO.setRatingTimeliness(5);
        reviewRequestDTO.setRatingCost(4);

        review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setProduct(product);
        review.setDateCreation(LocalDateTime.now());
        review.setTotalRating(4.0f);

        // Imposta l'utente autenticato
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null)
        );
    }

    @Test
    void createReview_Success() {
        // Configura il comportamento del mapper
        doAnswer(invocation -> {
            ReviewRequestDTO dto = invocation.getArgument(0);
            Review r = new Review();
            r.setRatingQuality(dto.getRatingQuality());
            r.setRatingCommunication(dto.getRatingCommunication());
            r.setRatingTimeliness(dto.getRatingTimeliness());
            r.setRatingCost(dto.getRatingCost());
            return r;
        }).when(reviewMapper).reviewRequestDTOToReview(any(ReviewRequestDTO.class));

        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(reviewRepository.existsByUserIdAndProductId(1L, 100L)).thenReturn(false);
        when(localStorageService.saveReviewImages(anyList(), eq(100L), eq(1L)))
                .thenReturn(List.of("image1.jpg", "image2.jpg"));
        when(reviewMapper.reviewToReviewResponseDTO(any(Review.class))).thenReturn(new ReviewResponseDTO());
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponseDTO response = reviewService.createReview(reviewRequestDTO, null);

        assertNotNull(response);

        // Verifica il salvataggio e cattura la review
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());

        Review savedReview = reviewCaptor.getValue();
        float expectedRating = (4f + 3f + 5f + 4f) / 4;
        assertEquals(expectedRating, savedReview.getTotalRating(), 0.01f);

        // Verifica aggiuntiva dei campi
        assertEquals(4, savedReview.getRatingQuality());
        assertEquals(3, savedReview.getRatingCommunication());
        assertEquals(5, savedReview.getRatingTimeliness());
        assertEquals(4, savedReview.getRatingCost());
    }

    @Test
    void createReview_ProductNotFound() {
        when(productRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                reviewService.createReview(reviewRequestDTO, Collections.emptyList())
        );
    }

    @Test
    void createReview_UserIsProductOwner() {
        product.getUser().setId(1L); // Rendiamo l'utente proprietario del prodotto
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        assertThrows(InvalidProductOwnerException.class, () ->
                reviewService.createReview(reviewRequestDTO, Collections.emptyList())
        );
    }

    @Test
    void createReview_ReviewAlreadyExists() {
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(reviewRepository.existsByUserIdAndProductId(1L, 100L)).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () ->
                reviewService.createReview(reviewRequestDTO, Collections.emptyList())
        );
    }

    @Test
    void createReview_SaveFailure() {
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(reviewRepository.existsByUserIdAndProductId(1L, 100L)).thenReturn(false);
        when(localStorageService.saveReviewImages(anyList(), eq(100L), eq(1L)))
                .thenReturn(List.of("image1.jpg"));
        when(reviewMapper.reviewRequestDTOToReview(reviewRequestDTO)).thenReturn(review);
        when(reviewRepository.save(review)).thenThrow(new RuntimeException("Database error"));

        assertThrows(EntityCreationException.class, () ->
                reviewService.createReview(reviewRequestDTO, Collections.emptyList())
        );
    }

    @Test
    void deleteReview_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        doNothing().when(ticketRepository).unsetReview(1L);
        when(reviewMapper.reviewToReviewResponseDTO(review)).thenReturn(new ReviewResponseDTO());

        ReviewResponseDTO response = reviewService.deleteReview(1L);

        assertNotNull(response);
        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_ReviewNotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                reviewService.deleteReview(1L)
        );
    }

    @Test
    void deleteReview_DeletionFailure() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        doThrow(new RuntimeException("Deletion error")).when(reviewRepository).delete(review);

        assertThrows(EntityDeletionException.class, () ->
                reviewService.deleteReview(1L)
        );
    }

    @Test
    void updateReview_Success() {
        doAnswer(invocation -> {
            ReviewRequestDTO dto = invocation.getArgument(0);
            review.setRatingQuality(dto.getRatingQuality());
            review.setRatingCommunication(dto.getRatingCommunication());
            review.setRatingTimeliness(dto.getRatingTimeliness());
            review.setRatingCost(dto.getRatingCost());
            return review;
        }).when(reviewMapper).reviewRequestDTOToReview(any(ReviewRequestDTO.class));

        when(reviewRepository.findByUserIdAndProductId(1L, 100L)).thenReturn(review);
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(localStorageService.saveReviewImages(anyList(), eq(100L), eq(1L)))
                .thenReturn(List.of("new_image.jpg"));
        when(reviewMapper.reviewToReviewResponseDTO(review)).thenReturn(new ReviewResponseDTO());
        when(reviewRepository.save(review)).thenReturn(review);

        ReviewRequestDTO updateDTO = new ReviewRequestDTO();
        updateDTO.setProductId(100L);
        updateDTO.setRatingQuality(5);
        updateDTO.setRatingCommunication(4);
        updateDTO.setRatingTimeliness(5);
        updateDTO.setRatingCost(4);

        ReviewResponseDTO response = reviewService.updateReview(updateDTO, Collections.emptyList());

        assertNotNull(response);
        assertEquals(4.5f, review.getTotalRating(), 0.01); // Aggiunto delta per confronto float
        verify(reviewRepository).save(review);
    }

    @Test
    void updateReview_ReviewNotFound_ShouldThrowException() {
        // Configura l'utente autenticato
        User user = new User();
        user.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null)
        );

        // Configura il comportamento dei mock
        when(reviewRepository.findByUserIdAndProductId(1L, 100L)).thenReturn(null);
        when(productRepository.findById(100L)).thenReturn(Optional.of(new Product()));

        // Crea DTO di test
        ReviewRequestDTO updateDTO = new ReviewRequestDTO();
        updateDTO.setProductId(100L);
        updateDTO.setRatingQuality(5);

        // Verifica l'eccezione
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> reviewService.updateReview(updateDTO, Collections.emptyList())
        );

        // Verifica i dettagli dell'eccezione
        assertEquals("Nessun recensione con ID utente = 1 è stato trovato.", exception.getMessage());

        // Verifica che non ci siano state interazioni con il save
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void updateReview_ReviewNotFound() {
        when(reviewRepository.findByUserIdAndProductId(1L, 100L)).thenReturn(null);

        ReviewRequestDTO updateDTO = new ReviewRequestDTO();
        updateDTO.setProductId(100L);

        assertThrows(EntityNotFoundException.class, () ->
                reviewService.updateReview(updateDTO, Collections.emptyList())
        );
    }

    @Test
    void updateReview_ProductNotFound() {
        when(reviewRepository.findByUserIdAndProductId(1L, 100L)).thenReturn(review);
        when(productRepository.findById(100L)).thenReturn(Optional.empty());

        ReviewRequestDTO updateDTO = new ReviewRequestDTO();
        updateDTO.setProductId(100L);

        assertThrows(EntityNotFoundException.class, () ->
                reviewService.updateReview(updateDTO, Collections.emptyList())
        );
    }

    @Test
    void updateReview_SaveFailure() {
        when(reviewRepository.findByUserIdAndProductId(1L, 100L)).thenReturn(review);
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(reviewRepository.save(review)).thenThrow(new RuntimeException("DB error"));

        ReviewRequestDTO updateDTO = new ReviewRequestDTO();
        updateDTO.setProductId(100L);

        assertThrows(EntityEditException.class, () ->
                reviewService.updateReview(updateDTO, Collections.emptyList())
        );
    }

    @Test
    void getReviewsByProductId_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> mockPage = new PageImpl<>(List.of(review));
        when(reviewRepository.findByProductId(100L, pageable)).thenReturn(mockPage);
        when(reviewMapper.reviewToReviewResponseDTO(review)).thenReturn(new ReviewResponseDTO());

        Page<ReviewResponseDTO> result = reviewService.getReviewsByProductId(100L, pageable);

        assertEquals(1, result.getContent().size());
        verify(reviewRepository).findByProductId(100L, pageable);
    }

    @Test
    void getReviewsReceivedByUserId_Success() {
        List<Review> reviews = List.of(review);
        when(reviewRepository.findByProductOwnerId(2L)).thenReturn(reviews);
        when(reviewMapper.reviewToReviewResponseDTO(review)).thenReturn(new ReviewResponseDTO());

        List<ReviewResponseDTO> result = reviewService.getReviewsReceivedByUserId(2L);

        assertEquals(1, result.size());
        verify(reviewRepository).findByProductOwnerId(2L);
    }

    @Test
    void getReviewsReceivedByUserId_EmptyList() {
        when(reviewRepository.findByProductOwnerId(2L)).thenReturn(Collections.emptyList());

        List<ReviewResponseDTO> result = reviewService.getReviewsReceivedByUserId(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getReviewsByUserId_Success() {
        List<Review> reviews = List.of(review);
        when(reviewRepository.findByUserId(1L)).thenReturn(reviews);
        when(reviewMapper.reviewToReviewResponseDTO(review)).thenReturn(new ReviewResponseDTO());

        List<ReviewResponseDTO> result = reviewService.getReviewsByUserId(1L);

        assertEquals(1, result.size());
        verify(reviewRepository).findByUserId(1L);
    }

    @Test
    void getReviewsByUserId_EmptyList() {
        when(reviewRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        List<ReviewResponseDTO> result = reviewService.getReviewsByUserId(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getReviewSummaryByProductId_NoReviews() {
        when(reviewRepository.countReviewsByProductId(1L))
                .thenReturn(Collections.emptyList());

        ReviewSummaryResponse response = reviewService.getReviewSummaryByProductId(1L);

        assertAll(
                () -> assertEquals(0, response.getTotalReviews()),
                () -> assertEquals(0.0, response.getAverageRating()),
                () -> assertTrue(response.getReviewSummaries().stream()
                        .allMatch(summary -> summary.getCount() == 0))
        );
    }

    @Test
    void getReviewSummaryByProductId_WithDecimalRatings() {
        List<Object[]> testData = List.of(
                new Object[]{4.5, 2L},  // Deve diventare 5 stelle
                new Object[]{3.3, 3L},  // Deve diventare 3 stelle
                new Object[]{5.5, 1L}   // Deve essere ignorato (6 stelle)
        );

        when(reviewRepository.countReviewsByProductId(1L)).thenReturn(testData);

        ReviewSummaryResponse response = reviewService.getReviewSummaryByProductId(1L);

        assertAll(
                () -> assertEquals(5, response.getTotalReviews()),
                () -> assertEquals(3.78, response.getAverageRating(), 0.01),
                () -> {
                    List<ReviewSummary> summaries = response.getReviewSummaries();
                    assertEquals(0, summaries.get(0).getCount()); // 1 stella
                    assertEquals(0, summaries.get(1).getCount()); // 2 stelle
                    assertEquals(3, summaries.get(2).getCount()); // 3 stelle
                    assertEquals(0, summaries.get(3).getCount()); // 4 stelle
                    assertEquals(2, summaries.get(4).getCount()); // 5 stelle
                }
        );
    }

    @Test
    void getReviewSummaryByProductId_MixedRatings() {
        List<Object[]> testData = List.of(
                new Object[]{1.0, 1L},
                new Object[]{5.0, 2L},
                new Object[]{2.5, 3L}
        );

        when(reviewRepository.countReviewsByProductId(1L)).thenReturn(testData);

        ReviewSummaryResponse response = reviewService.getReviewSummaryByProductId(1L);

        assertAll(
                () -> assertEquals(6, response.getTotalReviews()),
                () -> assertEquals(3.08, response.getAverageRating(), 0.01),
                () -> {
                    List<ReviewSummary> summaries = response.getReviewSummaries();
                    assertEquals(1, summaries.get(0).getCount()); // 1 stella
                    assertEquals(0, summaries.get(1).getCount()); // 2 stelle
                    assertEquals(3, summaries.get(2).getCount()); // 3 stelle
                    assertEquals(0, summaries.get(3).getCount()); // 4 stelle
                    assertEquals(2, summaries.get(4).getCount()); // 5 stelle
                }
        );
    }

    // Test per getReviewSummaryByUserId
    @Test
    void getReviewSummaryByUserId_NoReviews() {
        when(reviewRepository.countReviewsByProductUserId(1L))
                .thenReturn(Collections.emptyList());

        ReviewSummaryResponse response = reviewService.getReviewSummaryByUserId(1L);

        assertAll(
                () -> assertEquals(0, response.getTotalReviews()),
                () -> assertEquals(0.0, response.getAverageRating()),
                () -> assertTrue(response.getReviewSummaries().stream()
                        .allMatch(summary -> summary.getCount() == 0))
        );
    }

    @Test
    void getReviewSummaryByUserId_EdgeCases() {
        List<Object[]> testData = List.of(
                new Object[]{0.5, 3L},  // Arrotonda a 1 stella
                new Object[]{1.5, 2L},  // Arrotonda a 2 stelle
                new Object[]{6.0, 5L}   // Deve essere ignorato
        );

        when(reviewRepository.countReviewsByProductUserId(1L)).thenReturn(testData);

        ReviewSummaryResponse response = reviewService.getReviewSummaryByUserId(1L);

        assertAll(
                () -> assertEquals(5, response.getTotalReviews()), // 3 + 2 = 5
                () -> assertEquals(0.9, response.getAverageRating(), 0.01), // (0.5*3 + 1.5*2)/5 = 4.5/5 = 0.9
                () -> {
                    List<ReviewSummary> summaries = response.getReviewSummaries();
                    assertEquals(3, summaries.get(0).getCount()); // 1 stella
                    assertEquals(2, summaries.get(1).getCount()); // 2 stelle
                    assertEquals(0, summaries.get(2).getCount()); // 3 stelle
                    assertEquals(0, summaries.get(3).getCount()); // 4 stelle
                    assertEquals(0, summaries.get(4).getCount()); // 5 stelle
                }
        );
    }

    @Test
    void getReviewSummaryByUserId_MultipleCountsSameRating() {
        List<Object[]> testData = List.of(
                new Object[]{4.0, 2L},
                new Object[]{4.0, 3L},
                new Object[]{4.0, 1L}
        );

        when(reviewRepository.countReviewsByProductUserId(1L)).thenReturn(testData);

        ReviewSummaryResponse response = reviewService.getReviewSummaryByUserId(1L);

        assertAll(
                () -> assertEquals(6, response.getTotalReviews()),
                () -> assertEquals(4.0, response.getAverageRating()),
                () -> assertEquals(6, response.getReviewSummaries().get(3).getCount()) // 4 stelle
        );
    }

    @Test
    void getReviewSummaryByUserId_ExactHalfRatings() {
        List<Object[]> testData = List.of(
                new Object[]{2.5, 4L},  // Arrotonda a 3 stelle
                new Object[]{3.5, 2L}   // Arrotonda a 4 stelle
        );

        when(reviewRepository.countReviewsByProductUserId(1L)).thenReturn(testData);

        ReviewSummaryResponse response = reviewService.getReviewSummaryByUserId(1L);

        assertAll(
                () -> assertEquals(6, response.getTotalReviews()),
                () -> assertEquals(2.83, response.getAverageRating(), 0.01),
                () -> {
                    List<ReviewSummary> summaries = response.getReviewSummaries();
                    assertEquals(4, summaries.get(2).getCount()); // Sostituita virgola con ;
                    assertEquals(2, summaries.get(3).getCount()); // Aggiunto ;
                }
        );
    }
}