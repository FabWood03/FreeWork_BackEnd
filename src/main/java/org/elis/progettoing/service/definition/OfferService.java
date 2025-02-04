package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.request.auction.OfferRequestDTO;
import org.elis.progettoing.dto.response.auction.OfferResponseDTO;

import java.util.List;

/**
 * Interface for the OfferService class. Provides methods for creating, updating, and deleting offers, as well as
 * retrieving offers by ID, user, and auction.
 */
public interface OfferService {

    OfferResponseDTO updateOffer(OfferRequestDTO offerRequestDTO);

    OfferResponseDTO getOfferById(long offerId);

    OfferResponseDTO getOfferByUser(long auctionId);

    OfferResponseDTO createOffer(OfferRequestDTO offerRequestDTO);

    boolean deleteOffer(long auctionId);

    List<OfferResponseDTO> getAllOffersByAuction(long auctionId);
}
