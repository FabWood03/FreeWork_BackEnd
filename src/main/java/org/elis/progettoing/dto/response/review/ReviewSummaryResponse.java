package org.elis.progettoing.dto.response.review;

import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the response for a set of review summaries.
 * This class provides a list of {@link ReviewSummary} objects, the total number of reviews,
 * and the average rating for a given product.
 *
 * <p>The {@link ReviewSummaryResponse} class is used to structure the information returned
 * when querying a product's reviews, including the number of reviews for each rating and
 * the average rating for the product.</p>
 */
@Data
public class ReviewSummaryResponse {
    private List<ReviewSummary> reviewSummaries;
    private long totalReviews;
    private double averageRating;

    /**
     * Constructs a new {@link ReviewSummaryResponse} with the specified rating counts,
     * total number of reviews, and average rating.
     *
     * @param ratingCounts the list of {@link ReviewSummary} objects representing the number of reviews for each rating
     * @param totalReviews the total number of reviews for the product
     * @param roundedAverageRating the average rating for the product, rounded to the nearest half-point
     */
    public ReviewSummaryResponse(List<ReviewSummary> ratingCounts, long totalReviews, double roundedAverageRating) {
        this.reviewSummaries = ratingCounts;
        this.totalReviews = totalReviews;
        this.averageRating = roundedAverageRating;
    }

    /**
     * Empty constructor for the {@link ReviewSummaryResponse} class.
     */
    public ReviewSummaryResponse() {}
}
