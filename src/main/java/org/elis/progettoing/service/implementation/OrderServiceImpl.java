package org.elis.progettoing.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.elis.progettoing.exception.entity.EntityEditException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.OrderMapper;
import org.elis.progettoing.mapper.definition.OrderProductMapper;
import org.elis.progettoing.models.Order;
import org.elis.progettoing.models.OrderProduct;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.elis.progettoing.repository.OrderProductRepository;
import org.elis.progettoing.repository.OrderRepository;
import org.elis.progettoing.repository.PurchasedProductRepository;
import org.elis.progettoing.repository.ReviewRepository;
import org.elis.progettoing.service.definition.EmailService;
import org.elis.progettoing.service.definition.OrderService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing orders.
 * Provides methods to create, update, delete, and retrieve order data.
 */
@Service
public class OrderServiceImpl implements OrderService {
    private final EntityManager entityManager;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final PurchasedProductRepository purchasedProductRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderProductMapper orderProductMapper;
    private final ReviewRepository reviewRepository;
    private final EmailService emailService;

    /**
     * Constructs an instance of {@code OrderServiceImpl}.
     *
     * @param entityManager            the {@link EntityManager} instance to manage entities.
     * @param orderMapper              the mapper for converting order-related entities and DTOs.
     * @param orderRepository          the repository for managing order entities.
     * @param purchasedProductRepository the repository for managing purchased product entities.
     * @param orderProductRepository   the repository for managing order product entities.
     * @param orderProductMapper       the mapper for converting order product-related entities and DTOs.
     * @param reviewRepository         the repository for managing review entities.
     * @param emailService             the service for sending emails.
     */
    public OrderServiceImpl(EntityManager entityManager, OrderMapper orderMapper, OrderRepository orderRepository, PurchasedProductRepository purchasedProductRepository, OrderProductRepository orderProductRepository, OrderProductMapper orderProductMapper, ReviewRepository reviewRepository, EmailService emailService) {
        this.entityManager = entityManager;
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.purchasedProductRepository = purchasedProductRepository;
        this.orderProductRepository = orderProductRepository;
        this.orderProductMapper = orderProductMapper;
        this.reviewRepository = reviewRepository;
        this.emailService = emailService;
    }

    /**
     * Creates a new order.
     *
     * @param orderRequestDTO the request data containing order details to create.
     * @return the created {@link OrderResponseDTO}.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Order order = orderMapper.orderRequestDTOToOrder(orderRequestDTO);
        order.setBuyer(user);
        order.setOrderDate(LocalDateTime.now());

        List<PurchasedProduct> purchasedProducts = purchasedProductRepository.findByCartId(orderRequestDTO.getCartId());

        if (purchasedProducts.isEmpty()) {
            throw new EntityNotFoundException("prodotti", "carrello", orderRequestDTO.getCartId());
        }

        // Decodifica della stringa JSON in una mappa
        ObjectMapper objectMapper = new ObjectMapper();
        Map<Long, String> descriptions;
        try {
            descriptions = objectMapper.readValue(orderRequestDTO.getDescription(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Formato JSON della descrizione non valido", e);
        }

        List<OrderProduct> orderProducts = new ArrayList<>();
        for (PurchasedProduct purchasedProduct : purchasedProducts) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProduct(purchasedProduct.getProduct());
            orderProduct.setSelectedPackage(purchasedProduct.getSelectedPackage());
            orderProduct.setOrder(order);
            orderProduct.setStatus(OrderProductStatus.PENDING);
            orderProduct.setReviewExist(false);

            // Associa la descrizione specifica al prodotto
            String productDescription = descriptions.getOrDefault(purchasedProduct.getId(), "");
            orderProduct.setDescription(productDescription);

            int deliveryTimeInDays = purchasedProduct.getSelectedPackage().getDeliveryTime();
            LocalDateTime estimatedDeliveryDate = (deliveryTimeInDays > 0)
                    ? LocalDateTime.now().plusDays(deliveryTimeInDays)
                    : LocalDateTime.now();
            orderProduct.setEstimatedDeliveryDate(estimatedDeliveryDate);

            orderProducts.add(orderProduct);
        }

        order.setOrderProducts(orderProducts);

        try {
            orderRepository.save(order);
        } catch (Exception e) {
            throw new EntityEditException("ordine", "id", order.getId());
        }

        try {
            purchasedProductRepository.deletePurchasedProductsByBuyerId(user.getId());
        } catch (Exception e) {
            throw new EntityEditException("prodotti del carrello", "id", orderRequestDTO.getCartId());
        }

        return orderMapper.orderToOrderResponseDTO(order);
    }

    /**
     * Retrieves all orders made by the logged-in user.
     *
     * @return a list of {@link OrderResponseDTO} containing the orders made by the user.
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrderByUser() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        List<Order> orders = orderRepository.findByBuyer(user);
        if (orders.isEmpty()) return Collections.emptyList();

        Set<Long> productIds = orders.stream()
                .flatMap(order -> order.getOrderProducts().stream())
                .map(op -> op.getProduct().getId())
                .collect(Collectors.toSet());

        Set<Long> reviewedProductIds = reviewRepository.findReviewedProductIdsByUser(user.getId(), productIds);

        return orders.stream()
                .sorted(Comparator.comparingLong(Order::getId).reversed())
                .map(order -> {
                    OrderResponseDTO dto = orderMapper.orderToOrderResponseDTO(order);
                    dto.getOrderProducts().forEach(op ->
                            op.setHasReview(reviewedProductIds.contains(op.getProductId()))
                    );
                    return dto;
                })
                .toList();
    }


    /**
     * Retrieves all orders received by seller.
     *
     * @return a list of {@link OrderResponseDTO} containing the orders received by seller.
     */
    @Override
    public List<OrderResponseDTO> getReceivedOrdersBySeller() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User loggedSeller = (User) authentication.getPrincipal();

