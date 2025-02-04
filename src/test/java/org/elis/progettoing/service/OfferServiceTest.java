package org.elis.progettoing.service;

import org.elis.progettoing.dto.request.auction.OfferRequestDTO;
import org.elis.progettoing.dto.response.auction.OfferResponseDTO;
import org.elis.progettoing.enumeration.AuctionStatus;
import org.elis.progettoing.exception.auction.AuctionException;
import org.elis.progettoing.exception.auction.AuctionOwnershipException;
import org.elis.progettoing.exception.entity.EntityAlreadyExistsException;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.implementation.OfferMapperImpl;
import org.elis.progettoing.models.Offer;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.pattern.observerPattern.AuctionManager;
import org.elis.progettoing.repository.AuctionRepository;
import org.elis.progettoing.repository.OfferRepository;
import org.elis.progettoing.service.implementation.OfferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OfferServiceTest {
    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OfferMapperImpl offerMapper;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionManager auctionManager;

    @InjectMocks
    private OfferServiceImpl offerService;

    @Mock
    private UsernamePasswordAuthenticationToken authentication;

    OfferRequestDTO offerRequestDTO = new OfferRequestDTO();
    Offer offer1 = new Offer();
    Offer offer2 = new Offer();
    User user = new User();
    Auction auction = new Auction();
    Offer offer = new Offer();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user.setId(1L);

        auction.setId(1L);
        auction.setStatus(AuctionStatus.OPEN);
        auction.setOwner(new User());

        offerRequestDTO.setAuctionId(1L);
        offerRequestDTO.setPrice(100.0);
        offerRequestDTO.setDeliveryTimeProposed(5);

        offer.setId(1L);
        offer.setSeller(user);
        offer.setAuction(auction);

        offer1.setId(1L);
        offer1.setPrice(100.0);
        offer1.setDeliveryTimeProposed(5);
        offer1.setSeller(new User());

        offer2.setId(2L);
        offer2.setPrice(200.0);
        offer2.setDeliveryTimeProposed(10);
        offer2.setSeller(new User());

        authentication = new UsernamePasswordAuthenticationToken(user, null);
    }

    @Test
    void createOffer_WhenSaveFailed_ThrowEntityCreationException() {
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        when(offerRepository.findBySellerIdAndAuctionId(1L, 1L)).thenReturn(null);
        when(offerMapper.offerRequestDTOToOffer(offerRequestDTO)).thenReturn(offer);
        when(offerRepository.save(any(Offer.class))).thenThrow(new RuntimeException());

        assertThrows(EntityCreationException.class, () -> offerService.createOffer(offerRequestDTO));
    }

    @Test
    void createOffer_AuctionOwnerIsUserAuthenticated_ThrowAuctionOwnershipException() {
        auction.setOwner(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        assertThrows(AuctionOwnershipException.class, () -> offerService.createOffer(offerRequestDTO));
    }

    @Test
    void createOffer_WithValidRequest_CreatesAndReturnsOffer() {
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        when(offerRepository.findBySellerIdAndAuctionId(1L, 1L)).thenReturn(null);
        when(offerMapper.offerRequestDTOToOffer(offerRequestDTO)).thenReturn(offer);
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);
        when(offerMapper.offerToOfferResponseDTO(offer)).thenReturn(new OfferResponseDTO());

        OfferResponseDTO result = offerService.createOffer(offerRequestDTO);

        assertNotNull(result);
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    void createOffer_AuctionNotFound_ThrowsEntityNotFoundException() {
        when(auctionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> offerService.createOffer(offerRequestDTO));
    }

    @Test
    void createOffer_AuctionNotOpen_ThrowsAuctionException() {
        auction.setStatus(AuctionStatus.PENDING);

        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        assertThrows(AuctionException.class, () -> offerService.createOffer(offerRequestDTO));
    }

    @Test
    void createOffer_UserAlreadySubmittedOffer_ThrowsEntityAlreadyExistsException() {
        Offer existingOffer = new Offer();

        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        when(offerRepository.findBySellerIdAndAuctionId(1L, 1L)).thenReturn(existingOffer);

        assertThrows(EntityAlreadyExistsException.class, () -> offerService.createOffer(offerRequestDTO));
    }

    @Test
    void deleteOffer_WithValidRequest_DeletesOffer() {
        long auctionId = 1L;

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(offerRepository.findBySellerIdAndAuctionId(1L, auctionId)).thenReturn(offer);

        boolean result = offerService.deleteOffer(auctionId);

        assertTrue(result);
        verify(offerRepository).delete(offer);
    }

    @Test
    void deleteOffer_AuctionNotFound_ThrowsEntityNotFoundException() {
        long auctionId = 1L;

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> offerService.deleteOffer(auctionId));
    }

    @Test
    void updateOffer_WithValidRequest_UpdatesAndReturnsOffer() {
        offer.setDeliveryTimeProposed(5);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(offerRepository.findBySellerIdAndAuctionId(1L, 1L)).thenReturn(offer);
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);
        when(offerMapper.offerToOfferResponseDTO(offer)).thenReturn(new OfferResponseDTO());

        OfferResponseDTO result = offerService.updateOffer(offerRequestDTO);

        assertNotNull(result);
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    void getOfferById_WithValidId_ReturnsOffer() {
        long offerId = 1L;

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(offerMapper.offerToOfferResponseDTO(offer)).thenReturn(new OfferResponseDTO());

        OfferResponseDTO result = offerService.getOfferById(offerId);

        assertNotNull(result);
        verify(offerRepository).findById(offerId);
    }

    @Test
    void getOfferByUser_WithValidRequest_ReturnsOffer() {
        long auctionId = 1L;

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(offerRepository.findBySellerIdAndAuctionId(1L, auctionId)).thenReturn(offer);
        when(offerMapper.offerToOfferResponseDTO(offer)).thenReturn(new OfferResponseDTO());

        OfferResponseDTO result = offerService.getOfferByUser(auctionId);

        assertNotNull(result);
        verify(offerRepository).findBySellerIdAndAuctionId(1L, auctionId);
    }

    @Test
    void getAllOffersByAuction_WithValidRequest_ReturnsSortedOffers() {
        long auctionId = 1L;

        auction.setDeliveryDate(10L);

        List<Offer> offers = List.of(offer1, offer2);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(offerRepository.findAllByAuctionId(auctionId)).thenReturn(offers);
        when(offerMapper.offerToOfferResponseDTO(any(Offer.class))).thenReturn(new OfferResponseDTO());

        List<OfferResponseDTO> result = offerService.getAllOffersByAuction(auctionId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(offerRepository).findAllByAuctionId(auctionId);
    }

    @Test
    void updateOffer_WhenSaveFailed_ThrowEntityDeletionException() {
        when(offerRepository.findBySellerIdAndAuctionId(1L, 1L)).thenReturn(offer);
        when(offerRepository.save(any(Offer.class))).thenThrow(new RuntimeException());

        assertThrows(EntityDeletionException.class, () -> offerService.updateOffer(offerRequestDTO));
    }

    @Test
    void deleteOffer_WhenSaveFailed_ThrowEntityDeletionException() {
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        when(offerRepository.findBySellerIdAndAuctionId(1L, 1L)).thenReturn(offer);
        doThrow(new RuntimeException()).when(offerRepository).delete(offer);

        assertThrows(EntityDeletionException.class, () -> offerService.deleteOffer(1L));
    }
}
