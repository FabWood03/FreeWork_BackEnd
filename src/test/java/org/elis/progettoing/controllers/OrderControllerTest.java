package org.elis.progettoing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elis.progettoing.dto.request.order.OrderFilterRequest;
import org.elis.progettoing.dto.request.order.OrderRequestDTO;
import org.elis.progettoing.dto.response.order.FilteredOrdersResponse;
import org.elis.progettoing.dto.response.order.OrderProductResponseDTO;
import org.elis.progettoing.dto.response.order.OrderResponseDTO;
import org.elis.progettoing.service.definition.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateOrder() throws Exception {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setCartId(1L);
        orderRequestDTO.setTotalPrice(5000L);
        orderRequestDTO.setDescription("Order description");

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setPurchaseDate(LocalDateTime.now());
        orderResponseDTO.setOrderProducts(Collections.emptyList());
        orderResponseDTO.setTotalPrice(5000L);

        when(orderService.createOrder(Mockito.any(OrderRequestDTO.class))).thenReturn(orderResponseDTO);

        mockMvc.perform(post("/api/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(5000L))
                .andExpect(jsonPath("$.orderProducts").isEmpty());
    }

    @Test
    void testGetOrderByUser() throws Exception {
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setPurchaseDate(LocalDateTime.now());
        orderResponseDTO.setOrderProducts(Collections.emptyList());
        orderResponseDTO.setTotalPrice(5000L);

        when(orderService.getOrderByUser()).thenReturn(List.of(orderResponseDTO));

        mockMvc.perform(get("/api/order/getOrderByUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].totalPrice").value(5000L))
                .andExpect(jsonPath("$[0].orderProducts").isEmpty());
    }

    @Test
    void getFilteredOrders_ReturnsOrderList_WhenOrdersExist() throws Exception {
        OrderFilterRequest orderFilterRequest = new OrderFilterRequest();
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setPurchaseDate(LocalDateTime.now());
        orderResponseDTO.setOrderProducts(Collections.emptyList());
        orderResponseDTO.setTotalPrice(5000L);

        when(orderService.getOrderFiltered(Mockito.any(OrderFilterRequest.class))).thenReturn(List.of(orderResponseDTO));

        mockMvc.perform(post("/api/order/getFilteredOrders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderFilterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getReceivedOrdersBySeller_ReturnsOrderList() throws Exception {
        List<OrderResponseDTO> orders = List.of(new OrderResponseDTO(), new OrderResponseDTO());
        when(orderService.getReceivedOrdersBySeller()).thenReturn(orders);

        mockMvc.perform(get("/api/order/getReceivedOrdersBySeller"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getReceivedOrdersBySeller_ReturnsEmptyList() throws Exception {
        when(orderService.getReceivedOrdersBySeller()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/order/getReceivedOrdersBySeller"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getDelayedOrdersBySeller_ReturnsOrderList() throws Exception {
        List<OrderResponseDTO> orders = List.of(new OrderResponseDTO(), new OrderResponseDTO());
        when(orderService.getDelayedOrdersBySeller()).thenReturn(orders);

        mockMvc.perform(get("/api/order/getDelayedOrdersBySeller"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getDeliveredOrdersBySeller_ReturnsOrderList() throws Exception {
        List<OrderResponseDTO> orders = List.of(new OrderResponseDTO(), new OrderResponseDTO());
        when(orderService.getDeliveredOrdersBySeller()).thenReturn(orders);

        mockMvc.perform(get("/api/order/getDeliveredOrdersBySeller"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getRefusedOrdersBySeller_ReturnsOrderList() throws Exception {
        List<OrderResponseDTO> orders = List.of(new OrderResponseDTO(), new OrderResponseDTO());
        when(orderService.getRefusedOrdersBySeller()).thenReturn(orders);

        mockMvc.perform(get("/api/order/getRefusedOrdersBySeller"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getTakeOnOrdersBySeller_ReturnsOrderList() throws Exception {
        List<OrderResponseDTO> orders = List.of(new OrderResponseDTO(), new OrderResponseDTO());
        when(orderService.getTakeOnOrdersBySeller()).thenReturn(orders);

        mockMvc.perform(get("/api/order/getTakeOnOrdersBySeller"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getPendingOrdersBySeller_ReturnsOrderList() throws Exception {
        List<OrderResponseDTO> orders = List.of(new OrderResponseDTO(), new OrderResponseDTO());
        when(orderService.getPendingOrdersBySeller()).thenReturn(orders);

        mockMvc.perform(get("/api/order/getPendingOrdersBySeller"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void acceptSingleOrderProduct_ReturnsOrderResponse() throws Exception {
        OrderResponseDTO orderResponse = new OrderResponseDTO();
        when(orderService.acceptSingleOrderProduct(1L)).thenReturn(orderResponse);

        mockMvc.perform(post("/api/order/acceptSingleOrderProduct")
                        .param("orderProductId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7));
    }

    @Test
    void refuseSingleOrderProduct_ReturnsOrderResponse() throws Exception {
        OrderResponseDTO orderResponse = new OrderResponseDTO();
        when(orderService.refuseSingleOrderProduct(1L)).thenReturn(orderResponse);

        mockMvc.perform(post("/api/order/refuseSingleOrderProduct")
                        .param("orderProductId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7));
    }

    @Test
    void getOrderProductById_ReturnsOrderProductResponse() throws Exception {
        OrderProductResponseDTO orderProductResponse = new OrderProductResponseDTO();
        when(orderService.getOrderProductById(1L)).thenReturn(orderProductResponse);

        mockMvc.perform(get("/api/order/getOrderProductById")
                        .param("orderProductId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(14));
    }

    @Test
    void deliveryResponse_ReturnsUpdatedOrderResponse_WhenResponseIsValid() throws Exception {
        OrderResponseDTO orderResponse = new OrderResponseDTO();
        when(orderService.deliveryResponse(1L, "Delivered")).thenReturn(orderResponse);

        mockMvc.perform(post("/api/order/deliveryResponse")
                        .param("orderProductId", "1")
                        .param("deliveryResponse", "Delivered"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7));
    }

    @Test
    void getFilteredOrdersBySeller_ReturnsFilteredOrders() throws Exception {
        OrderFilterRequest filterRequest = new OrderFilterRequest();
        FilteredOrdersResponse response = new FilteredOrdersResponse();
        when(orderService.getFilteredOrdersBySeller(filterRequest)).thenReturn(response);

        mockMvc.perform(post("/api/order/getFilteredOrdersBySeller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));
    }
}
