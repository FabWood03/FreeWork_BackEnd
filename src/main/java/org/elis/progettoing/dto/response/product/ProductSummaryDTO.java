package org.elis.progettoing.dto.response.product;

import lombok.Data;
import org.elis.progettoing.dto.response.user.UserResponseDTO;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a summary of a product.
 * This class provides basic information about a product, such as the product ID,
 * title, description, associated user, product photos, and starting price.
 *
 * <p>The {@link ProductSummaryDTO} class is used to return essential details
 * about a product in response to a request, typically in cases where full product details
 * are not required.</p>
 */
@Data
public class ProductSummaryDTO {

    private long id;

    private String title;

    private String description;

    private UserResponseDTO user;

    private List<String> urlProductPhoto;

    private double startPrice;

    /**
     * Default constructor for {@link ProductSummaryDTO}.
     */
    public ProductSummaryDTO() {}

    /**
     * Constructs a new ProductSummaryDTO with the specified ID and title.
     *
     * @param id the product ID
     * @param title the product title
     */
    public ProductSummaryDTO(long id, String title) {
        this.id = id;
        this.title = title;
    }
}
