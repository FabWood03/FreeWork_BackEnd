package org.elis.progettoing.dto.response.product;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a tag associated with a product.
 * This class provides the name of the tag, which is typically used to categorize or label a product.
 *
 * <p>The {@link TagResponseDTO} class is used to return information about a tag in response
 * to a request, typically when products are tagged with specific keywords for categorization.</p>
 */
@Data
public class TagResponseDTO {

    private String name;
}
