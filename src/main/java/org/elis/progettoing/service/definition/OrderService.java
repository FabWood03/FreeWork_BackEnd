package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.request.order.OrderFilterRequest;
import org.elis.progettoing.dto.request.order.OrderRequestDTO;
import org.elis.progettoing.dto.response.order.FilteredOrdersResponse;
import org.elis.progettoing.dto.response.order.OrderProductResponseDTO;
import org.elis.progettoing.dto.response.order.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO);

    List<OrderResponseDTO> getOrderByUser();

    List<OrderResponseDTO> getOrderFiltered(OrderFilterRequest orderFilterRequest);

    List<OrderResponseDTO> getReceivedOrdersBySeller();

    List<OrderResponseDTO> getPendingOrdersBySeller();

    List<OrderResponseDTO> getDeliveredOrdersBySeller();

    List<OrderResponseDTO> getTakeOnOrdersBySeller();

    List<OrderResponseDTO> getRefusedOrdersBySeller();

    List<OrderResponseDTO> getDelayedOrdersBySeller();

    OrderResponseDTO acceptSingleOrderProduct(long orderProductId);

    OrderResponseDTO refuseSingleOrderProduct(long orderProductId);

    OrderProductResponseDTO getOrderProductById(long orderProductId);

    FilteredOrdersResponse getFilteredOrdersBySeller(OrderFilterRequest orderFilterRequest);

    OrderResponseDTO deliveryResponse(long orderProductId, String response);
}
