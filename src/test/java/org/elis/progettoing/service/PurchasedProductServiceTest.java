package org.elis.progettoing.service;

import org.elis.progettoing.dto.response.cart.PurchasedProductResponseDTO;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.PurchasedProductMapper;
import org.elis.progettoing.models.Cart;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.elis.progettoing.repository.PurchasedProductRepository;
import org.elis.progettoing.service.implementation.PurchasedProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchasedProductServiceTest {

    @Mock
    private PurchasedProductRepository purchasedProductRepository;

    @Mock
    private PurchasedProductMapper purchasedProductMapper;

    @InjectMocks
    private PurchasedProductServiceImpl purchasedProductService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = mock(User.class);
        when(user.getCart()).thenReturn(mock(Cart.class));  // Simula un carrello per l'utente
    }

    @Test
    void testFindById_Success() {
        Long productId = 1L;
        PurchasedProduct product = new PurchasedProduct();
        product.setId(productId);

        PurchasedProductResponseDTO responseDTO = new PurchasedProductResponseDTO();
        responseDTO.setId(productId);

        when(purchasedProductRepository.findById(productId)).thenReturn(Optional.of(product));
        when(purchasedProductMapper.purchasedProductToResponseDTO(product)).thenReturn(responseDTO);

        PurchasedProductResponseDTO result = purchasedProductService.findById(productId);

        assertNotNull(result);
        assertEquals(productId, result.getId());
    }

    @Test
    void testFindById_EntityNotFound() {
        Long productId = 1L;
        when(purchasedProductRepository.findById(productId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> purchasedProductService.findById(productId));
        assertEquals("Nessun carrello con ID = 1 è stato trovato.", exception.getMessage());
    }

    @Test
    void testFindByCartId_success() {
        Long cartId = 1L;
        PurchasedProduct product = new PurchasedProduct();
        List<PurchasedProduct> purchasedProducts = Collections.singletonList(product);

        // Mock dell'oggetto Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);  // Restituisci il mock di user

        // Mock di SecurityContextHolder
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);  // Imposta il contesto di sicurezza mockato

        when(user.getCart().getId()).thenReturn(cartId);  // Simula il carrello dell'utente
        when(purchasedProductRepository.findAllByCartId(cartId)).thenReturn(purchasedProducts);
        when(purchasedProductMapper.purchasedProductsToPurchasedProductDTOs(purchasedProducts)).thenReturn(Collections.singletonList(new PurchasedProductResponseDTO()));

        // Chiamata al metodo
        List<PurchasedProductResponseDTO> result = purchasedProductService.findByCartId();

        // Verifica dei risultati
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(purchasedProductRepository).findAllByCartId(cartId);
        verify(purchasedProductMapper).purchasedProductsToPurchasedProductDTOs(purchasedProducts);
    }


    @Test
    void testFindByCartId_emptyList() {
        Long cartId = 1L;

        // Mock dell'oggetto Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);  // Restituisci il mock di user

        // Mock di SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);  // Imposta il contesto di sicurezza mockato

        when(user.getCart().getId()).thenReturn(cartId);
        when(purchasedProductRepository.findAllByCartId(cartId)).thenReturn(Collections.emptyList());

        // Verifica che venga lanciata l'eccezione
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> purchasedProductService.findByCartId());
        assertEquals("Nessun prodotto con ID carrello = 1 è stato trovato.", exception.getMessage());
    }


    @Test
    void testFindByCartId_EntityNotFound() {
        Long cartId = 1L;
        Authentication authentication = mock(Authentication.class);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(user.getCart().getId()).thenReturn(cartId);
        when(purchasedProductRepository.findAllByCartId(cartId)).thenReturn(Collections.emptyList());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> purchasedProductService.findByCartId());
        assertEquals("Nessun prodotto con ID carrello = 1 è stato trovato.", exception.getMessage());
    }

    @Test
    void testFindAllPurchasedProducts_Success() {
        PurchasedProduct product1 = new PurchasedProduct();
        PurchasedProduct product2 = new PurchasedProduct();
        List<PurchasedProduct> allProducts = Arrays.asList(product1, product2);

        PurchasedProductResponseDTO responseDTO1 = new PurchasedProductResponseDTO();
        PurchasedProductResponseDTO responseDTO2 = new PurchasedProductResponseDTO();
        List<PurchasedProductResponseDTO> responseDTOs = Arrays.asList(responseDTO1, responseDTO2);

        when(purchasedProductRepository.findAll()).thenReturn(allProducts);
        when(purchasedProductMapper.purchasedProductsToPurchasedProductDTOs(allProducts)).thenReturn(responseDTOs);

        List<PurchasedProductResponseDTO> result = purchasedProductService.findAllPurchasedProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindAllPurchasedProducts_EmptyList() {
        when(purchasedProductRepository.findAll()).thenReturn(Collections.emptyList());

        List<PurchasedProductResponseDTO> result = purchasedProductService.findAllPurchasedProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
