package org.elis.progettoing.dto.response.order;

import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a response containing filtered orders.
 * This class contains a list of all orders, as well as lists of orders that have been refused,
 * delayed, delivered, pending, or taken on.
 *
 * <p>This DTO is used when fetching orders that have been filtered based on their status.</p>
 */
@Data
public class FilteredOrdersResponse {
    private List<OrderResponseDTO> allOrders;
    private List<OrderResponseDTO> refusedOrders;
    private List<OrderResponseDTO> delayedOrders;
    private List<OrderResponseDTO> deliveredOrders;
    private List<OrderResponseDTO> pendingOrders;
    private List<OrderResponseDTO> takeOnOrders;
}
