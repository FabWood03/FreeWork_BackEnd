package org.elis.progettoing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elis.progettoing.dto.request.review.ReviewRequestDTO;
import org.elis.progettoing.dto.response.review.ReviewResponseDTO;
import org.elis.progettoing.dto.response.review.ReviewSummaryResponse;
import org.elis.progettoing.dto.response.review.UserReviewResponseDTO;
import org.elis.progettoing.service.definition.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private ReviewResponseDTO reviewResponseDTO;
    private UserReviewResponseDTO userReviewResponseDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();

        reviewResponseDTO = new ReviewResponseDTO();
        reviewResponseDTO.setId(1L);
        reviewResponseDTO.setComment("Great product!");
        reviewResponseDTO.setDateCreation(LocalDateTime.now());
        reviewResponseDTO.setRatingQuality(5);
        reviewResponseDTO.setRatingCommunication(4);
        reviewResponseDTO.setRatingTimeliness(4);
        reviewResponseDTO.setRatingCost(5);
        reviewResponseDTO.setTotalRating(18);

        userReviewResponseDTO = new UserReviewResponseDTO();
        userReviewResponseDTO.setId(1L);
        userReviewResponseDTO.setComment("Great product!");
        userReviewResponseDTO.setDateCreation(LocalDateTime.now());
        userReviewResponseDTO.setRatingQuality(5);
        userReviewResponseDTO.setRatingCommunication(4);
        userReviewResponseDTO.setRatingTimeliness(4);
        userReviewResponseDTO.setRatingCost(5);
        userReviewResponseDTO.setTotalRating(18);
    }

    @Test
    void testGetReceivedReviewByUserId () throws Exception {
        List<ReviewResponseDTO> reviewList = Collections.singletonList(reviewResponseDTO);

        when(reviewService.getReviewsReceivedByUserId(1L)).thenReturn(reviewList);

        mockMvc.perform(get("/api/reviews/getReviewsReceivedByUserId")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].comment").value("Great product!"))
                .andExpect(jsonPath("$[0].ratingQuality").value(5));
    }

    @Test
    void testGetReviewByProductId() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);

        Page<ReviewResponseDTO> reviewPage = new PageImpl<>(Collections.singletonList(reviewResponseDTO), pageable, 1);

        when(reviewService.getReviewsByProductId(1L, pageable)).thenReturn(reviewPage);

        mockMvc.perform(get("/api/reviews/findByProductId")
                        .param("productId", "1")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].comment").value("Great product!"))
                .andExpect(jsonPath("$.content[0].ratingQuality").value(5));
    }

    @Test
    void testGetReviewSummaryByProductId() throws Exception {
        ReviewSummaryResponse reviewSummaryResponse = new ReviewSummaryResponse();
        reviewSummaryResponse.setAverageRating(4.5);
        when(reviewService.getReviewSummaryByProductId(1L)).thenReturn(reviewSummaryResponse);

        mockMvc.perform(get("/api/reviews/reviewSummaryByProductId")
                        .param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.5));
    }

    @Test
    void testGetReviewSummaryByUserId() throws Exception {
        ReviewSummaryResponse reviewSummaryResponse = new ReviewSummaryResponse();
        reviewSummaryResponse.setAverageRating(4.0);
        when(reviewService.getReviewSummaryByUserId(1L)).thenReturn(reviewSummaryResponse);

        mockMvc.perform(get("/api/reviews/reviewSummaryByUserId")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.0));
    }

    @Test
    void createReview_withValidRequestAndImages_returnsCreatedReview() throws Exception {
        // Create and configure the ReviewRequestDTO
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO();
        reviewRequestDTO.setComment("This product is amazing!");
        reviewRequestDTO.setRatingCost(5);
        reviewRequestDTO.setRatingQuality(5);
        reviewRequestDTO.setRatingTimeliness(5);
        reviewRequestDTO.setRatingCommunication(5);
        reviewRequestDTO.setProductId(1L); // Set a valid product ID (must be > 0)

        // Create and configure the ReviewResponseDTO
        ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO();
        reviewResponseDTO.setId(1L);
        reviewResponseDTO.setComment("This product is amazing!");
        reviewResponseDTO.setRatingCost(5);
        reviewResponseDTO.setRatingQuality(5);
        reviewResponseDTO.setRatingTimeliness(5);
        reviewResponseDTO.setRatingCommunication(5);

        // Mock the service behavior
        MockMultipartFile image = new MockMultipartFile("images", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());
        when(reviewService.createReview(any(ReviewRequestDTO.class), anyList())).thenReturn(reviewResponseDTO);

        // Serialize reviewRequestDTO to JSON
        String reviewRequestDTOJson = new ObjectMapper().writeValueAsString(reviewRequestDTO);
        MockMultipartFile reviewRequestDTOFile = new MockMultipartFile(
                "reviewRequestDTO", // Part name in the request
                "reviewRequestDTO.json", // Original file name
                MediaType.APPLICATION_JSON_VALUE, // Content-Type: application/json
                reviewRequestDTOJson.getBytes() // Content as bytes
        );

        // Perform the multipart request
        mockMvc.perform(multipart("/api/reviews/create")
                        .file(reviewRequestDTOFile) // JSON data
                        .file(image) // Image file
                        .contentType(MediaType.MULTIPART_FORM_DATA)) // Multipart form data
                .andExpect(status().isCreated()) // Expect 201 Created
                .andExpect(jsonPath("$.id").exists()) // Expect the response to have an "id" field
                .andExpect(jsonPath("$.comment").value("This product is amazing!")) // Verify comment
                .andExpect(jsonPath("$.ratingCost").value(5)) // Verify cost rating
                .andExpect(jsonPath("$.ratingQuality").value(5)) // Verify quality rating
                .andExpect(jsonPath("$.ratingTimeliness").value(5)) // Verify timeliness rating
                .andExpect(jsonPath("$.ratingCommunication").value(5)); // Verify communication rating
    }

    @Test
    void getReviewsByUserId_withValidUserId_returnsReviewList() throws Exception {
        long userId = 1L;
        List<ReviewResponseDTO> reviews = List.of(new ReviewResponseDTO(), new ReviewResponseDTO());
        when(reviewService.getReviewsByUserId(userId)).thenReturn(reviews);

        mockMvc.perform(get("/api/reviews/getReviewsByUserId")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
