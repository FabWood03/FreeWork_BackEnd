package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.auction.OfferRequestDTO;
import org.elis.progettoing.dto.response.auction.OfferResponseDTO;
import org.elis.progettoing.mapper.definition.OfferMapper;
import org.elis.progettoing.models.Offer;
import org.elis.progettoing.models.auction.Auction;
import org.springframework.stereotype.Component;

/**
 * Implementation of the OfferMapper interface. Provides methods to map between
 * Offer entities, OfferRequestDTOs, and OfferResponseDTOs.
 */
@Component
public class OfferMapperImpl implements OfferMapper {

    private final UserMapperImpl userMapperImpl;

    /**
     * Constructs a new OfferMapperImpl with the specified UserMapper.
     *
     * @param userMapperImpl the UserMapper to be used for mapping
     */
    public OfferMapperImpl(UserMapperImpl userMapperImpl) {
        this.userMapperImpl = userMapperImpl;
    }

    /**
     * Converts an Offer entity to an OfferResponseDTO.
     *
     * @param offer the Offer entity to be converted
     * @return an OfferResponseDTO populated with the Offer entity data, or null if the offer is null
     */
    @Override
    public OfferResponseDTO offerToOfferResponseDTO(Offer offer) {
        if (offer == null) {
            return null;
        }

        OfferResponseDTO offerResponseDTO = new OfferResponseDTO();

        offerResponseDTO.setAuctionId(offerAuctionId(offer));
        offerResponseDTO.setId(offer.getId());
        offerResponseDTO.setDeliveryTimeProposed(offer.getDeliveryTimeProposed());
        offerResponseDTO.setPrice(offer.getPrice());
        offerResponseDTO.setSeller(userMapperImpl.userToUserResponseDTO(offer.getSeller()));

        return offerResponseDTO;
    }

    /**
     * Converts an OfferRequestDTO to an Offer entity.
     *
     * @param offerRequestDTO the OfferRequestDTO to be converted
     * @return an Offer entity populated with the OfferRequestDTO data, or null if the requestDTO is null
     */
    @Override
    public Offer offerRequestDTOToOffer(OfferRequestDTO offerRequestDTO) {
        if (offerRequestDTO == null) {
            return null;
        }

        Offer offer = new Offer();

        offer.setAuction(offerRequestDTOToAuction(offerRequestDTO));
        offer.setDeliveryTimeProposed(offerRequestDTO.getDeliveryTimeProposed());
        offer.setPrice(offerRequestDTO.getPrice());

        return offer;
    }

    /**
     * Retrieves the ID of the Auction related to the Offer.
     *
     * @param offer the Offer from which to extract the Auction ID
     * @return the ID of the Auction, or 0L if no Auction is found
     */
    public long offerAuctionId(Offer offer) {
        if (offer == null) {
            return 0L;
        }
        Auction auction = offer.getAuction();
        if (auction == null) {
            return 0L;
        }
        return auction.getId();
    }

    /**
     * Converts an OfferRequestDTO to an Auction entity.
     *
     * @param offerRequestDTO the OfferRequestDTO containing the Auction ID
     * @return an Auction entity populated with the data from the OfferRequestDTO, or null if the requestDTO is null
     */
    public Auction offerRequestDTOToAuction(OfferRequestDTO offerRequestDTO) {
        if (offerRequestDTO == null) {
            return null;
        }

        Auction auction = new Auction();

        auction.setId(offerRequestDTO.getAuctionId());

        return auction;
    }
}
