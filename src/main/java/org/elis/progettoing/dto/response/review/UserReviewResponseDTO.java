package org.elis.progettoing.dto.response.review;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data Transfer Object (DTO) representing the details of a review for a product or service.
 *
 * <p>The {@link ReviewResponseDTO} class includes the review's comment, ratings for various
 * attributes, and metadata such as the review's creation date and the reviewer (user) details.</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserReviewResponseDTO extends ReviewResponseDTO {
    private String productTitle;
    private String productPackage;
}
