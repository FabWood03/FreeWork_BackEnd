package org.elis.progettoing.dto.request.order;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a request to create an order.
 * This DTO contains the necessary details for the order, including the cart ID,
 * total price, and description.
 * The fields are validated using constraints to ensure that the data is valid before processing.
 */
@Data
public class OrderRequestDTO {
    private long cartId;
    private long totalPrice;
    private String description;
}
