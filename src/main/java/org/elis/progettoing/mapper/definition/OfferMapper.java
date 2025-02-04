package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.request.auction.OfferRequestDTO;
import org.elis.progettoing.dto.response.auction.OfferResponseDTO;
import org.elis.progettoing.models.Offer;

/**
 * Interface for mapping between Offer entities and their respective DTOs.
 * This interface defines methods for converting an Offer entity to an OfferResponseDTO,
 * and converting an OfferRequestDTO to an Offer entity.
 */
public interface OfferMapper {

    /**
     * Converts an Offer entity to an OfferResponseDTO.
     *
     * @param offer the Offer entity to be converted
     * @return the OfferResponseDTO populated with data from the Offer entity
     */
    OfferResponseDTO offerToOfferResponseDTO(Offer offer);

    /**
     * Converts an OfferRequestDTO to an Offer entity.
     *
     * @param offerRequestDTO the OfferRequestDTO to be converted
     * @return the Offer entity populated with data from the OfferRequestDTO
     */
    Offer offerRequestDTOToOffer(OfferRequestDTO offerRequestDTO);
}
