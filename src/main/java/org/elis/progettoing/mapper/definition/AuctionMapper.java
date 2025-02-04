package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.request.auction.AuctionRequestDTO;
import org.elis.progettoing.dto.response.auction.AuctionDetailsDTO;
import org.elis.progettoing.dto.response.auction.AuctionSummaryDTO;
import org.elis.progettoing.models.auction.Auction;

/**
 * Interface for mapping between Auction-related data transfer objects (DTOs)
 * and the corresponding Auction entity.
 * Provides methods for converting AuctionRequestDTO to Auction,
 * and mapping Auction entities to their respective response DTOs.
 */
public interface AuctionMapper {

    /**
     * Converts an AuctionRequestDTO to an Auction entity.
     *
     * @param auctionRequestDTO the DTO containing auction request data
     * @return the Auction entity populated with data from the DTO
     */
    Auction auctionRequestDTOToAuction(AuctionRequestDTO auctionRequestDTO);

    /**
     * Converts an Auction entity to an AuctionDetailsDTO.
     *
     * @param auction the Auction entity to be converted
     * @return the AuctionDetailsDTO populated with data from the Auction entity
     */
    AuctionDetailsDTO auctionToAuctionResponseDTO(Auction auction);

    /**
     * Converts an Auction entity to an AuctionSummaryDTO.
     *
     * @param auction the Auction entity to be converted
     * @return the AuctionSummaryDTO populated with summary data from the Auction entity
     */
    AuctionSummaryDTO auctionToAuctionSummaryDTO(Auction auction);
}