        // Recupera tutti gli ordini.
        // In alternativa, se esiste un metodo repository che già filtra sugli orderProduct, potresti usarlo.
        List<Order> allOrders = orderRepository.findAll();

        // Lista in cui andremo ad accumulare gli ordini che contengono almeno un OrderProduct per il seller loggato.
        List<Order> ordersForSeller = new ArrayList<>();

        for (Order order : allOrders) {
            // Filtra la lista degli OrderProduct in base al seller del prodotto.
            List<OrderProduct> filteredOrderProducts = order.getOrderProducts().stream()
                    .filter(orderProduct -> orderProduct.getProduct().getUser().getId() == loggedSeller.getId())
                    .toList();

            if (!filteredOrderProducts.isEmpty()) {
                // Se l'ordine contiene almeno un OrderProduct per il seller loggato,
                // sostituisco la lista degli orderProducts con quella filtrata.
                order.setOrderProducts(filteredOrderProducts);
                ordersForSeller.add(order);
            }
        }

        ordersForSeller.sort(Comparator.comparingLong(Order::getId).reversed());

        // Se non ci sono ordini, restituisco una lista vuota.
        if (ordersForSeller.isEmpty()) {
            return Collections.emptyList();
        }

        // Converte gli Order in OrderResponseDTO tramite il mapper.
        return orderMapper.orderListToOrderResponseList(ordersForSeller);
    }

    /**
     * Retrieves all orders with status equal to PENDING received by seller.
     *
     * @return a list of {@link OrderResponseDTO} containing the orders received by seller.
     */
    @Override
    public List<OrderResponseDTO> getPendingOrdersBySeller() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User loggedSeller = (User) authentication.getPrincipal();

        List<Order> allOrders = orderRepository.findAll();

        List<Order> ordersForSeller = new ArrayList<>();

        for (Order order : allOrders) {
            List<OrderProduct> filteredOrderProducts = order.getOrderProducts().stream()
                    .filter(op -> op.getProduct().getUser().getId() == loggedSeller.getId()
                            && op.getStatus().equals(OrderProductStatus.PENDING))
                    .toList();

            if (!filteredOrderProducts.isEmpty()) {
                order.setOrderProducts(filteredOrderProducts);
                ordersForSeller.add(order);
            }
        }

        ordersForSeller.sort(Comparator.comparingLong(Order::getId).reversed());

        if (ordersForSeller.isEmpty()) {
            return Collections.emptyList();
        }

        return orderMapper.orderListToOrderResponseList(ordersForSeller);
    }

    /**
     * Retrieves all orders with status equal to PENDING received by seller.
     *
     * @return a list of {@link OrderResponseDTO} containing the orders received by seller.
     */
    @Override
    public List<OrderResponseDTO> getDeliveredOrdersBySeller() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User loggedSeller = (User) authentication.getPrincipal();

        List<Order> allOrders = orderRepository.findAll();

        List<Order> ordersForSeller = new ArrayList<>();

        for (Order order : allOrders) {
            List<OrderProduct> filteredOrderProducts = order.getOrderProducts().stream()
                    .filter(op -> op.getProduct().getUser().getId() == loggedSeller.getId()
                            && op.getStatus().equals(OrderProductStatus.DELIVERED))
                    .toList();

            if (!filteredOrderProducts.isEmpty()) {
                order.setOrderProducts(filteredOrderProducts);
                ordersForSeller.add(order);
            }
        }

        ordersForSeller.sort(Comparator.comparingLong(Order::getId).reversed());

        if (ordersForSeller.isEmpty()) {
            return Collections.emptyList();
        }

        return orderMapper.orderListToOrderResponseList(ordersForSeller);
    }

    /**
     * Retrieves all orders with status equal to IN_PROGRESS received by seller.
     *
     * @return a list of {@link OrderResponseDTO} containing the orders received by seller.
     */
    @Override
    public List<OrderResponseDTO> getTakeOnOrdersBySeller() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User loggedSeller = (User) authentication.getPrincipal();

        List<Order> allOrders = orderRepository.findAll();

        List<Order> ordersForSeller = new ArrayList<>();

        for (Order order : allOrders) {
            List<OrderProduct> filteredOrderProducts = order.getOrderProducts().stream()
                    .filter(op -> op.getProduct().getUser().getId() == loggedSeller.getId()
                            && op.getStatus().equals(OrderProductStatus.IN_PROGRESS))
                    .toList();

            if (!filteredOrderProducts.isEmpty()) {
                order.setOrderProducts(filteredOrderProducts);
                ordersForSeller.add(order);
            }
        }

        ordersForSeller.sort(Comparator.comparingLong(Order::getId).reversed());

        if (ordersForSeller.isEmpty()) {
            return Collections.emptyList();
        }

        return orderMapper.orderListToOrderResponseList(ordersForSeller);
    }

    /**
     * Retrieves all orders with status equal to REFUSED received by seller.
     *
     * @return a list of {@link OrderResponseDTO} containing the orders received by seller.
     */
    @Override
    public List<OrderResponseDTO> getRefusedOrdersBySeller() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User loggedSeller = (User) authentication.getPrincipal();

        List<Order> allOrders = orderRepository.findAll();

        List<Order> ordersForSeller = new ArrayList<>();

        for (Order order : allOrders) {
            List<OrderProduct> filteredOrderProducts = order.getOrderProducts().stream()
                    .filter(op -> op.getProduct().getUser().getId() == loggedSeller.getId()
                            && op.getStatus().equals(OrderProductStatus.REFUSED))
                    .toList();

            if (!filteredOrderProducts.isEmpty()) {
                order.setOrderProducts(filteredOrderProducts);
                ordersForSeller.add(order);
            }
        }

        ordersForSeller.sort(Comparator.comparingLong(Order::getId).reversed());

        if (ordersForSeller.isEmpty()) {
            return Collections.emptyList();
        }

        return orderMapper.orderListToOrderResponseList(ordersForSeller);
    }

    /**
     * Retrieves all orders with status equal to LATE_DELIVERY received by seller.
     *
     * @return a list of {@link OrderResponseDTO} containing the orders received by seller.
     */
    @Override
    public List<OrderResponseDTO> getDelayedOrdersBySeller() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User loggedSeller = (User) authentication.getPrincipal();

        List<Order> allOrders = orderRepository.findAll();

        List<Order> ordersForSeller = new ArrayList<>();

        for (Order order : allOrders) {
            List<OrderProduct> filteredOrderProducts = order.getOrderProducts().stream()
                    .sorted(Comparator.comparing(OrderProduct::getEstimatedDeliveryDate))
                    .filter(op -> op.getProduct().getUser().getId() == loggedSeller.getId()
                            && op.getStatus().equals(OrderProductStatus.LATE_DELIVERY))
                    .toList();

            if (!filteredOrderProducts.isEmpty()) {
                order.setOrderProducts(filteredOrderProducts);
                ordersForSeller.add(order);
            }
        }

        if (ordersForSeller.isEmpty()) {
            return Collections.emptyList();
        }

        return orderMapper.orderListToOrderResponseList(ordersForSeller);
    }

    @Override
    public OrderResponseDTO acceptSingleOrderProduct(long orderProductId) {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User seller = (User) authentication.getPrincipal();

        OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
                .orElseThrow(() -> new EntityNotFoundException("prodotto dell'ordine", "id", orderProductId));

        if (orderProduct.getProduct().getUser().getId() != seller.getId()) {
            throw new IllegalArgumentException("Il venditore non è autorizzato a modificare questo prodotto dell'ordine");
        }

        if (orderProduct.getStatus() != OrderProductStatus.PENDING) {
            throw new IllegalArgumentException("Lo stato del prodotto dell'ordine non è 'in attesa'");
        }

        orderProduct.setStatus(OrderProductStatus.IN_PROGRESS);

        try {
            orderProductRepository.save(orderProduct);
        } catch (Exception e) {
            throw new EntityEditException("prodotto dell'ordine", "id", orderProduct.getId());
        }

        return orderMapper.orderToOrderResponseDTO(orderProduct.getOrder());
    }

    @Override
    public OrderResponseDTO refuseSingleOrderProduct(long orderProductId) {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User seller = (User) authentication.getPrincipal();

        OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
                .orElseThrow(() -> new EntityNotFoundException("prodotto dell'ordine", "id", orderProductId));

        if (orderProduct.getProduct().getUser().getId() != seller.getId()) {
            throw new IllegalArgumentException("Il venditore non è autorizzato a modificare questo prodotto dell'ordine");
        }

        if (orderProduct.getStatus() != OrderProductStatus.PENDING) {
            throw new IllegalArgumentException("Lo stato del prodotto dell'ordine non è 'in attesa'");
        }

        orderProduct.setStatus(OrderProductStatus.REFUSED);

        try {
            orderProductRepository.save(orderProduct);
        } catch (Exception e) {
            throw new EntityEditException("prodotto dell'ordine", "id", orderProduct.getId());
        }

        return orderMapper.orderToOrderResponseDTO(orderProduct.getOrder());
    }

    @Override
    public OrderProductResponseDTO getOrderProductById(long orderProductId) {
        OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
                .orElseThrow(() -> new EntityNotFoundException("prodotto dell'ordine", "id", orderProductId));

        return orderProductMapper.orderProductToOrderProductResponseDTO(orderProduct);
    }

    @Override
    public FilteredOrdersResponse getFilteredOrdersBySeller(OrderFilterRequest orderFilterRequest) {
        // Ottieni l'utente loggato
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // Inizializza CriteriaBuilder e CriteriaQuery
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
        Root<Order> root = criteriaQuery.from(Order.class);

        // Predicato per filtrare gli OrderProduct il cui prodotto appartenga all'utente loggato.
        // NOTA: il percorso potrebbe variare in base al nome del campo nella tua entità Product.
        Predicate sellerPredicate = criteriaBuilder.equal(
                root.get("orderProducts").get("product").get("user").get("id"),
                user.getId()
        );

        // Costruisce eventuali altri predicati (ricerca per testo, intervallo date, ecc.)
        List<Predicate> predicates = buildOrderPredicates(orderFilterRequest, criteriaBuilder, root);

        // Combina il predicato sul seller con gli altri predicati
        if (!predicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(
                    sellerPredicate,
                    criteriaBuilder.and(predicates.toArray(new Predicate[0]))
            ));
        } else {
            criteriaQuery.where(sellerPredicate);
        }

        // Esegue la query
        TypedQuery<Order> query = entityManager.createQuery(criteriaQuery);
        List<Order> orders = query.getResultList();

        // Per ogni ordine, filtra la lista degli OrderProduct per mantenere soltanto quelli il cui prodotto appartenga all'utente loggato.
        // In questo modo, nell'ordine rimarranno solo i prodotti relativi al seller loggato.
        for (Order order : orders) {
            List<OrderProduct> filteredOrderProducts = order.getOrderProducts().stream()
                    .filter(op -> op.getProduct().getUser().getId() == user.getId())
                    .collect(Collectors.toList());
            order.setOrderProducts(filteredOrderProducts);
        }

        // Se necessario, puoi escludere dalla lista quegli ordini che, dopo il filtraggio, non contengono più alcun OrderProduct
        orders.removeIf(order -> order.getOrderProducts().isEmpty());

        // Mappa la lista di Order a DTO
        List<OrderResponseDTO> orderResponseDTOS = orderMapper.orderListToOrderResponseList(orders);

        // Raggruppa gli ordini per stato dei prodotti (le condizioni si basano sul campo status, ad esempio "REFUSED", "LATE_DELIVERY", ecc.)
        List<OrderResponseDTO> refusedOrders = orderResponseDTOS.stream()
                .filter(order -> order.getOrderProducts().stream()
                        .anyMatch(orderProduct -> orderProduct.getStatus().equals("REFUSED")))
                .toList();

        List<OrderResponseDTO> delayedOrders = orderResponseDTOS.stream()
                .filter(order -> order.getOrderProducts().stream()
                        .anyMatch(orderProduct -> orderProduct.getStatus().equals("LATE_DELIVERY")))
                .toList();

        List<OrderResponseDTO> deliveredOrders = orderResponseDTOS.stream()
                .filter(order -> order.getOrderProducts().stream()
                        .anyMatch(orderProduct -> orderProduct.getStatus().equals("DELIVERED")))
                .toList();

        List<OrderResponseDTO> pendingOrders = orderResponseDTOS.stream()
                .filter(order -> order.getOrderProducts().stream()
                        .anyMatch(orderProduct -> orderProduct.getStatus().equals("PENDING")))
                .toList();

        List<OrderResponseDTO> takeOnOrders = orderResponseDTOS.stream()
                .filter(order -> order.getOrderProducts().stream()
                        .anyMatch(orderProduct -> orderProduct.getStatus().equals("IN_PROGRESS")))
                .toList();

        // Prepara e restituisce la response raggruppata
        FilteredOrdersResponse filteredOrdersResponse = new FilteredOrdersResponse();
        filteredOrdersResponse.setAllOrders(orderResponseDTOS);
        filteredOrdersResponse.setDelayedOrders(delayedOrders);
        filteredOrdersResponse.setRefusedOrders(refusedOrders);
        filteredOrdersResponse.setPendingOrders(pendingOrders);
        filteredOrdersResponse.setTakeOnOrders(takeOnOrders);
        filteredOrdersResponse.setDeliveredOrders(deliveredOrders);

        return filteredOrdersResponse;
    }

    @Override
    public OrderResponseDTO deliveryResponse(long orderProductId, String response) {
        OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
                .orElseThrow(() -> new EntityNotFoundException("prodotto dell'ordine", "id", orderProductId));

        if (orderProduct.getStatus() != OrderProductStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Lo stato del prodotto dell'ordine non è 'in corso'");
        }

        orderProduct.setStatus(OrderProductStatus.DELIVERED);
        orderProduct.setEstimatedDeliveryDate(LocalDateTime.now());
        emailService.sendDeliveryConfirmationEmail(orderProduct.getOrder().getBuyer(), orderProduct, response);

        try {
            orderProductRepository.save(orderProduct);
        } catch (Exception e) {
            throw new EntityEditException("prodotto dell'ordine", "id", orderProduct.getId());
        }

        return orderMapper.orderToOrderResponseDTO(orderProduct.getOrder());
    }

    @Override
    public List<OrderResponseDTO> getOrderFiltered(OrderFilterRequest orderFilterRequest) {
        // Ottieni l'utente loggato
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();  // Supponiamo che il tipo di utente sia User

        // Inizializza l'EntityManager e CriteriaBuilder per costruire la query dinamica
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
        Root<Order> root = criteriaQuery.from(Order.class);

        // Filtro per l'utente loggato
        Predicate userPredicate = criteriaBuilder.equal(root.get("buyer").get("id"), user.getId());

        // Aggiungi altre condizioni di filtro basate sui parametri ricevuti (ad esempio, intervallo date, totale, ecc.)
        List<Predicate> predicates = buildOrderPredicates(orderFilterRequest, criteriaBuilder, root);

        // Applica il predicato dell'utente e gli altri filtri
        if (!predicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(userPredicate, criteriaBuilder.and(predicates.toArray(new Predicate[0]))));
        } else {
            criteriaQuery.where(userPredicate);
        }

        // Esegui la query per ottenere gli ordini filtrati
        TypedQuery<Order> query = entityManager.createQuery(criteriaQuery);
        List<Order> orders = query.getResultList();


        return orderMapper.orderListToOrderResponseList(orders);
    }

    protected List<Predicate> buildOrderPredicates(OrderFilterRequest orderFilterRequest, CriteriaBuilder criteriaBuilder, Root<Order> root) {
        List<Predicate> predicates = new ArrayList<>();

        // Crea il join con OrderProduct
        Join<Order, OrderProduct> orderProductJoin = root.join("orderProducts", JoinType.LEFT);

        // Aggiungi il filtro sul testo di ricerca (ad esempio, per l'ID dell'ordine o il nome del buyer)
        addSearchTextPredicate(orderFilterRequest, criteriaBuilder, root, predicates, orderProductJoin);

        // Aggiungi il filtro per l'intervallo di date
        addDateRangePredicate(orderFilterRequest, criteriaBuilder, root, predicates);

        return predicates;
    }

    private void addSearchTextPredicate(OrderFilterRequest request, CriteriaBuilder criteriaBuilder, Root<Order> root, List<Predicate> predicates, Join<Order, OrderProduct> orderProductJoin) {
        if (request.getSearchText() != null && !request.getSearchText().isEmpty()) {
            String searchPattern = "%" + request.getSearchText().toLowerCase() + "%";

            // Filtra per ID ordine
            Predicate idLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("id").as(String.class)), searchPattern);

            // Filtra per nome del buyer
            Predicate buyerNameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("buyer").get("name")), searchPattern);

            // Filtra per cognome del buyer
            Predicate buyerSurnameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("buyer").get("surname")), searchPattern);

            // Filtra per email del buyer
            Predicate buyerEmailLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("buyer").get("email")), searchPattern);

            // Filtra per titolo del prodotto negli OrderProduct associati
            Predicate productTitleLike = criteriaBuilder.like(criteriaBuilder.lower(orderProductJoin.get("product").get("title")), searchPattern);

            // Combina tutte le condizioni di ricerca con un "OR"
            predicates.add(criteriaBuilder.or(idLike, buyerNameLike, buyerSurnameLike, buyerEmailLike, productTitleLike));
        }
    }


    private void addDateRangePredicate(OrderFilterRequest request, CriteriaBuilder criteriaBuilder, Root<Order> root, List<Predicate> predicates) {
        if (request.getDateRangeType() != null) {
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            LocalDate now = LocalDate.now();

            switch (request.getDateRangeType()) {
                case "THIS_WEEK":
                    startDateTime = now.with(DayOfWeek.MONDAY).atStartOfDay();
                    endDateTime = now.with(DayOfWeek.SUNDAY).atTime(23, 59, 59);
                    break;
                case "THIS_MONTH":
                    startDateTime = now.withDayOfMonth(1).atStartOfDay();
                    endDateTime = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);
                    break;
                case "TODAY":
                    startDateTime = now.atStartOfDay();
                    endDateTime = now.atTime(23, 59, 59);
                    break;
                case "ALWAYS":
                default:
                    break;
            }

            if (startDateTime != null) {
                predicates.add(criteriaBuilder.between(root.get("orderDate"), startDateTime, endDateTime));
            }
        }
    }
}
