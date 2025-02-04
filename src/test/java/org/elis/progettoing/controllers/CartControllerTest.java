package org.elis.progettoing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elis.progettoing.dto.request.product.PurchasedProductRequestDTO;
import org.elis.progettoing.dto.response.cart.CartResponseDTO;
import org.elis.progettoing.dto.response.cart.PurchasedProductResponseDTO;
import org.elis.progettoing.service.definition.CartService;
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

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    private ObjectMapper objectMapper;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testFindById() throws Exception {
        CartResponseDTO cartResponseDTO = new CartResponseDTO();
        cartResponseDTO.setId(1L);
        cartResponseDTO.setPurchasedProducts(Collections.emptyList());

        when(cartService.findById(1L)).thenReturn(cartResponseDTO);

        mockMvc.perform(get("/api/cart/findById?id=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.purchasedProducts").isEmpty());
    }

    @Test
    void testFindByUser() throws Exception {
        CartResponseDTO cartResponseDTO = new CartResponseDTO();
        cartResponseDTO.setId(1L);
        cartResponseDTO.setPurchasedProducts(Collections.emptyList());

        when(cartService.findByUserId()).thenReturn(cartResponseDTO);

        mockMvc.perform(get("/api/cart/findByUserId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.purchasedProducts").isEmpty());
    }

    @Test
    void testAddPurchasedProduct() throws Exception {
        PurchasedProductRequestDTO requestDTO = new PurchasedProductRequestDTO(1L, 2L);
        PurchasedProductResponseDTO responseDTO = new PurchasedProductResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setProductImagePhoto("image.jpg");
        responseDTO.setProductTitle("Product Title");
        responseDTO.setUserName("John");
        responseDTO.setUserSurname("Doe");
        responseDTO.setType("Type1");
        responseDTO.setPrice(1000L);

        when(cartService.addPurchasedProduct(Mockito.any(PurchasedProductRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/cart/addPurchasedProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productImagePhoto").value("image.jpg"))
                .andExpect(jsonPath("$.price").value(1000L));
    }

    @Test
    void testRemovePurchasedProduct() throws Exception {
        when(cartService.removePurchasedProduct(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/cart/removePurchasedProduct?id=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
