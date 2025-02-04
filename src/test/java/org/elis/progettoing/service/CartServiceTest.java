package org.elis.progettoing.service;

import org.elis.progettoing.dto.request.product.PurchasedProductRequestDTO;
import org.elis.progettoing.dto.response.cart.CartResponseDTO;
import org.elis.progettoing.dto.response.cart.PurchasedProductResponseDTO;
import org.elis.progettoing.exception.PurchasedProductException;
import org.elis.progettoing.exception.entity.EntityAlreadyExistsException;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.CartMapper;
import org.elis.progettoing.mapper.definition.PurchasedProductMapper;
import org.elis.progettoing.models.Cart;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.ProductPackage;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.elis.progettoing.repository.CartRepository;
import org.elis.progettoing.repository.ProductPackageRepository;
import org.elis.progettoing.repository.ProductRepository;
import org.elis.progettoing.repository.PurchasedProductRepository;
import org.elis.progettoing.service.implementation.CartServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductPackageRepository productPackageRepository;

    @Mock
    private PurchasedProductRepository purchasedProductRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private PurchasedProductMapper purchasedProductMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User mockUser;
    private Cart mockCart;
    private Product mockProduct;
    private ProductPackage mockProductPackage;
    PurchasedProductRequestDTO requestDTO = new PurchasedProductRequestDTO();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);

        mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setPurchasedProducts(Collections.emptyList());
        mockUser.setCart(mockCart);

        mockProduct = new Product();
        mockProduct.setId(1L);

        mockProductPackage = new ProductPackage();
        mockProductPackage.setId(1L);
        mockProductPackage.setProduct(mockProduct);

        requestDTO.setProductId(1L);
        requestDTO.setPackageId(2L);

        // Mock security context
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(mockUser, null));
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findById_ShouldReturnCartResponseDTO_WhenCartExists() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(mockCart));
        CartResponseDTO mockResponse = new CartResponseDTO();
        when(cartMapper.cartToCartResponseDTO(mockCart)).thenReturn(mockResponse);

        CartResponseDTO result = cartService.findById(1L);

        assertEquals(mockResponse, result);
        verify(cartRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowEntityNotFoundException_WhenCartDoesNotExist() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.findById(1L));
        verify(cartRepository, times(1)).findById(1L);
    }

    @Test
    void findByUserId_ShouldReturnCartResponseDTO_WhenUserHasCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(mockCart));
        CartResponseDTO mockResponse = new CartResponseDTO();
        when(cartMapper.cartToCartResponseDTO(mockCart)).thenReturn(mockResponse);

        CartResponseDTO result = cartService.findByUserId();

        assertEquals(mockResponse, result);
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    @Test
    void findByUserId_ShouldThrowEntityNotFoundException_WhenUserHasNoCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.findByUserId());
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    @Test
    void addPurchasedProduct_ShouldAddProductToCart_WhenValidRequest() {
        PurchasedProductRequestDTO request = new PurchasedProductRequestDTO();
        request.setProductId(1L);
        request.setPackageId(1L);

        PurchasedProduct purchasedProduct = new PurchasedProduct();

        User productUser = new User();
        productUser.setId(2L);
        mockProduct.setUser(productUser);

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productPackageRepository.findByProductIdAndId(1L, 1L)).thenReturn(Optional.of(mockProductPackage));
        when(purchasedProductMapper.requestDTOToPurchasedProduct(request)).thenReturn(purchasedProduct);
        when(purchasedProductRepository.save(purchasedProduct)).thenReturn(purchasedProduct);
        PurchasedProductResponseDTO mockResponse = new PurchasedProductResponseDTO();
        when(purchasedProductMapper.purchasedProductToResponseDTO(purchasedProduct)).thenReturn(mockResponse);

        PurchasedProductResponseDTO result = cartService.addPurchasedProduct(request);

        assertEquals(mockResponse, result);
        verify(purchasedProductRepository, times(1)).save(purchasedProduct);
    }

    @Test
    void addPurchasedProduct_ShouldThrowEntityAlreadyExistsException_WhenProductAlreadyInCart() {
        PurchasedProductRequestDTO request = new PurchasedProductRequestDTO();
        request.setProductId(1L);
        request.setPackageId(1L);

        PurchasedProduct existingProduct = new PurchasedProduct();
        existingProduct.setProduct(mockProduct);
        existingProduct.setSelectedPackage(mockProductPackage);
        mockCart.setPurchasedProducts(Collections.singletonList(existingProduct));

        User productUser = new User();
        productUser.setId(2L);
        mockProduct.setUser(productUser);

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productPackageRepository.findByProductIdAndId(1L, 1L)).thenReturn(Optional.of(mockProductPackage));

        assertThrows(EntityAlreadyExistsException.class, () -> cartService.addPurchasedProduct(request));
        verify(productRepository, times(1)).findById(1L);
        verify(productPackageRepository, times(1)).findByProductIdAndId(1L, 1L);
        verifyNoMoreInteractions(productRepository, productPackageRepository, purchasedProductRepository);
    }

    @Test
    void addPurchasedProduct_ShouldThrowEntityNotFoundException_WhenSelectedPackageNotFound() {
        // Setup della richiesta
        requestDTO.setProductId(1L);
        requestDTO.setPackageId(2L);

        // Mock del prodotto esistente
        mockProduct.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        // Simula che il pacchetto non venga trovato
        when(productPackageRepository.findByProductIdAndId(1L, 2L)).thenReturn(Optional.empty());

        // Verifica che venga lanciata l'eccezione EntityNotFoundException
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cartService.addPurchasedProduct(requestDTO));

        // Verifica il messaggio dell'eccezione
        assertEquals("Nessun pacchetto con ID prodotto = 1 è stato trovato.", exception.getMessage());

        // Verifica che il metodo findById sia stato chiamato sul repository del prodotto
        verify(productRepository, times(1)).findById(1L);
        verify(productPackageRepository, times(1)).findByProductIdAndId(1L, 2L);
    }

    @Test
    void addPurchasedProduct_ShouldThrowEntityCreationException_WhenSaveFails() {
        PurchasedProduct mockPurchasedProduct = new PurchasedProduct();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockUser, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ProductPackage mockPackage = new ProductPackage();
        mockPackage.setId(2L);
        mockCart.setPurchasedProducts(new ArrayList<>());
        mockUser.setCart(mockCart);

        User productUser = new User();
        productUser.setId(2L);
        mockProduct.setUser(productUser);

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productPackageRepository.findByProductIdAndId(1L, 2L)).thenReturn(Optional.of(mockPackage));
        when(purchasedProductMapper.requestDTOToPurchasedProduct(requestDTO)).thenReturn(mockPurchasedProduct);

        doThrow(new RuntimeException("Database error")).when(purchasedProductRepository).save(mockPurchasedProduct);

        EntityCreationException exception = assertThrows(EntityCreationException.class,
                () -> cartService.addPurchasedProduct(requestDTO));

        assertEquals("Si è verificato un errore durante la creazione di prodotto acquistato con ID prodotto = 1", exception.getMessage());

        verify(purchasedProductRepository, times(1)).save(mockPurchasedProduct);
    }

    @Test
    void removePurchasedProduct_ShouldRemoveProductFromCart_WhenProductExists() {
        PurchasedProduct purchasedProduct = new PurchasedProduct();
        purchasedProduct.setId(1L);
        // Usa una lista modificabile
        mockCart.setPurchasedProducts(new ArrayList<>(Collections.singletonList(purchasedProduct)));

        when(cartRepository.save(mockCart)).thenReturn(mockCart);

        boolean result = cartService.removePurchasedProduct(1L);

        assertTrue(result);
        verify(cartRepository, times(1)).save(mockCart);
    }

    @Test
    void removePurchasedProduct_ShouldThrowEntityNotFoundException_WhenProductNotInCart() {
        mockCart.setPurchasedProducts(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () -> cartService.removePurchasedProduct(1L));
        verify(cartRepository, never()).save(mockCart);
    }

    @Test
    void removePurchasedProduct_ShouldThrowEntityDeletionException_WhenCartSaveFails() {
        PurchasedProduct purchasedProduct = new PurchasedProduct();
        purchasedProduct.setId(1L);
        // Usa una lista modificabile per permettere le operazioni di modifica
        mockCart.setPurchasedProducts(new ArrayList<>(Collections.singletonList(purchasedProduct)));

        // Simula l'eccezione al salvataggio del carrello
        doThrow(new RuntimeException("Database save error")).when(cartRepository).save(mockCart);

        // Verifica che l'eccezione EntityDeletionException venga lanciata
        assertThrows(EntityDeletionException.class, () -> cartService.removePurchasedProduct(1L));

        // Verifica che il metodo save sia stato chiamato
        verify(cartRepository, times(1)).save(mockCart);
    }

    @Test
    void addPurchasedProduct_ShouldThrowPurchasedProductException_WhenBuyerIsSeller() {
        // Given: A request where the buyer is the seller (same ID)
        PurchasedProductRequestDTO request = new PurchasedProductRequestDTO();
        request.setProductId(1L);
        request.setPackageId(1L);

        // Setup mock product with the same user ID as the buyer
        Product product = new Product();
        product.setId(1L);
        User seller = new User();
        seller.setId(1L);  // Same ID as the buyer
        product.setUser(seller);

        // Simulate the buyer being the same as the seller
        User mockBuyer = new User();
        mockBuyer.setId(1L);

        // Mock security context for buyer authentication
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(mockBuyer, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Mock the repository calls
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productPackageRepository.findByProductIdAndId(1L, 1L)).thenReturn(Optional.of(mockProductPackage));

        // When & Then: Ensure that the exception is thrown
        assertThrows(PurchasedProductException.class, () -> cartService.addPurchasedProduct(request));

        // Verify repository interactions
        verify(productRepository, times(1)).findById(1L);
        verify(productPackageRepository, times(1)).findByProductIdAndId(1L, 1L);
    }
}
