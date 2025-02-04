package org.elis.progettoing.controllers;

import jakarta.validation.Valid;
import org.elis.progettoing.dto.request.order.OrderFilterRequest;
import org.elis.progettoing.dto.request.order.OrderRequestDTO;
import org.elis.progettoing.dto.response.order.FilteredOrdersResponse;
import org.elis.progettoing.dto.response.order.OrderProductResponseDTO;
import org.elis.progettoing.dto.response.order.OrderResponseDTO;
import org.elis.progettoing.service.definition.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing orders.
 * Provides endpoints to create, update, delete, and retrieve order data.
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    /**
     * Constructs an instance of {@code OrderController}.
     *
     * @param orderService the service managing order-related business logic.
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Endpoint to create a new order.
     *
     * @param orderRequestDTO the request data containing order details to create.
     * @return a {@link ResponseEntity} containing the created {@link OrderResponseDTO} and HTTP status 201 (Created).
     */
    @PostMapping("/create")
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        return new ResponseEntity<>(orderService.createOrder(orderRequestDTO), HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve orders by the current user.
     *
     * @return a {@link ResponseEntity} containing a list of {@link OrderResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getOrderByUser")
    public ResponseEntity<List<OrderResponseDTO>> getOrderByUser() {
        return new ResponseEntity<>(orderService.getOrderByUser(), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve filtered orders.
     *
     * @param orderFilterRequest the request data containing filter criteria.
     * @return a {@link ResponseEntity} containing a list of {@link OrderResponseDTO} and HTTP status 200 (OK).
     */
    @PostMapping("/getFilteredOrders")
    public ResponseEntity<List<OrderResponseDTO>> getFilteredOrders(@RequestBody OrderFilterRequest orderFilterRequest) {
        return new ResponseEntity<>(orderService.getOrderFiltered(orderFilterRequest), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve received orders by the seller.
     *
     * @return a {@link ResponseEntity} containing a list of {@link OrderResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getReceivedOrdersBySeller")
    public ResponseEntity<List<OrderResponseDTO>> getReceivedOrdersBySeller() {
        return new ResponseEntity<>(orderService.getReceivedOrdersBySeller(), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve delayed orders by the seller.
     *
     * @return a {@link ResponseEntity} containing a list of {@link OrderResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getDelayedOrdersBySeller")
    public ResponseEntity<List<OrderResponseDTO>> getDelayedOrdersBySeller() {
        return new ResponseEntity<>(orderService.getDelayedOrdersBySeller(), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve delivered orders by the seller.
     *
     * @return a {@link ResponseEntity} containing a list of {@link OrderResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getDeliveredOrdersBySeller")
    public ResponseEntity<List<OrderResponseDTO>> getDeliveredOrdersBySeller() {
        return new ResponseEntity<>(orderService.getDeliveredOrdersBySeller(), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve refused orders by the seller.
     *
     * @return a {@link ResponseEntity} containing a list of {@link OrderResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getRefusedOrdersBySeller")
    public ResponseEntity<List<OrderResponseDTO>> getRefusedOrdersBySeller() {
        return new ResponseEntity<>(orderService.getRefusedOrdersBySeller(), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve take-on orders by the seller.
     *
     * @return a {@link ResponseEntity} containing a list of {@link OrderResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getTakeOnOrdersBySeller")
    public ResponseEntity<List<OrderResponseDTO>> getTakeOnOrdersBySeller() {
        return new ResponseEntity<>(orderService.getTakeOnOrdersBySeller(), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve pending orders by the seller.
     *
     * @return a {@link ResponseEntity} containing a list of {@link OrderResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getPendingOrdersBySeller")
    public ResponseEntity<List<OrderResponseDTO>> getPendingOrdersBySeller() {
        return new ResponseEntity<>(orderService.getPendingOrdersBySeller(), HttpStatus.OK);
    }

    /**
     * Endpoint to accept a single order product.
     *
     * @param orderProductId the ID of the order product to accept.
     * @return a {@link ResponseEntity} containing the updated {@link OrderResponseDTO} and HTTP status 200 (OK).
     */
    @PostMapping("/acceptSingleOrderProduct")
    public ResponseEntity<OrderResponseDTO> acceptSingleOrderProduct(@RequestParam("orderProductId") long orderProductId) {
        return new ResponseEntity<>(orderService.acceptSingleOrderProduct(orderProductId), HttpStatus.OK);
    }

    /**
     * Endpoint to refuse a single order product.
     *
     * @param orderProductId the ID of the order product to refuse.
     * @return a {@link ResponseEntity} containing the updated {@link OrderResponseDTO} and HTTP status 200 (OK).
     */
    @PostMapping("/refuseSingleOrderProduct")
    public ResponseEntity<OrderResponseDTO> refuseSingleOrderProduct(@RequestParam("orderProductId") long orderProductId) {
        return new ResponseEntity<>(orderService.refuseSingleOrderProduct(orderProductId), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve an order product by its ID.
     *
     * @param orderProductId the ID of the order product to retrieve.
     * @return a {@link ResponseEntity} containing the {@link OrderProductResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getOrderProductById")
    public ResponseEntity<OrderProductResponseDTO> getOrderProductById(@RequestParam("orderProductId") long orderProductId) {
        return new ResponseEntity<>(orderService.getOrderProductById(orderProductId), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve filtered orders by the seller.
     *
     * @param orderFilterRequest the request data containing filter criteria.
     * @return a {@link ResponseEntity} containing the {@link FilteredOrdersResponse} and HTTP status 200 (OK).
     */
    @PostMapping("/getFilteredOrdersBySeller")
    public ResponseEntity<FilteredOrdersResponse> getFilteredOrdersBySeller(@RequestBody OrderFilterRequest orderFilterRequest) {
        return new ResponseEntity<>(orderService.getFilteredOrdersBySeller(orderFilterRequest), HttpStatus.OK);
    }

    /**
     * Endpoint to deliver a product.
     *
     * @param orderProductId the ID of the order product to deliver.
     * @param response the response to the delivery request.
     * @return a {@link ResponseEntity} containing the updated {@link OrderResponseDTO} and HTTP status 200 (OK).
     */
    @PostMapping("/deliveryResponse")
    public ResponseEntity<OrderResponseDTO> deliveryResponse(@RequestParam("orderProductId") long orderProductId, @RequestParam("deliveryResponse") String response) {
        return new ResponseEntity<>(orderService.deliveryResponse(orderProductId, response), HttpStatus.OK);
    }
}