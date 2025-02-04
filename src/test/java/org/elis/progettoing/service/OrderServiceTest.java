package org.elis.progettoing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.elis.progettoing.dto.request.order.OrderFilterRequest;
import org.elis.progettoing.dto.request.order.OrderRequestDTO;
import org.elis.progettoing.dto.response.order.FilteredOrdersResponse;
import org.elis.progettoing.dto.response.order.OrderProductResponseDTO;
import org.elis.progettoing.dto.response.order.OrderResponseDTO;
import org.elis.progettoing.enumeration.OrderProductStatus;
import org.elis.progettoing.enumeration.Role;
import org.elis.progettoing.exception.entity.EntityEditException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.implementation.OrderMapperImpl;
import org.elis.progettoing.mapper.implementation.OrderProductMapperImpl;
import org.elis.progettoing.models.Order;
import org.elis.progettoing.models.OrderProduct;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.ProductPackage;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.elis.progettoing.repository.OrderProductRepository;
import org.elis.progettoing.repository.OrderRepository;
import org.elis.progettoing.repository.PurchasedProductRepository;
import org.elis.progettoing.repository.ReviewRepository;
import org.elis.progettoing.service.implementation.EmailServiceImpl;
import org.elis.progettoing.service.implementation.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @Mock
    private PurchasedProductRepository purchasedProductRepository;

    @Mock
    private UsernamePasswordAuthenticationToken authentication;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OrderMapperImpl orderMapper;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private OrderProductMapperImpl orderProductMapper;

    @Mock
    private OrderFilterRequest orderFilterRequest;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Order> criteriaQuery;

    @Mock
    private Root<Order> root;

    @Mock
    private TypedQuery<Order> typedQuery;

    @InjectMocks
    private OrderServiceImpl orderService;

    OrderRequestDTO orderRequestDTO = new OrderRequestDTO();

    User user = new User();
    Order order = new Order();
    OrderProduct orderProduct = new OrderProduct();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        authentication = new UsernamePasswordAuthenticationToken(user, null);

        orderRequestDTO.setCartId(1L);
        orderRequestDTO.setDescription("{\"key\": \"value\"}");

        user.setId(1L);
        user.setName("Test");
        user.setSurname("User");
        user.setUrlUserPhoto("https://example.com");
        user.setEmail("example@gmail.com");
        user.setRole(Role.BUYER);
        user.setPassword("password");
        user.setOrders(List.of(order));
        user.setPurchasedProducts(new ArrayList<>());
        user.setFiscalCode("FSCMRA00A01A001A");

        orderProduct.setId(1L);
        orderProduct.setProduct(new Product());
        orderProduct.setStatus(OrderProductStatus.PENDING);

        order.setId(1L);
        order.setBuyer(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(100);
        order.setOrderProducts((Collections.singletonList(orderProduct)));
    }

    @Test
    void createOrder_success() throws JsonProcessingException {
        // Arrange
        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setCartId(1L);
        requestDTO.setDescription("{\"1\":\"Description for product 1\"}");

        List<PurchasedProduct> purchasedProducts = List.of(createPurchasedProduct(1L), createPurchasedProduct(2L));
        Map<Long, String> descriptions = Map.of(1L, "Description for product 1", 2L, "Description for product 2");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(orderMapper.orderRequestDTOToOrder(requestDTO)).thenReturn(order);
        when(purchasedProductRepository.findByCartId(requestDTO.getCartId())).thenReturn(purchasedProducts);
        when(objectMapper.readValue(requestDTO.getDescription(), Map.class)).thenReturn(descriptions);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.orderToOrderResponseDTO(order)).thenReturn(new OrderResponseDTO());

        // Act
        OrderResponseDTO responseDTO = orderService.createOrder(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        verify(orderRepository, times(1)).save(order);
        verify(purchasedProductRepository, times(1)).deletePurchasedProductsByBuyerId(user.getId());
    }

    @Test
    void createOrder_saveFails_throwsEntityEditException() throws JsonProcessingException {
        // Arrange
        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setCartId(1L);
        requestDTO.setDescription("{\"1\":\"Description for product 1\"}");

        List<PurchasedProduct> purchasedProducts = List.of(createPurchasedProduct(1L));
        Map<Long, String> descriptions = Map.of(1L, "Description for product 1");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(orderMapper.orderRequestDTOToOrder(requestDTO)).thenReturn(order);
        when(purchasedProductRepository.findByCartId(requestDTO.getCartId())).thenReturn(purchasedProducts);
        when(objectMapper.readValue(requestDTO.getDescription(), Map.class)).thenReturn(descriptions);
        doThrow(RuntimeException.class).when(orderRepository).save(any(Order.class));

        // Act & Assert
        EntityEditException exception = assertThrows(EntityEditException.class, () -> orderService.createOrder(requestDTO));
        assertEquals("Si è verificato un errore nell'aggiornamento dell'entità id con ordine = 1.", exception.getMessage());
        verify(orderRepository, times(1)).save(order);
    }

    private PurchasedProduct createPurchasedProduct(long id) {
        PurchasedProduct purchasedProduct = new PurchasedProduct();
        purchasedProduct.setId(id);
        purchasedProduct.setProduct(new Product());
        purchasedProduct.setSelectedPackage(new ProductPackage());
        // Mocking product and selectedPackage with simple placeholders
        return purchasedProduct;
    }

    @Test
    void getOrderByUser_ShouldReturnEmptyListWhenNoOrders() {
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(orderRepository.findByBuyer(user)).thenReturn(Collections.emptyList());

        List<OrderResponseDTO> result = orderService.getOrderByUser();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(orderRepository, times(1)).findByBuyer(any(User.class));
        verify(orderMapper, times(0)).orderListToOrderResponseList(anyList());
    }

    @Test
    void getRefusedOrdersBySeller_withNoRefusedOrders_returnsEmptyList() {
        when(orderRepository.findAllBySellerAndProductStatus(user, OrderProductStatus.REFUSED)).thenReturn(Collections.emptyList());
        when(orderMapper.orderListToOrderResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<OrderResponseDTO> result = orderService.getRefusedOrdersBySeller();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getReceivedOrdersBySeller_withNoRefusedOrders_returnsEmptyList() {
        when(orderMapper.orderListToOrderResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<OrderResponseDTO> result = orderService.getReceivedOrdersBySeller();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getPendingOrdersBySeller_withNoRefusedOrders_returnsEmptyList() {
        when(orderRepository.findAllBySellerAndProductStatus(user, OrderProductStatus.PENDING)).thenReturn(Collections.emptyList());
        when(orderMapper.orderListToOrderResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<OrderResponseDTO> result = orderService.getPendingOrdersBySeller();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getDeliveredOrdersBySeller_withNoRefusedOrders_returnsEmptyList() {
        when(orderRepository.findAllBySellerAndProductStatus(user, OrderProductStatus.DELIVERED)).thenReturn(Collections.emptyList());
        when(orderMapper.orderListToOrderResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<OrderResponseDTO> result = orderService.getDeliveredOrdersBySeller();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getDelayedOrdersBySeller_withNoRefusedOrders_returnsEmptyList() {
        when(orderRepository.findAllBySellerAndProductStatus(user, OrderProductStatus.LATE_DELIVERY)).thenReturn(Collections.emptyList());
        when(orderMapper.orderListToOrderResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<OrderResponseDTO> result = orderService.getDelayedOrdersBySeller();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTakeOnOrdersBySeller_withNoRefusedOrders_returnsEmptyList() {
        when(orderRepository.findAllBySellerAndProductStatus(user, OrderProductStatus.LATE_DELIVERY)).thenReturn(Collections.emptyList());
        when(orderMapper.orderListToOrderResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<OrderResponseDTO> result = orderService.getTakeOnOrdersBySeller();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOrderByUser_ShouldSetHasReviewCorrectly() {
        // Configurazione contesto di sicurezza
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        // Creazione di un prodotto e ordine di esempio
        Product product = new Product();
        product.setId(1L);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);

        Order order = new Order();
        order.setOrderProducts(Collections.singletonList(orderProduct));

        // Configurazione mock per orderRepository
        when(orderRepository.findByBuyer(user)).thenReturn(Collections.singletonList(order));

        // Estrae gli ID dei prodotti dall'ordine (simula la logica del servizio)
        Set<Long> productIds = order.getOrderProducts().stream()
                .map(op -> op.getProduct().getId())
                .collect(Collectors.toSet());

        // Configurazione mock per reviewRepository: il prodotto è stato recensito
        Set<Long> reviewedProductIds = new HashSet<>(Collections.singleton(1L));
        when(reviewRepository.findReviewedProductIdsByUser(user.getId(), productIds))
                .thenReturn(reviewedProductIds);

        // Configurazione OrderResponseDTO con OrderProductDTO
        OrderResponseDTO mockDto = new OrderResponseDTO();
        OrderProductResponseDTO opDto = new OrderProductResponseDTO();
        opDto.setProductId(1L); // Deve corrispondere all'ID del prodotto nell'ordine
        mockDto.setOrderProducts(Collections.singletonList(opDto));

        when(orderMapper.orderToOrderResponseDTO(order)).thenReturn(mockDto);

        // Esecuzione del metodo da testare
        List<OrderResponseDTO> result = orderService.getOrderByUser();

        // Verifiche
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Verifica che 'hasReview' sia stato impostato correttamente
        assertTrue(opDto.isHasReview()); // Verifica che sia true poiché l'ID 1L è nelle recensioni

        verify(orderRepository).findByBuyer(user);
        verify(reviewRepository).findReviewedProductIdsByUser(user.getId(), productIds);
        verify(orderMapper).orderToOrderResponseDTO(order);
    }

    @Test
    void deliveryResponse_ShouldUpdateStatusToDelivered_WhenOrderProductIsInProgress() {
        long orderProductId = 1L;
        String response = "Delivery confirmed";
        User buyer = new User();
        Order order = new Order();
        order.setBuyer(buyer);
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setStatus(OrderProductStatus.IN_PROGRESS);
        orderProduct.setOrder(order);

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.of(orderProduct));

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        when(orderMapper.orderToOrderResponseDTO(order)).thenReturn(orderResponseDTO);

        OrderResponseDTO result = orderService.deliveryResponse(orderProductId, response);

        assertNotNull(result);
        assertEquals(OrderProductStatus.DELIVERED, orderProduct.getStatus());
        verify(orderProductRepository, times(1)).save(orderProduct);
        verify(emailService, times(1)).sendDeliveryConfirmationEmail(buyer, orderProduct, response);
        verify(orderMapper, times(1)).orderToOrderResponseDTO(order);
    }

    @Test
    void deliveryResponse_ShouldThrowEntityNotFoundException_WhenOrderProductDoesNotExist() {
        long orderProductId = 1L;
        String response = "Delivery confirmed";

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.deliveryResponse(orderProductId, response));
    }

    @Test
    void deliveryResponse_ShouldThrowIllegalArgumentException_WhenOrderProductStatusIsNotInProgress() {
        long orderProductId = 1L;
        String response = "Delivery confirmed";
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setStatus(OrderProductStatus.PENDING);

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.of(orderProduct));

        assertThrows(IllegalArgumentException.class, () -> orderService.deliveryResponse(orderProductId, response));
    }

    @Test
    void deliveryResponse_ShouldThrowEntityEditException_WhenSaveFails() {
        long orderProductId = 1L;
        String response = "Delivery confirmed";
        User buyer = new User();
        Order order = new Order();
        order.setBuyer(buyer);
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setStatus(OrderProductStatus.IN_PROGRESS);
        orderProduct.setOrder(order);

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.of(orderProduct));
        doThrow(new RuntimeException("Save failed")).when(orderProductRepository).save(orderProduct);

        assertThrows(EntityEditException.class, () -> orderService.deliveryResponse(orderProductId, response));
    }

    @Test
    void acceptSingleOrderProduct_shouldUpdateStatusToInProgress_whenOrderProductIsPending() {
        // Arrange
        long orderProductId = 1L;
        User seller = new User();
        seller.setId(1L);

        Product product = new Product();
        product.setUser(seller);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.PENDING);

        Order order = new Order();
        orderProduct.setOrder(order);

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.of(orderProduct));
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        when(orderMapper.orderToOrderResponseDTO(order)).thenReturn(orderResponseDTO);

        // Act
        OrderResponseDTO result = orderService.acceptSingleOrderProduct(orderProductId);

        // Assert
        assertNotNull(result);
        assertEquals(OrderProductStatus.IN_PROGRESS, orderProduct.getStatus());
        verify(orderProductRepository, times(1)).save(orderProduct);
        verify(orderMapper, times(1)).orderToOrderResponseDTO(order);
    }

    @Test
    void acceptSingleOrderProduct_shouldThrowEntityNotFoundException_whenOrderProductDoesNotExist() {
        // Arrange
        long orderProductId = 1L;
        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> orderService.acceptSingleOrderProduct(orderProductId));
    }

    @Test
    void acceptSingleOrderProduct_shouldThrowIllegalArgumentException_whenSellerIsNotAuthorized() {
        // Arrange
        long orderProductId = 1L;
        User seller = new User();
        seller.setId(1L);

        User differentSeller = new User();
        differentSeller.setId(2L);

        Product product = new Product();
        product.setUser(differentSeller);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.PENDING);

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.of(orderProduct));
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> orderService.acceptSingleOrderProduct(orderProductId));
    }

    @Test
    void acceptSingleOrderProduct_shouldThrowIllegalArgumentException_whenOrderProductStatusIsNotPending() {
        // Arrange
        long orderProductId = 1L;
        User seller = new User();
        seller.setId(1L);

        Product product = new Product();
        product.setUser(seller);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.DELIVERED);

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.of(orderProduct));

        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> orderService.acceptSingleOrderProduct(orderProductId));
    }

    @Test
    void acceptSingleOrderProduct_shouldThrowEntityEditException_whenSaveFails() {
        // Arrange
        long orderProductId = 1L;
        User seller = new User();
        seller.setId(1L);

        Product product = new Product();
        product.setUser(seller);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.PENDING);

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.of(orderProduct));

        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        doThrow(new RuntimeException("Save failed")).when(orderProductRepository).save(orderProduct);

        // Act & Assert
        assertThrows(EntityEditException.class, () -> orderService.acceptSingleOrderProduct(orderProductId));
    }

    @Test
    void refuseSingleOrderProduct_shouldUpdateStatusToRefused_whenOrderProductIsPending() {
        long orderProductId = 1L;
        User seller = new User();
        seller.setId(1L);

        Product product = new Product();
        product.setUser(seller);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.PENDING);

        Order order = new Order();
        orderProduct.setOrder(order);

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.of(orderProduct));
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        when(orderMapper.orderToOrderResponseDTO(order)).thenReturn(orderResponseDTO);

        OrderResponseDTO result = orderService.refuseSingleOrderProduct(orderProductId);

        assertNotNull(result);
        assertEquals(OrderProductStatus.REFUSED, orderProduct.getStatus());
        verify(orderProductRepository, times(1)).save(orderProduct);
        verify(orderMapper, times(1)).orderToOrderResponseDTO(order);
    }

    @Test
    void refuseSingleOrderProduct_shouldThrowEntityNotFoundException_whenOrderProductDoesNotExist() {
        long orderProductId = 1L;
        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.refuseSingleOrderProduct(orderProductId));
    }

    @Test
    void refuseSingleOrderProduct_shouldThrowIllegalArgumentException_whenSellerIsNotAuthorized() {
        long orderProductId = 1L;
        User seller = new User();
        seller.setId(1L);

        User differentSeller = new User();
        differentSeller.setId(2L);

        Product product = new Product();
        product.setUser(differentSeller);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.PENDING);

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.of(orderProduct));
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        assertThrows(IllegalArgumentException.class, () -> orderService.refuseSingleOrderProduct(orderProductId));
    }

    @Test
    void refuseSingleOrderProduct_shouldThrowIllegalArgumentException_whenOrderProductStatusIsNotPending() {
        long orderProductId = 1L;
        User seller = new User();
        seller.setId(1L);

        Product product = new Product();
        product.setUser(seller);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.DELIVERED);

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.of(orderProduct));
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        assertThrows(IllegalArgumentException.class, () -> orderService.refuseSingleOrderProduct(orderProductId));
    }

    @Test
    void refuseSingleOrderProduct_shouldThrowEntityEditException_whenSaveFails() {
        long orderProductId = 1L;
        User seller = new User();
        seller.setId(1L);

        Product product = new Product();
        product.setUser(seller);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.PENDING);

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.of(orderProduct));
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);
        doThrow(new RuntimeException("Save failed")).when(orderProductRepository).save(orderProduct);

        assertThrows(EntityEditException.class, () -> orderService.refuseSingleOrderProduct(orderProductId));
    }

    @Test
    void getOrderProductById_shouldReturnOrderProductResponseDTO_whenOrderProductExists() {
        long orderProductId = 1L;
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);

        OrderProductResponseDTO orderProductResponseDTO = new OrderProductResponseDTO();
        orderProductResponseDTO.setId(orderProductId);

        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.of(orderProduct));
        when(orderProductMapper.orderProductToOrderProductResponseDTO(orderProduct)).thenReturn(orderProductResponseDTO);

        OrderProductResponseDTO result = orderService.getOrderProductById(orderProductId);

        assertNotNull(result);
        assertEquals(orderProductId, result.getId());
        verify(orderProductRepository, times(1)).findById(orderProductId);
        verify(orderProductMapper, times(1)).orderProductToOrderProductResponseDTO(orderProduct);
    }

    @Test
    void getOrderProductById_shouldThrowEntityNotFoundException_whenOrderProductDoesNotExist() {
        long orderProductId = 1L;
        when(orderProductRepository.findById(orderProductId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.getOrderProductById(orderProductId));
        verify(orderProductRepository, times(1)).findById(orderProductId);
        verifyNoInteractions(orderProductMapper);
    }

    @Test
    void testGetOrderFiltered_WithAllFilters() {
        // Creazione dell'utente seller (mock reale)
        User seller = new User();
        seller.setId(1L);

        // Configurazione del SecurityContext per il seller
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        // Mock della Criteria API
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Order.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Order.class)).thenReturn(root);

        // Configurazione del percorso per il join: Order -> orderProducts -> product -> user -> id
        Path<Object> orderProductsPath = mock(Path.class);
        Path<Object> productPath = mock(Path.class);
        Path<Object> userPath = mock(Path.class);
        Path<Object> userIdPath = mock(Path.class);

        when(root.get("orderProducts")).thenReturn(orderProductsPath);
        when(orderProductsPath.get("product")).thenReturn(productPath);
        when(productPath.get("user")).thenReturn(userPath);
        when(userPath.get("id")).thenReturn(userIdPath);

        Predicate sellerPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(userIdPath, seller.getId())).thenReturn(sellerPredicate);

        // Mock dei predicati aggiuntivi
        List<Predicate> additionalPredicates = List.of(mock(Predicate.class));
        Predicate combinedPredicate = mock(Predicate.class);
        when(criteriaBuilder.and(sellerPredicate, criteriaBuilder.and(additionalPredicates.toArray(new Predicate[0]))))
                .thenReturn(combinedPredicate);

        // Configuriamo la where della query
        when(criteriaQuery.where(combinedPredicate)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        // Creazione dei dummy dati: due ordini con OrderProduct in cui viene impostato il prodotto
        // Impostazione di un dummy prodotto per il primo OrderProduct
        Product dummyProduct1 = new Product();
        dummyProduct1.setTitle("Ticket 1");
        dummyProduct1.setUser(seller);  // Il prodotto appartiene al seller

        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setStatus(OrderProductStatus.PENDING);
        orderProduct1.setProduct(dummyProduct1);

        Order order1 = new Order();
        order1.setId(10L);
        order1.setOrderProducts(List.of(orderProduct1));

        // Impostazione di un dummy prodotto per il secondo OrderProduct
        Product dummyProduct2 = new Product();
        dummyProduct2.setTitle("Ticket 2");
        dummyProduct2.setUser(seller);  // Anche qui il prodotto appartiene al seller

        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setStatus(OrderProductStatus.DELIVERED);
        orderProduct2.setProduct(dummyProduct2);

        Order order2 = new Order();
        order2.setId(20L);
        order2.setOrderProducts(List.of(orderProduct2));

        List<Order> orders = new ArrayList<>(Arrays.asList(order1, order2));
        when(typedQuery.getResultList()).thenReturn(orders);

        // Mock del mapper: mappa la lista di Order in una lista di OrderResponseDTO
        OrderResponseDTO orderDTO1 = new OrderResponseDTO();
        orderDTO1.setOrderProducts(new ArrayList<>());  // Inizializza la lista
        OrderResponseDTO orderDTO2 = new OrderResponseDTO();
        orderDTO2.setOrderProducts(new ArrayList<>());
        when(orderMapper.orderListToOrderResponseList(orders)).thenReturn(List.of(orderDTO1, orderDTO2));

        // Imposta alcuni parametri di filtro
        orderFilterRequest.setSearchText("ticket");
        orderFilterRequest.setDateRangeType("THIS_WEEK");

        // Esegue il metodo da testare
        FilteredOrdersResponse result = orderService.getFilteredOrdersBySeller(orderFilterRequest);

        // Asserzioni: il risultato non deve essere null e deve contenere 2 ordini
        assertNotNull(result);
        assertEquals(2, result.getAllOrders().size());

        // Verifica che alcuni metodi mock siano stati chiamati (ad es. il mapper)
        verify(criteriaBuilder, times(1)).createQuery(Order.class);
        verify(orderMapper, times(1)).orderListToOrderResponseList(orders);
    }

    @Test
    void getOrderFiltered() {
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Order.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Order.class)).thenReturn(root);

        Path<Object> orderPath = mock(Path.class);
        Path<Object> userPath = mock(Path.class);

        when(root.get("buyer")).thenReturn(orderPath);
        when(orderPath.get("id")).thenReturn(userPath);

        Predicate predicate = mock(Predicate.class);
        when(criteriaBuilder.equal(userPath, user.getId())).thenReturn(predicate);

        List<Predicate> additionalPredicates = List.of(mock(Predicate.class));

        Predicate combinedPredicate = mock(Predicate.class);
        when(criteriaBuilder.and(predicate, criteriaBuilder.and(additionalPredicates.toArray(new Predicate[0]))))
                .thenReturn(combinedPredicate);

        when(criteriaQuery.where(combinedPredicate)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        List<OrderResponseDTO> orderResponseDTOS = orderService.getOrderFiltered(orderFilterRequest);

        assertNotNull(orderResponseDTOS);
    }

    @Test
    void addSearchTextPredicate_shouldAddPredicates_whenSearchTextIsProvided() throws Exception {
        OrderFilterRequest request = new OrderFilterRequest();
        request.setSearchText("test");

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Root<Order> root = mock(Root.class);
        Join<Order, OrderProduct> orderProductJoin = mock(Join.class);

        Path<Object> idPath = mock(Path.class);
        Path<Object> path = mock(Path.class);
        Path<Object> namePath = mock(Path.class);
        Path<Object> surnamePath = mock(Path.class);
        Path<Object> emailPath = mock(Path.class);
        Path<Object> productPath = mock(Path.class);
        Path<Object> titlePath = mock(Path.class);

        when(root.get("id")).thenReturn(idPath);
        when(root.get("buyer")).thenReturn(path);
        when(path.get("name")).thenReturn(namePath);
        when(path.get("surname")).thenReturn(surnamePath);
        when(path.get("email")).thenReturn(emailPath);
        when(orderProductJoin.get("product")).thenReturn(productPath);
        when(productPath.get("title")).thenReturn(titlePath);

        Predicate idLike = mock(Predicate.class);
        Predicate nameLike = mock(Predicate.class);
        Predicate surnameLike = mock(Predicate.class);
        Predicate emailLike = mock(Predicate.class);
        Predicate titleLike = mock(Predicate.class);

        when(criteriaBuilder.like(eq(criteriaBuilder.lower(idPath.as(String.class))), eq("%test%"))).thenReturn(idLike);
        when(criteriaBuilder.like(eq(criteriaBuilder.lower(namePath.as(String.class))), eq("%test%"))).thenReturn(nameLike);
        when(criteriaBuilder.like(eq(criteriaBuilder.lower(surnamePath.as(String.class))), eq("%test%"))).thenReturn(surnameLike);
        when(criteriaBuilder.like(eq(criteriaBuilder.lower(emailPath.as(String.class))), eq("%test%"))).thenReturn(emailLike);
        when(criteriaBuilder.like(eq(criteriaBuilder.lower(titlePath.as(String.class))), eq("%test%"))).thenReturn(titleLike);

        List<Predicate> predicates = new ArrayList<>();

        Method method = OrderServiceImpl.class.getDeclaredMethod("addSearchTextPredicate", OrderFilterRequest.class, CriteriaBuilder.class, Root.class, List.class, Join.class);
        method.setAccessible(true);
        method.invoke(orderService, request, criteriaBuilder, root, predicates, orderProductJoin);

        assertEquals(1, predicates.size());
    }

    @Test
    void addDateRangePredicate_shouldAddPredicates_whenDateRangeTypeIsProvided() throws Exception {
        OrderFilterRequest request = new OrderFilterRequest();
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Root<Order> root = mock(Root.class);
        List<Predicate> predicates = new ArrayList<>();

        Path<LocalDateTime> orderDatePath = mock(Path.class);
        when(root.get("orderDate")).thenReturn((Path) orderDatePath);

        Predicate datePredicate = mock(Predicate.class);
        when(criteriaBuilder.between(eq(orderDatePath), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(datePredicate);

        Method method = OrderServiceImpl.class.getDeclaredMethod("addDateRangePredicate", OrderFilterRequest.class, CriteriaBuilder.class, Root.class, List.class);
        method.setAccessible(true);

        // Test for "THIS_WEEK"
        request.setDateRangeType("THIS_WEEK");
        method.invoke(orderService, request, criteriaBuilder, root, predicates);
        assertEquals(1, predicates.size());
        verify(criteriaBuilder, times(1)).between(eq(orderDatePath), any(LocalDateTime.class), any(LocalDateTime.class));
        reset(criteriaBuilder, root, orderDatePath, datePredicate);
        predicates.clear();

        // Test for "THIS_MONTH"
        request.setDateRangeType("THIS_MONTH");
        method.invoke(orderService, request, criteriaBuilder, root, predicates);
        assertEquals(1, predicates.size());
        verify(criteriaBuilder, times(1)).between(eq(null), any(LocalDateTime.class), any(LocalDateTime.class));
        reset(criteriaBuilder, root, orderDatePath, datePredicate);
        predicates.clear();

        // Test for "TODAY"
        request.setDateRangeType("TODAY");
        method.invoke(orderService, request, criteriaBuilder, root, predicates);
        assertEquals(1, predicates.size());
        verify(criteriaBuilder, times(1)).between(eq(null), any(LocalDateTime.class), any(LocalDateTime.class));
        reset(criteriaBuilder, root, orderDatePath, datePredicate);
        predicates.clear();

        // Test for "ALWAYS"
        request.setDateRangeType("ALWAYS");
        method.invoke(orderService, request, criteriaBuilder, root, predicates);
        assertEquals(0, predicates.size());
    }

    @Test
    void getReceivedOrdersBySeller_ShouldReturnOrdersForLoggedSeller() {
        User seller = new User();
        seller.setId(1L);
        Product product = new Product();
        product.setUser(seller);
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        Order order = new Order();
        order.setOrderProducts(List.of(orderProduct));

        when(orderRepository.findAll()).thenReturn(List.of(order));
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        List<OrderResponseDTO> result = orderService.getReceivedOrdersBySeller();

        assertNotNull(result);
        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).orderListToOrderResponseList(anyList());
    }

    @Test
    void pendingOrdersBySeller_ShouldReturnOrdersForLoggedSeller() {
        User seller = new User();
        seller.setId(1L);
        Product product = new Product();
        product.setUser(seller);
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.PENDING);
        Order order = new Order();
        order.setOrderProducts(List.of(orderProduct));

        when(orderRepository.findAll()).thenReturn(List.of(order));
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        List<OrderResponseDTO> result = orderService.getPendingOrdersBySeller();

        assertNotNull(result);
        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).orderListToOrderResponseList(anyList());
    }

    @Test
    void delayedOrdersBySeller_ShouldReturnOrdersForLoggedSeller() {
        User seller = new User();
        seller.setId(1L);
        Product product = new Product();
        product.setUser(seller);
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.LATE_DELIVERY);
        Order order = new Order();
        order.setOrderProducts(List.of(orderProduct));

        when(orderRepository.findAll()).thenReturn(List.of(order));
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        List<OrderResponseDTO> result = orderService.getDelayedOrdersBySeller();

        assertNotNull(result);
        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).orderListToOrderResponseList(anyList());
    }

    @Test
    void deliveredOrdersBySeller_ShouldReturnOrdersForLoggedSeller() {
        User seller = new User();
        seller.setId(1L);
        Product product = new Product();
        product.setUser(seller);
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.DELIVERED);
        Order order = new Order();
        order.setOrderProducts(List.of(orderProduct));

        when(orderRepository.findAll()).thenReturn(List.of(order));
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        List<OrderResponseDTO> result = orderService.getDeliveredOrdersBySeller();

        assertNotNull(result);
        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).orderListToOrderResponseList(anyList());
    }

    @Test
    void takeOnOrdersBySeller_ShouldReturnOrdersForLoggedSeller() {
        User seller = new User();
        seller.setId(1L);
        Product product = new Product();
        product.setUser(seller);
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.IN_PROGRESS);
        Order order = new Order();
        order.setOrderProducts(List.of(orderProduct));

        when(orderRepository.findAll()).thenReturn(List.of(order));
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        List<OrderResponseDTO> result = orderService.getTakeOnOrdersBySeller();

        assertNotNull(result);
        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).orderListToOrderResponseList(anyList());
    }

    @Test
    void refusedOrdersBySeller_ShouldReturnOrdersForLoggedSeller() {
        User seller = new User();
        seller.setId(1L);
        Product product = new Product();
        product.setUser(seller);
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setStatus(OrderProductStatus.REFUSED);
        Order order = new Order();
        order.setOrderProducts(List.of(orderProduct));

        when(orderRepository.findAll()).thenReturn(List.of(order));
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(seller);

        List<OrderResponseDTO> result = orderService.getRefusedOrdersBySeller();

        assertNotNull(result);
        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).orderListToOrderResponseList(anyList());
    }


}
