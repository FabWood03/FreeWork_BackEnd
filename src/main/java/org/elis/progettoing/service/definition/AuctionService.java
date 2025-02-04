package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.request.auction.AuctionRequestDTO;
import org.elis.progettoing.dto.response.auction.AuctionDetailsDTO;
import org.elis.progettoing.dto.response.auction.AuctionSummaryDTO;

import java.util.List;

/**
 * Interface for the AuctionService class. Provides methods for creating, deleting, and updating auctions,
 * as well as retrieving auction details and lists of active, closed, and pending auctions.
 */
public interface AuctionService {

    AuctionDetailsDTO createAuction(AuctionRequestDTO auctionRequestDTO);

    boolean deleteAuction(long auctionId);

    boolean subscribeUserNotification(long auctionId);

    AuctionDetailsDTO getAuctionDetails(long auctionId);

    List<AuctionDetailsDTO> listActiveAuctions();

    List<AuctionDetailsDTO> listClosedAuctions();

    List<AuctionDetailsDTO> listPendingAuctions();

    List<AuctionDetailsDTO> listSubscribedAuctions();

    AuctionDetailsDTO updateAuction(AuctionRequestDTO auctionRequestDTO);

    List<AuctionSummaryDTO> getActiveAuctionSummary();

    List<AuctionSummaryDTO> getAuctionSummaryByUserId(long userId);

    List<AuctionSummaryDTO> getPendingAuctionSummary();

    Boolean assignWinner(long auctionId, long winnerId);

    List<AuctionSummaryDTO> getClosedAndWithoutWinnerAuctionSummary();

    List<AuctionSummaryDTO> getPendingAndWithoutWinnerAuctionSummary();

    List<AuctionSummaryDTO> getOpenAndWithoutWinnerAuctionSummary();

    Boolean getAuctionSubscriptionByAuctionId(long auctionId);
}
