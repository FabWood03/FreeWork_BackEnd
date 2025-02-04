package org.elis.progettoing.controllers;

import org.elis.progettoing.dto.request.auction.OfferRequestDTO;
import org.elis.progettoing.dto.response.auction.OfferResponseDTO;
import org.elis.progettoing.service.definition.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OfferControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OfferService offerService;

    @InjectMocks
    private OfferController offerController;

    private OfferResponseDTO offerResponseDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(offerController).build();

        OfferRequestDTO offerRequestDTO = new OfferRequestDTO();
        offerRequestDTO.setAuctionId(1L);
        offerRequestDTO.setDeliveryTimeProposed(5L);
        offerRequestDTO.setPrice(100.0);

        offerResponseDTO = new OfferResponseDTO();
        offerResponseDTO.setId(1L);
        offerResponseDTO.setAuctionId(1L);
        offerResponseDTO.setDeliveryTimeProposed(5L);
        offerResponseDTO.setPrice(100.0);
    }

    @Test
    void testCreateOffer() throws Exception {
        when(offerService.createOffer(any(OfferRequestDTO.class)))
                .thenReturn(offerResponseDTO);

        mockMvc.perform(post("/api/offer/create")
                        .contentType("application/json")
                        .content("{\"auctionId\":1,\"deliveryTimeProposed\":5,\"price\":100.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.auctionId").value(1L))
                .andExpect(jsonPath("$.deliveryTimeProposed").value(5L))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    @Test
    void testDeleteOffer() throws Exception {
        when(offerService.deleteOffer(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/offer/delete")
                        .param("auctionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testUpdateOffer() throws Exception {
        when(offerService.updateOffer(any(OfferRequestDTO.class)))
                .thenReturn(offerResponseDTO);

        mockMvc.perform(patch("/api/offer/update")
                        .contentType("application/json")
                        .content("{\"auctionId\":1,\"deliveryTimeProposed\":5,\"price\":100.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.auctionId").value(1L))
                .andExpect(jsonPath("$.deliveryTimeProposed").value(5L))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    @Test
    void testGetOfferById() throws Exception {
        when(offerService.getOfferById(1L))
                .thenReturn(offerResponseDTO);

        mockMvc.perform(get("/api/offer/getOfferById")
                        .param("offerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.auctionId").value(1L))
                .andExpect(jsonPath("$.deliveryTimeProposed").value(5L))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    @Test
    void testGetOffersByAuctionId() throws Exception {
        when(offerService.getAllOffersByAuction(1L))
                .thenReturn(Collections.singletonList(offerResponseDTO));

        mockMvc.perform(get("/api/offer/getOffersByAuctionId")
                        .param("auctionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].auctionId").value(1L))
                .andExpect(jsonPath("$[0].deliveryTimeProposed").value(5L))
                .andExpect(jsonPath("$[0].price").value(100.0));
    }

    @Test
    void testGetOfferByUser() throws Exception {
        when(offerService.getOfferByUser(1L))
                .thenReturn(offerResponseDTO);

        mockMvc.perform(get("/api/offer/getOfferByUser")
                        .param("auctionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.auctionId").value(1L))
                .andExpect(jsonPath("$.deliveryTimeProposed").value(5L))
                .andExpect(jsonPath("$.price").value(100.0));
    }
}
