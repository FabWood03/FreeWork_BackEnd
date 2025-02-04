package org.elis.progettoing.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.elis.progettoing.dto.request.FilterRequest;
import org.elis.progettoing.dto.response.FilteredEntitiesResponse;
import org.elis.progettoing.dto.response.auction.AuctionSummaryDTO;
import org.elis.progettoing.dto.response.product.ProductSummaryDTO;
import org.elis.progettoing.mapper.definition.AuctionMapper;
import org.elis.progettoing.mapper.definition.ProductMapper;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.models.category.SubCategory;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.service.implementation.FilterServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilterServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private AuctionMapper auctionMapper;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private FilterServiceImpl filterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getFilteredEntities_shouldReturnFilteredEntities_whenValidRequest() {
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setSearchText("test");

        List<Auction> mockAuctions = List.of(new Auction());
        List<Product> mockProducts = List.of(new Product());
        List<AuctionSummaryDTO> mockAuctionDTOs = List.of(new AuctionSummaryDTO());
        List<ProductSummaryDTO> mockProductDTOs = List.of(new ProductSummaryDTO());

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Auction> auctionQuery = mock(CriteriaQuery.class);
        Root<Auction> auctionRoot = mock(Root.class);
        Path<Object> auctionPath = mock(Path.class);
        TypedQuery<Auction> auctionTypedQuery = mock(TypedQuery.class);

        CriteriaQuery<Product> productQuery = mock(CriteriaQuery.class);
        Root<Product> productRoot = mock(Root.class);
        Path<Object> productPath = mock(Path.class);
        TypedQuery<Product> productTypedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Auction.class)).thenReturn(auctionQuery);
        when(auctionQuery.from(Auction.class)).thenReturn(auctionRoot);
        when(auctionRoot.get(anyString())).thenReturn(auctionPath); // Mocking get method
        when(auctionQuery.select(auctionRoot)).thenReturn(auctionQuery); // Mocking select method
        when(auctionQuery.where(any(Predicate.class))).thenReturn(auctionQuery); // Mocking where method
        when(entityManager.createQuery(auctionQuery)).thenReturn(auctionTypedQuery);
        when(auctionTypedQuery.getResultList()).thenReturn(mockAuctions);

        when(cb.createQuery(Product.class)).thenReturn(productQuery);
        when(productQuery.from(Product.class)).thenReturn(productRoot);
        when(productRoot.get(anyString())).thenReturn(productPath); // Mocking get method
        when(productQuery.select(productRoot)).thenReturn(productQuery); // Mocking select method
        when(productQuery.where(any(Predicate.class))).thenReturn(productQuery); // Mocking where method
        when(entityManager.createQuery(productQuery)).thenReturn(productTypedQuery);
        when(productTypedQuery.getResultList()).thenReturn(mockProducts);

        when(auctionMapper.auctionToAuctionSummaryDTO(any(Auction.class))).thenReturn(mockAuctionDTOs.getFirst());
        when(productMapper.productToSummaryDTO(any(Product.class))).thenReturn(mockProductDTOs.getFirst());

        FilteredEntitiesResponse response = filterService.getFilteredEntities(filterRequest);

        assertNotNull(response);
        assertEquals(1, response.getFilteredAuctions().size());
        assertEquals(1, response.getFilteredProducts().size());
    }

    @Test
    void getFilteredEntities_shouldReturnEmptyLists_whenNoEntitiesMatch() {
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setSearchText("nonexistent");

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Auction> auctionQuery = mock(CriteriaQuery.class);
        Root<Auction> auctionRoot = mock(Root.class);
        Path<Object> auctionPath = mock(Path.class);
        TypedQuery<Auction> auctionTypedQuery = mock(TypedQuery.class);

        CriteriaQuery<Product> productQuery = mock(CriteriaQuery.class);
        Root<Product> productRoot = mock(Root.class);
        Path<Object> productPath = mock(Path.class);
        TypedQuery<Product> productTypedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Auction.class)).thenReturn(auctionQuery);
        when(auctionQuery.from(Auction.class)).thenReturn(auctionRoot);
        when(auctionRoot.get(anyString())).thenReturn(auctionPath); // Mocking get method
        when(auctionQuery.select(auctionRoot)).thenReturn(auctionQuery); // Mocking select method
        when(auctionQuery.where(any(Predicate.class))).thenReturn(auctionQuery); // Mocking where method
        when(entityManager.createQuery(auctionQuery)).thenReturn(auctionTypedQuery);
        when(auctionTypedQuery.getResultList()).thenReturn(List.of());

        when(cb.createQuery(Product.class)).thenReturn(productQuery);
        when(productQuery.from(Product.class)).thenReturn(productRoot);
        when(productRoot.get(anyString())).thenReturn(productPath); // Mocking get method
        when(productQuery.select(productRoot)).thenReturn(productQuery); // Mocking select method
        when(productQuery.where(any(Predicate.class))).thenReturn(productQuery); // Mocking where method
        when(entityManager.createQuery(productQuery)).thenReturn(productTypedQuery);
        when(productTypedQuery.getResultList()).thenReturn(List.of());

        FilteredEntitiesResponse response = filterService.getFilteredEntities(filterRequest);

        assertNotNull(response);
        assertTrue(response.getFilteredAuctions().isEmpty());
        assertTrue(response.getFilteredProducts().isEmpty());
    }

    @Test
    void getFilteredEntities_shouldHandleNullFieldsInRequest() {
        FilterRequest filterRequest = new FilterRequest();

        List<Auction> mockAuctions = List.of(new Auction());
        List<Product> mockProducts = List.of(new Product());
        List<AuctionSummaryDTO> mockAuctionDTOs = List.of(new AuctionSummaryDTO());
        List<ProductSummaryDTO> mockProductDTOs = List.of(new ProductSummaryDTO());

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Auction> auctionQuery = mock(CriteriaQuery.class);
        Root<Auction> auctionRoot = mock(Root.class);
        TypedQuery<Auction> auctionTypedQuery = mock(TypedQuery.class);

        CriteriaQuery<Product> productQuery = mock(CriteriaQuery.class);
        Root<Product> productRoot = mock(Root.class);
        TypedQuery<Product> productTypedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Auction.class)).thenReturn(auctionQuery);
        when(auctionQuery.from(Auction.class)).thenReturn(auctionRoot);
        when(auctionQuery.select(auctionRoot)).thenReturn(auctionQuery); // Mocking select method
        when(auctionQuery.where(any(Predicate.class))).thenReturn(auctionQuery); // Mocking where method
        when(entityManager.createQuery(auctionQuery)).thenReturn(auctionTypedQuery);
        when(auctionTypedQuery.getResultList()).thenReturn(mockAuctions);

        when(cb.createQuery(Product.class)).thenReturn(productQuery);
        when(productQuery.from(Product.class)).thenReturn(productRoot);
        when(productQuery.select(productRoot)).thenReturn(productQuery); // Mocking select method
        when(productQuery.where(any(Predicate.class))).thenReturn(productQuery); // Mocking where method
        when(entityManager.createQuery(productQuery)).thenReturn(productTypedQuery);
        when(productTypedQuery.getResultList()).thenReturn(mockProducts);

        when(auctionMapper.auctionToAuctionSummaryDTO(any(Auction.class))).thenReturn(mockAuctionDTOs.getFirst());
        when(productMapper.productToSummaryDTO(any(Product.class))).thenReturn(mockProductDTOs.getFirst());

        FilteredEntitiesResponse response = filterService.getFilteredEntities(filterRequest);

        assertNotNull(response);
        assertEquals(1, response.getFilteredAuctions().size());
        assertEquals(1, response.getFilteredProducts().size());
    }

    @Test
    void getFilteredEntities_shouldFilterBySubCategory_whenSubCategoryIsProvided() {
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setSubCategory(1);

        SubCategory mockSubCategory = new SubCategory();
        mockSubCategory.setName("Electronics");

        List<Auction> mockAuctions = List.of(new Auction());
        List<Product> mockProducts = List.of(new Product());
        List<AuctionSummaryDTO> mockAuctionDTOs = List.of(new AuctionSummaryDTO());
        List<ProductSummaryDTO> mockProductDTOs = List.of(new ProductSummaryDTO());

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Auction> auctionQuery = mock(CriteriaQuery.class);
        Root<Auction> auctionRoot = mock(Root.class);
        Path<Object> auctionPath = mock(Path.class);
        TypedQuery<Auction> auctionTypedQuery = mock(TypedQuery.class);

        CriteriaQuery<Product> productQuery = mock(CriteriaQuery.class);
        Root<Product> productRoot = mock(Root.class);
        Path<Object> productPath = mock(Path.class);
        TypedQuery<Product> productTypedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Auction.class)).thenReturn(auctionQuery);
        when(auctionQuery.from(Auction.class)).thenReturn(auctionRoot);
        when(auctionRoot.get(anyString())).thenReturn(auctionPath); // Mocking get method
        when(auctionQuery.select(auctionRoot)).thenReturn(auctionQuery); // Mocking select method
        when(auctionQuery.where(any(Predicate.class))).thenReturn(auctionQuery); // Mocking where method
        when(entityManager.createQuery(auctionQuery)).thenReturn(auctionTypedQuery);
        when(auctionTypedQuery.getResultList()).thenReturn(mockAuctions);

        when(cb.createQuery(Product.class)).thenReturn(productQuery);
        when(productQuery.from(Product.class)).thenReturn(productRoot);
        when(productRoot.get(anyString())).thenReturn(productPath); // Mocking get method
        when(productQuery.select(productRoot)).thenReturn(productQuery); // Mocking select method
        when(productQuery.where(any(Predicate.class))).thenReturn(productQuery); // Mocking where method
        when(entityManager.createQuery(productQuery)).thenReturn(productTypedQuery);
        when(productTypedQuery.getResultList()).thenReturn(mockProducts);

        when(entityManager.createQuery("SELECT s FROM SubCategory s WHERE s.name = :name", SubCategory.class))
                .thenReturn(mock(TypedQuery.class));
        when(entityManager.createQuery("SELECT s FROM SubCategory s WHERE s.name = :name", SubCategory.class)
                .setParameter("name", "Electronics")).thenReturn(mock(TypedQuery.class));
        when(entityManager.createQuery("SELECT s FROM SubCategory s WHERE s.name = :name", SubCategory.class)
                .setParameter("name", "Electronics").getSingleResult()).thenReturn(mockSubCategory);

        when(auctionMapper.auctionToAuctionSummaryDTO(any(Auction.class))).thenReturn(mockAuctionDTOs.getFirst());
        when(productMapper.productToSummaryDTO(any(Product.class))).thenReturn(mockProductDTOs.getFirst());

        FilteredEntitiesResponse response = filterService.getFilteredEntities(filterRequest);

        assertNotNull(response);
        assertEquals(1, response.getFilteredAuctions().size());
        assertEquals(1, response.getFilteredProducts().size());
    }
}