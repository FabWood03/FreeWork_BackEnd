package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.response.cart.CartResponseDTO;
import org.elis.progettoing.mapper.implementation.CartMapperImpl;
import org.elis.progettoing.mapper.implementation.PurchasedProductMapperImpl;
import org.elis.progettoing.models.Cart;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartMapperImplTest {
    @Mock
    private PurchasedProductMapperImpl purchasedProductMapper;

    @InjectMocks
    private CartMapperImpl cartMapper;

    @Test
    void testCartToCartResponseDTO() {
        Cart cart = new Cart();
        cart.setId(1L);

        PurchasedProduct purchasedProduct = new PurchasedProduct();
        cart.setPurchasedProducts(List.of(purchasedProduct));

        when(purchasedProductMapper.purchasedProductsToPurchasedProductDTOs(cart.getPurchasedProducts()))
                .thenReturn(Collections.emptyList());

        CartResponseDTO cartResponseDTO = cartMapper.cartToCartResponseDTO(cart);

        assertNotNull(cartResponseDTO);
        assertEquals(cart.getId(), cartResponseDTO.getId());
        assertNotNull(cartResponseDTO.getPurchasedProducts());
        assertTrue(cartResponseDTO.getPurchasedProducts().isEmpty());

        verify(purchasedProductMapper, times(1))
                .purchasedProductsToPurchasedProductDTOs(cart.getPurchasedProducts());
    }

    @Test
    void testCartToCartResponseDTO_NullCart() {
        CartResponseDTO cartResponseDTO = cartMapper.cartToCartResponseDTO(null);
        assertNull(cartResponseDTO);
    }

    @Test
    void testCartToCartResponseDTO_EmptyPurchasedProducts() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setPurchasedProducts(Collections.emptyList());

        when(purchasedProductMapper.purchasedProductsToPurchasedProductDTOs(cart.getPurchasedProducts()))
                .thenReturn(Collections.emptyList());

        CartResponseDTO cartResponseDTO = cartMapper.cartToCartResponseDTO(cart);

        assertNotNull(cartResponseDTO);
        assertEquals(cart.getId(), cartResponseDTO.getId());
        assertNotNull(cartResponseDTO.getPurchasedProducts());
        assertTrue(cartResponseDTO.getPurchasedProducts().isEmpty());

        verify(purchasedProductMapper, times(1))
                .purchasedProductsToPurchasedProductDTOs(cart.getPurchasedProducts());
    }
}
