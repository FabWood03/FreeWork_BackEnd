package org.elis.progettoing.dto.response.cart;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a purchased product response.
 * This class contains the details of a purchased product in the cart,
 * including the product's details, the selected package, and the purchase date.
 *
 * <p>This DTO is used to return detailed information about a product that has been purchased
 * or added to the cart, including the product's details, the selected package, and the date of purchase.</p>
 */
@Data
public class PurchasedProductResponseDTO {

    private long id;
    private String productImagePhoto;
    private String productTitle;
    private String userName;
    private String userSurname;
    private String type;
    private long price;
}
