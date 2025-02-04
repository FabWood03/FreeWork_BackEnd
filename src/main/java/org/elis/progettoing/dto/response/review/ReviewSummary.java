package org.elis.progettoing.dto.response.review;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a summary of a product's reviews.
 * This class provides details about the average rating and the total number of reviews.
 *
 * <p>The {@link ReviewSummary} class is used to structure the information returned
 * when querying a product's reviews, including the average rating and the total number of reviews.</p>
 */
@Data
public class ReviewSummary {
    private int rating;
    private long count;

    /**
     * Constructor for the ReviewSummary class.
     * @param rating the average rating of the product's reviews
     * @param count the total number of reviews for the product
     */
    public ReviewSummary(int rating, long count) {
        this.rating = rating;
        this.count = count;
    }
}
