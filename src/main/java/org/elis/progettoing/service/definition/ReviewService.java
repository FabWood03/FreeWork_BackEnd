package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.request.review.ReviewRequestDTO;
import org.elis.progettoing.dto.response.review.ReviewResponseDTO;
import org.elis.progettoing.dto.response.review.ReviewSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO createReview(ReviewRequestDTO reviewRequestDTO, List<MultipartFile> images);

    ReviewResponseDTO deleteReview(long reviewId);

    ReviewResponseDTO updateReview(ReviewRequestDTO reviewRequestDTO, List<MultipartFile> images);

    Page<ReviewResponseDTO> getReviewsByProductId(long productId, Pageable pageable);

    List<ReviewResponseDTO> getReviewsReceivedByUserId(long userId);

    List<ReviewResponseDTO> getReviewsByUserId(long userId);

    ReviewSummaryResponse getReviewSummaryByProductId(long productId);

    ReviewSummaryResponse getReviewSummaryByUserId(long userId);
}
