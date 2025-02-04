package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.request.review.ReviewRequestDTO;
import org.elis.progettoing.dto.response.review.ReviewResponseDTO;
import org.elis.progettoing.dto.response.review.UserReviewResponseDTO;
import org.elis.progettoing.mapper.implementation.ReviewMapperImpl;
import org.elis.progettoing.mapper.implementation.UserMapperImpl;
import org.elis.progettoing.models.Review;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewMapperImplTest {

    @Mock
    private UserMapperImpl userMapperImpl;

    @InjectMocks
    private ReviewMapperImpl reviewMapperImpl;

    private ReviewRequestDTO reviewRequestDTO;
    private Review review;

    @BeforeEach
    void setUp() {
        // Create a mock user
        User user = new User();
        user.setId(1L);
        user.setNickname("testUserNickname");

        // Create the ReviewRequestDTO object
        reviewRequestDTO = new ReviewRequestDTO();
        reviewRequestDTO.setId(1L);
        reviewRequestDTO.setComment("Great product!");
        reviewRequestDTO.setRatingQuality(5);
        reviewRequestDTO.setRatingCommunication(4);
        reviewRequestDTO.setRatingTimeliness(5);
        reviewRequestDTO.setRatingCost(4);

        // Create the Review entity
        review = new Review();
        review.setId(1L);
        review.setComment("Great product!");
        review.setRatingQuality(5);
        review.setRatingCommunication(4);
        review.setRatingTimeliness(5);
        review.setRatingCost(4);
        review.setTotalRating(18);
        review.setDateCreation(LocalDateTime.of(2025, 1, 5, 12, 0));
        review.setUrlReviewPhoto(Collections.singletonList("https://example.com/image.jpg"));
        review.setUser(user);

        // Create the ReviewResponseDTO object
        ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO();
        reviewResponseDTO.setId(1L);
        reviewResponseDTO.setComment("Great product!");
        review.setDateCreation(LocalDateTime.of(2025, 1, 5, 12, 0));
        reviewResponseDTO.setRatingQuality(5);
        reviewResponseDTO.setRatingCommunication(4);
        reviewResponseDTO.setRatingTimeliness(5);
        reviewResponseDTO.setRatingCost(4);
        reviewResponseDTO.setTotalRating(18);
        reviewResponseDTO.setImagesPath(Collections.singletonList("http://example.com/image.jpg"));
        reviewResponseDTO.setUser(new org.elis.progettoing.dto.response.user.UserResponseDTO());
    }

    @Test
    void testReviewRequestDTOToReview() {
        // Call the method
        Review result = reviewMapperImpl.reviewRequestDTOToReview(reviewRequestDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(reviewRequestDTO.getId(), result.getId());
        assertEquals(reviewRequestDTO.getComment(), result.getComment());
        assertEquals(reviewRequestDTO.getRatingQuality(), result.getRatingQuality());
        assertEquals(reviewRequestDTO.getRatingCommunication(), result.getRatingCommunication());
        assertEquals(reviewRequestDTO.getRatingTimeliness(), result.getRatingTimeliness());
        assertEquals(reviewRequestDTO.getRatingCost(), result.getRatingCost());
    }

    @Test
    void testReviewToReviewResponseDTO() {
        // Mock the userMapperImpl's response for user conversion
        when(userMapperImpl.userToUserResponseDTO(any(User.class))).thenReturn(new org.elis.progettoing.dto.response.user.UserResponseDTO());

        // Call the method
        ReviewResponseDTO result = reviewMapperImpl.reviewToReviewResponseDTO(review);

        // Assertions
        assertNotNull(result);
        assertEquals(review.getId(), result.getId());
        assertEquals(review.getComment(), result.getComment());
        assertEquals(review.getDateCreation(), result.getDateCreation());
        assertEquals(review.getRatingQuality(), result.getRatingQuality());
        assertEquals(review.getRatingCommunication(), result.getRatingCommunication());
        assertEquals(review.getRatingTimeliness(), result.getRatingTimeliness());
        assertEquals(review.getRatingCost(), result.getRatingCost());
        assertEquals(review.getTotalRating(), result.getTotalRating());
        assertEquals(review.getUrlReviewPhoto(), result.getImagesPath());
        assertNotNull(result.getUser());
    }

    @Test
    void testReviewRequestDTOToReview_NullDTO() {
        Review result = reviewMapperImpl.reviewRequestDTOToReview(null);

        // Assertions
        assertNull(result);
    }

    @Test
    void testReviewToReviewResponseDTO_NullEntity() {
        ReviewResponseDTO result = reviewMapperImpl.reviewToReviewResponseDTO(null);

        // Assertions
        assertNull(result);
    }

    @Test
    void testUserMapping() {
        // Verify that the userMapperImpl is called during the mapping process
        reviewMapperImpl.reviewToReviewResponseDTO(review);

        verify(userMapperImpl, times(1)).userToUserResponseDTO(any(User.class));
    }

    @Test
    void testReviewToUserReviewResponseDTO() {
        // Arrange
        Review review = mock(Review.class);
        Product product = mock(Product.class);

        LocalDateTime expectedDate = LocalDateTime.of(2025, 1, 5, 12, 0, 0);
        when(review.getId()).thenReturn(1L);
        when(review.getComment()).thenReturn("Great product!");
        when(review.getDateCreation()).thenReturn(expectedDate);
        when(review.getRatingQuality()).thenReturn(4.5);
        when(review.getRatingCommunication()).thenReturn(3.5);
        when(review.getRatingTimeliness()).thenReturn(4.0);
        when(review.getRatingCost()).thenReturn(5.0);
        when(review.getTotalRating()).thenReturn(4.5);
        when(review.getUrlReviewPhoto()).thenReturn(Collections.singletonList("path/to/photo.jpg"));
        when(review.getProduct()).thenReturn(product); // Associamo il prodotto alla recensione
        when(product.getTitle()).thenReturn("Product Title");

        // Act
        UserReviewResponseDTO result = reviewMapperImpl.reviewToUserReviewResponseDTO(review);

        // Assert
        assertNotNull(result); // Verifica che il risultato non sia nullo
        assertEquals(1L, result.getId()); // Verifica l'ID
        assertEquals("Great product!", result.getComment()); // Verifica il commento
        assertNotNull(result.getDateCreation()); // Verifica la data di creazione
        assertEquals(4, result.getRatingQuality()); // Verifica il ratingQuality (casting a int)
        assertEquals(3, result.getRatingCommunication()); // Verifica il ratingCommunication (casting a int)
        assertEquals(4, result.getRatingTimeliness()); // Verifica il ratingTimeliness (casting a int)
        assertEquals(5, result.getRatingCost()); // Verifica il ratingCost (casting a int)
        assertEquals(4, result.getTotalRating()); // Verifica il totalRating (casting a int)
        assertEquals(Collections.singletonList("path/to/photo.jpg"), result.getImagesPath());
        assertEquals("Product Title", result.getProductTitle()); // Verifica il titolo del prodotto
    }

    @Test
    void testReviewToUserReviewResponseDTO_NullEntity() {
        // Act
        UserReviewResponseDTO result = reviewMapperImpl.reviewToUserReviewResponseDTO(null);

        // Assert
        assertNull(result); // Verifica che il risultato sia nullo
    }
}
