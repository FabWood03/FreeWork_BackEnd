package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.order.OrderRequestDTO;
import org.elis.progettoing.dto.response.order.OrderResponseDTO;
import org.elis.progettoing.mapper.definition.OrderMapper;
import org.elis.progettoing.mapper.definition.OrderProductMapper;
import org.elis.progettoing.models.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class OrderMapperImpl implements OrderMapper {
    private final OrderProductMapper orderProductMapper;

    public OrderMapperImpl(OrderProductMapper orderProductMapper) {
        this.orderProductMapper = orderProductMapper;
    }

    @Override
    public Order orderRequestDTOToOrder(OrderRequestDTO orderRequestDTO) {
        if (orderRequestDTO == null) {
            return null;
        }

        Order order = new Order();
        order.setTotalPrice(orderRequestDTO.getTotalPrice());
        return order;

    }

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

    @Override
    public List<OrderResponseDTO> orderListToOrderResponseList(List<Order> order) {
        if (order == null) {
            return Collections.emptyList();
        }

        return order.stream().map(this::orderToOrderResponseDTO).toList();
    }
}
