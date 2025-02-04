package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.request.auction.OfferRequestDTO;
import org.elis.progettoing.dto.response.auction.OfferResponseDTO;
import org.elis.progettoing.mapper.implementation.OfferMapperImpl;
import org.elis.progettoing.mapper.implementation.UserMapperImpl;
import org.elis.progettoing.models.Offer;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferMapperImplTest {
    @Mock
    private UserMapperImpl userMapper;

    @InjectMocks
    private OfferMapperImpl offerMapper;

    @Test
    void testOfferToOfferResponseDTO() {
        Offer offer = new Offer();
        offer.setId(1L);
        offer.setPrice(100.0);
        offer.setDeliveryTimeProposed(13);

        Auction auction = new Auction();
        auction.setId(10L);
        offer.setAuction(auction);

        User seller = new User();
        offer.setSeller(seller);

        when(userMapper.userToUserResponseDTO(seller)).thenReturn(null);

        OfferResponseDTO responseDTO = offerMapper.offerToOfferResponseDTO(offer);

        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals(100.0, responseDTO.getPrice());
        assertEquals(13, responseDTO.getDeliveryTimeProposed());
        assertEquals(10L, responseDTO.getAuctionId());
        assertNull(responseDTO.getSeller());
    }

    @Test
    void testOfferToOfferResponseDTO_Null() {
        OfferResponseDTO responseDTO = offerMapper.offerToOfferResponseDTO(null);
        assertNull(responseDTO);
    }

    @Test
    void testOfferRequestDTOToOffer() {
        OfferRequestDTO requestDTO = new OfferRequestDTO();
        requestDTO.setAuctionId(10L);
        requestDTO.setPrice(150.0);
        requestDTO.setDeliveryTimeProposed(13);

        Offer offer = offerMapper.offerRequestDTOToOffer(requestDTO);

        assertNotNull(offer);
        assertEquals(150.0, offer.getPrice());
        assertEquals(13, offer.getDeliveryTimeProposed());
        assertNotNull(offer.getAuction());
        assertEquals(10L, offer.getAuction().getId());
    }

    @Test
    void testOfferRequestDTOToOffer_Null() {
        Offer offer = offerMapper.offerRequestDTOToOffer(null);
        assertNull(offer);
    }

    @Test
    void testOfferRequestDTOToAuction() {
        OfferRequestDTO requestDTO = new OfferRequestDTO();
        requestDTO.setAuctionId(20L);

        Auction auction = offerMapper.offerRequestDTOToAuction(requestDTO);

        assertNotNull(auction);
        assertEquals(20L, auction.getId());
    }

    @Test
    void testOfferRequestDTOToAuction_Null() {
        Auction auction = offerMapper.offerRequestDTOToAuction(null);
        assertNull(auction);
    }

    @Test
    void testOfferAuctionId() {
        Offer offer = new Offer();
        Auction auction = new Auction();
        auction.setId(30L);
        offer.setAuction(auction);

        long auctionId = offerMapper.offerAuctionId(offer);

        assertEquals(30L, auctionId);
    }

    @Test
    void testOfferAuctionId_NullOffer() {
        long auctionId = offerMapper.offerAuctionId(null);
        assertEquals(0L, auctionId);
    }

    @Test
    void testOfferAuctionId_NullAuction() {
        Offer offer = new Offer();
        long auctionId = offerMapper.offerAuctionId(offer);
        assertEquals(0L, auctionId);
    }
}
