package org.elis.progettoing.controllers;

import jakarta.validation.Valid;
import org.elis.progettoing.dto.request.review.ReviewRequestDTO;
import org.elis.progettoing.dto.response.review.ReviewResponseDTO;
import org.elis.progettoing.dto.response.review.ReviewSummary;
import org.elis.progettoing.dto.response.review.ReviewSummaryResponse;
import org.elis.progettoing.service.definition.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for managing product reviews.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Constructs an instance of {@code ReviewController}.
     *
     * @param reviewService the service managing review-related business logic.
     */
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create")
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestPart(name = "reviewRequestDTO") ReviewRequestDTO reviewRequestDTO,
                                                          @RequestPart(name = "images", required = false) List<MultipartFile> images) {
        ReviewResponseDTO reviewResponseDTO = reviewService.createReview(reviewRequestDTO, images);
        return new ResponseEntity<>(reviewResponseDTO, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve all reviews for a specific product by its ID.
     *
     * @param productId the ID of the product whose reviews are to be retrieved.
     * @param page      the page number of the reviews to be retrieved.
     * @param size      the number of reviews to be retrieved per page.
     * @return a {@link ResponseEntity} containing a list of {@link ReviewResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/findByProductId")
    public ResponseEntity<Page<ReviewResponseDTO>> getReviewByProductId(
            @RequestParam("productId") long productId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponseDTO> reviewPage = reviewService.getReviewsByProductId(productId, pageable);

        return new ResponseEntity<>(reviewPage, HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve all Received reviews for a specific product by its ID.
     *
     * @param userId the ID of the user whose reviews are to be retrieved.
     * @return a {@link ResponseEntity} containing a list of {@link ReviewResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getReviewsReceivedByUserId")
    public ResponseEntity<List<ReviewResponseDTO>> getReceivedReviewByUserId(@RequestParam("userId") long userId) {
        List<ReviewResponseDTO> reviewPage = reviewService.getReviewsReceivedByUserId(userId);
        return new ResponseEntity<>(reviewPage, HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve all reviews for a specific user by their ID.
     *
     * @param userId the ID of the user whose reviews are to be retrieved.
     * @return a {@link ResponseEntity} containing a list of {@link ReviewResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getReviewsByUserId")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByUserId(@RequestParam("userId") long userId) {
        List<ReviewResponseDTO> reviewPage = reviewService.getReviewsByUserId(userId);
        return new ResponseEntity<>(reviewPage, HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve a summary of reviews for a specific product by its ID.
     *
     * @param productId the ID of the product whose review summary is to be retrieved.
     * @return a {@link ResponseEntity} containing a list of {@link ReviewSummary} and HTTP status 200 (OK).
     */
    @GetMapping("/reviewSummaryByProductId")
    public ResponseEntity<ReviewSummaryResponse> getReviewSummaryByProductId(@RequestParam("productId") long productId) {
        return new ResponseEntity<>(reviewService.getReviewSummaryByProductId(productId), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve a summary of reviews for a specific user by their ID.
     *
     * @param userId the ID of the user whose review summary is to be retrieved.
     * @return a {@link ResponseEntity} containing a list of {@link ReviewSummary} and HTTP status 200 (OK).
     */
    @GetMapping("/reviewSummaryByUserId")
    public ResponseEntity<ReviewSummaryResponse> getReviewSummaryByUserId(@RequestParam("userId") long userId) {
        return new ResponseEntity<>(reviewService.getReviewSummaryByUserId(userId), HttpStatus.OK);
    }
}
