package org.elis.progettoing.dto.request.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a request to purchase a product.
 * This DTO is used for submitting the required information to record a product purchase,
 * including the product ID and the associated package ID.
 * The class ensures that the necessary details for a purchased product are provided, with validation for mandatory fields.
 */
@Data
public class PurchasedProductRequestDTO {

    @NotNull(message = "Il service ID non può essere nullo")
    private long productId;

    @NotNull(message = "Il package ID non può essere nullo")
    private long packageId;

    /**
     * Constructor for the PurchasedProductRequestDTO class.
     * @param productId The ID of the product being purchased.
     * @param packageId The ID of the package being purchased.
     */
    public PurchasedProductRequestDTO(long productId, long packageId) {
        this.productId = productId;
        this.packageId = packageId;
    }

    /**
     * Default constructor for the PurchasedProductRequestDTO class.
     */
    public PurchasedProductRequestDTO() {}
}
