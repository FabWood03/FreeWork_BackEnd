package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.order.OrderRequestDTO;
import org.elis.progettoing.dto.response.order.OrderResponseDTO;
import org.elis.progettoing.mapper.definition.OrderMapper;
import org.elis.progettoing.mapper.definition.OrderProductMapper;
import org.elis.progettoing.models.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * This class implements the OrderMapper interface and provides the mapping functionality
 * between DTOs and entities related to Order, and OrderProduct.
 * It converts OrderRequestDTO to Order, Order to OrderResponseDTO,
 * and also maps various other related DTOs.
 */
@Component
public class OrderMapperImpl implements OrderMapper {
    private final OrderProductMapper orderProductMapper;

    /**
     * Constructs a new OrderMapperImpl with the specified OrderProductMapper.
     *
     * @param orderProductMapper the OrderProductMapper to be used for mapping
     */
    public OrderMapperImpl(OrderProductMapper orderProductMapper) {
        this.orderProductMapper = orderProductMapper;
    }

    /**
     * Converts an OrderRequestDTO to an Order entity.
     *
     * @param orderRequestDTO the OrderRequestDTO containing the order data
     * @return the Order entity populated with data from the DTO, or null if the DTO is null
     */
    @Override
    public Order orderRequestDTOToOrder(OrderRequestDTO orderRequestDTO) {
        if (orderRequestDTO == null) {
            return null;
        }

        Order order = new Order();
        order.setTotalPrice(orderRequestDTO.getTotalPrice());
        return order;

    }

    /**
     * Converts an Order entity to an OrderResponseDTO.
     *
     * @param order the Order entity containing the order data
     * @return the OrderResponseDTO populated with data from the entity, or null if the entity is null
     */
    @Override
    public OrderResponseDTO orderToOrderResponseDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(order.getId());
        orderResponseDTO.setTotalPrice(order.getTotalPrice());
        orderResponseDTO.setPurchaseDate(order.getOrderDate());
        orderResponseDTO.setOrderProducts(orderProductMapper.orderProductListToOrderProductResponseList(order.getOrderProducts()));
        orderResponseDTO.setBuyerName(order.getBuyer().getName());
        orderResponseDTO.setBuyerSurname(order.getBuyer().getSurname());
        orderResponseDTO.setBuyerPhoto(order.getBuyer().getUrlUserPhoto());
        return orderResponseDTO;
    }

    /**
     * Converts a list of Order entities to a list of OrderResponseDTOs.
     *
     * @param order the list of Order entities to be converted
     * @return the list of OrderResponseDTOs populated with data from the entities, or an empty list if the entities list is null
     */
    @Override
    public List<OrderResponseDTO> orderListToOrderResponseList(List<Order> order) {
        if (order == null) {
            return Collections.emptyList();
        }

        return order.stream().map(this::orderToOrderResponseDTO).toList();
    }
}
