package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.review.ReviewRequestDTO;
import org.elis.progettoing.dto.response.review.ReviewResponseDTO;
import org.elis.progettoing.dto.response.review.UserReviewResponseDTO;
import org.elis.progettoing.mapper.definition.ReviewMapper;
import org.elis.progettoing.models.Review;
import org.springframework.stereotype.Component;

/**
 * Implementation of the ReviewMapper interface. Provides methods for mapping between
 * Review-related request and response DTOs and entity models.
 */
@Component
public class ReviewMapperImpl implements ReviewMapper {

    private final UserMapperImpl userMapperImpl;

    /**
     * Constructs a new ReviewMapperImpl with the specified UserMapper.
     *
     * @param userMapperImpl the UserMapper to be used for mapping
     */
    public ReviewMapperImpl(UserMapperImpl userMapperImpl) {
        this.userMapperImpl = userMapperImpl;
    }

    /**
     * Converts a ReviewRequestDTO to a Review entity.
     *
     * @param reviewRequestDTO the ReviewRequestDTO to be converted
     * @return a Review entity populated with data from the ReviewRequestDTO, or null if the reviewRequestDTO is null
     */
    @Override
    public Review reviewRequestDTOToReview(ReviewRequestDTO reviewRequestDTO) {
        if (reviewRequestDTO == null) {
            return null;
        }

        Review review = new Review();

        review.setId(reviewRequestDTO.getId());
        review.setComment(reviewRequestDTO.getComment());
        review.setRatingQuality(reviewRequestDTO.getRatingQuality());
        review.setRatingCommunication(reviewRequestDTO.getRatingCommunication());
        review.setRatingTimeliness(reviewRequestDTO.getRatingTimeliness());
        review.setRatingCost(reviewRequestDTO.getRatingCost());
        return review;
    }

    /**
     * Converts a Review entity to a ReviewResponseDTO.
     *
     * @param review the Review entity to be converted
     * @return a ReviewResponseDTO populated with data from the Review entity, or null if the review is null
     */
    @Override
    public ReviewResponseDTO reviewToReviewResponseDTO(Review review) {
        if (review == null) {
            return null;
        }

        ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO();

        reviewResponseDTO.setId(review.getId());
        reviewResponseDTO.setComment(review.getComment());
        reviewResponseDTO.setDateCreation(review.getDateCreation());
        reviewResponseDTO.setRatingQuality(review.getRatingQuality());
        reviewResponseDTO.setRatingCommunication(review.getRatingCommunication());
        reviewResponseDTO.setRatingTimeliness(review.getRatingTimeliness());
        reviewResponseDTO.setRatingCost(review.getRatingCost());
        reviewResponseDTO.setTotalRating(review.getTotalRating());
        reviewResponseDTO.setImagesPath(review.getUrlReviewPhoto());
        reviewResponseDTO.setUser(userMapperImpl.userToUserResponseDTO(review.getUser()));

        return reviewResponseDTO;
    }

    /**
     * Converts a Review entity to a UserReviewResponseDTO.
     *
     * @param review the Review entity to be converted
     * @return a UserReviewResponseDTO populated with data from the Review entity, or null if the review is null
     */
    @Override
    public UserReviewResponseDTO reviewToUserReviewResponseDTO(Review review) {
        if (review == null) {
            return null;
        }

        UserReviewResponseDTO userReviewResponseDTO = new UserReviewResponseDTO();

        userReviewResponseDTO.setId(review.getId());
        userReviewResponseDTO.setComment(review.getComment());
        userReviewResponseDTO.setDateCreation(review.getDateCreation());
        userReviewResponseDTO.setRatingQuality((int) review.getRatingQuality());
        userReviewResponseDTO.setRatingCommunication((int) review.getRatingCommunication());
        userReviewResponseDTO.setRatingTimeliness((int) review.getRatingTimeliness());
        userReviewResponseDTO.setRatingCost((int) review.getRatingCost());
        userReviewResponseDTO.setTotalRating((int) review.getTotalRating());
        userReviewResponseDTO.setImagesPath(review.getUrlReviewPhoto());
        userReviewResponseDTO.setProductTitle(review.getProduct().getTitle());
        userReviewResponseDTO.setUser(userMapperImpl.userToUserResponseDTO(review.getUser()));

        return userReviewResponseDTO;
    }
}
