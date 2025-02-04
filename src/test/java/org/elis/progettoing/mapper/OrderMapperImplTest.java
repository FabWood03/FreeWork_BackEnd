package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.request.order.OrderRequestDTO;
import org.elis.progettoing.dto.response.order.OrderResponseDTO;
import org.elis.progettoing.mapper.implementation.OrderMapperImpl;
import org.elis.progettoing.mapper.implementation.OrderProductMapperImpl;
import org.elis.progettoing.models.Order;
import org.elis.progettoing.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderMapperImplTest {

    @InjectMocks
    private OrderMapperImpl orderMapper;

    @Mock
    private OrderProductMapperImpl orderProductMapper;

    @Test
    void orderRequestDTOToOrder_ReturnsOrder_WhenOrderRequestDTOIsValid() {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setTotalPrice(100);

        Order order = orderMapper.orderRequestDTOToOrder(orderRequestDTO);

        assertNotNull(order);
        assertEquals(100.0, order.getTotalPrice());
    }

    @Test
    void orderRequestDTOToOrder_ReturnsNull_WhenOrderRequestDTOIsNull() {
        Order order = orderMapper.orderRequestDTOToOrder(null);

        assertNull(order);
    }

    @Test
    void orderToOrderResponseDTO_ReturnsOrderResponseDTO_WhenOrderIsValid() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalPrice(100);
        order.setOrderDate(LocalDateTime.now());
        User buyer = new User();
        buyer.setName("John");
        buyer.setSurname("Doe");
        buyer.setUrlUserPhoto("photo_url");
        order.setBuyer(buyer);
        order.setOrderProducts(Collections.emptyList());

        when(orderProductMapper.orderProductListToOrderProductResponseList(anyList())).thenReturn(Collections.emptyList());

        OrderResponseDTO orderResponseDTO = orderMapper.orderToOrderResponseDTO(order);

        assertNotNull(orderResponseDTO);
        assertEquals(1L, orderResponseDTO.getId());
        assertEquals(100.0, orderResponseDTO.getTotalPrice());
        assertEquals("John", orderResponseDTO.getBuyerName());
        assertEquals("Doe", orderResponseDTO.getBuyerSurname());
        assertEquals("photo_url", orderResponseDTO.getBuyerPhoto());
    }

    @Test
    void orderToOrderResponseDTO_ReturnsNull_WhenOrderIsNull() {
        OrderResponseDTO orderResponseDTO = orderMapper.orderToOrderResponseDTO(null);

        assertNull(orderResponseDTO);
    }

    @Test
    void orderListToOrderResponseList_ReturnsEmptyList_WhenOrderListIsNull() {
        List<OrderResponseDTO> orderResponseDTOList = orderMapper.orderListToOrderResponseList(null);

        assertNotNull(orderResponseDTOList);
        assertTrue(orderResponseDTOList.isEmpty());
    }

    @Test
    void orderListToOrderResponseList_ReturnsOrderResponseDTOList_WhenOrderListIsValid() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalPrice(100);
        order.setOrderDate(LocalDateTime.now());
        User buyer = new User();
        buyer.setName("John");
        buyer.setSurname("Doe");
        buyer.setUrlUserPhoto("photo_url");
        order.setBuyer(buyer);
        order.setOrderProducts(Collections.emptyList());

        when(orderProductMapper.orderProductListToOrderProductResponseList(anyList())).thenReturn(Collections.emptyList());

        List<OrderResponseDTO> orderResponseDTOList = orderMapper.orderListToOrderResponseList(List.of(order));

        assertNotNull(orderResponseDTOList);
        assertEquals(1, orderResponseDTOList.size());
        assertEquals(1L, orderResponseDTOList.getFirst().getId());
    }
}
