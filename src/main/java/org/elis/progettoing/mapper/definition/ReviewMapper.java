package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.request.review.ReviewRequestDTO;
import org.elis.progettoing.dto.response.review.ReviewResponseDTO;
import org.elis.progettoing.dto.response.review.UserReviewResponseDTO;
import org.elis.progettoing.models.Review;

/**
 * Interface for mapping between Review entities and their respective DTOs.
 * This interface defines methods for converting a ReviewRequestDTO to a Review entity
 * and a Review entity to a ReviewResponseDTO.
 */
public interface ReviewMapper {

    /**
     * Converts a ReviewRequestDTO to a Review entity.
     *
     * @param reviewRequestDTO the ReviewRequestDTO to be converted
     * @return the Review entity populated with data from the ReviewRequestDTO
     */
    Review reviewRequestDTOToReview(ReviewRequestDTO reviewRequestDTO);

    /**
     * Converts a Review entity to a ReviewResponseDTO.
     *
     * @param review the Review entity to be converted
     * @return the ReviewResponseDTO populated with data from the Review entity
     */
    ReviewResponseDTO reviewToReviewResponseDTO(Review review);

    /**
     * Converts a Review entity to a UserReviewResponseDTO.
     *
     * @param review the Review entity to be converted
     * @return the UserReviewResponseDTO populated with data from the Review entity
     */
    UserReviewResponseDTO reviewToUserReviewResponseDTO(Review review);
}
