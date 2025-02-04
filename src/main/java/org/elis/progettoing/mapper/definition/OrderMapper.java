package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.request.order.OrderRequestDTO;
import org.elis.progettoing.dto.response.order.OrderResponseDTO;
import org.elis.progettoing.models.Order;

import java.util.List;

/**
 * Interface for mapping between Order entities and their respective DTOs.
 * This interface defines methods for converting an Order entity to an OrderResponseDTO,
 * and converting an OrderRequestDTO to an Order entity.
 */
public interface OrderMapper {

    /**
     * Converts an orderRequestDTO to an Order entity.
     *
     * @param orderRequestDTO the OrderRequestDTO to be converted
     * @return the Order entity populated with data from the OrderRequestDTO
     */
    Order orderRequestDTOToOrder(OrderRequestDTO orderRequestDTO);

    /**
     * Converts an Order entity to an OrderResponseDTO.
     *
     * @param order the Order entity to be converted
     * @return the OrderResponseDTO populated with data from the Order entity
     */
    OrderResponseDTO orderToOrderResponseDTO(Order order);

    /**
     * Converts a list of Order entities to a list of OrderResponseDTOs.
     *
     * @param order the list of Order entities to be converted
     * @return the list of OrderResponseDTOs populated with data from the list of Order entities
     */
    List<OrderResponseDTO> orderListToOrderResponseList(List<Order> order);
}
