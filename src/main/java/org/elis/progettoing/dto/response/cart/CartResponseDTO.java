package org.elis.progettoing.dto.response.cart;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a cart response.
 * This class holds the details of a cart, including the cart's ID and the list of purchased products in the cart.
 *
 * <p>This DTO is used to return information about a user's cart, including all the products the user has added to their cart.</p>
 */
@Data
public class CartResponseDTO {

    private long id;

    private List<PurchasedProductResponseDTO> purchasedProducts = new ArrayList<>();
}
