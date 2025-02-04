package org.elis.progettoing.controllers;

import jakarta.validation.Valid;
import org.elis.progettoing.dto.request.auction.AuctionRequestDTO;
import org.elis.progettoing.dto.response.auction.AuctionDetailsDTO;
import org.elis.progettoing.dto.response.auction.AuctionSummaryDTO;
import org.elis.progettoing.service.definition.AuctionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing auction-related operations.
 * Provides endpoints for creating, updating, deleting, and retrieving auctions.
 */
@RestController
@RequestMapping("/api/auction")
public class AuctionController {
    private final AuctionService auctionService;

    /**
     * Constructs an instance of AuctionController with the specified AuctionService.
     *
     * @param auctionService the service used for auction operations
     */
    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    /**
     * Creates a new auction.
     *
     * @param auctionRequestDTO the auction data to be created
     * @return the details of the newly created auction
     */
    @PostMapping("/create")
    public ResponseEntity<AuctionDetailsDTO> createAuction(@Valid @RequestBody AuctionRequestDTO auctionRequestDTO) {
        return new ResponseEntity<>(auctionService.createAuction(auctionRequestDTO), HttpStatus.CREATED);
    }

    /**
     * Deletes an existing auction.
     *
     * @param auctionId the ID of the auction to be deleted
     * @return a boolean indicating the success of the operation
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteAuction(@RequestParam("auctionId") long auctionId) {
        return new ResponseEntity<>(auctionService.deleteAuction(auctionId), HttpStatus.OK);
    }

    /**
     * Updates an existing auction with new data.
     *
     * @param auctionRequestDTO the updated auction data
     * @return the details of the updated auction
     */
    @PatchMapping("/update")
    public ResponseEntity<AuctionDetailsDTO> updateAuction(@Valid @RequestBody AuctionRequestDTO auctionRequestDTO) {
        return new ResponseEntity<>(auctionService.updateAuction(auctionRequestDTO), HttpStatus.OK);
    }

    /**
     * Retrieves a summary of auctions with limited details.
     *
     * @return a list of auction summaries
     */
    @GetMapping("/auctionActiveSummary")
    public ResponseEntity<List<AuctionSummaryDTO>> auctionActiveAndPendingByUser() {
        return new ResponseEntity<>(auctionService.getActiveAuctionSummary(), HttpStatus.OK);
    }

    /**
     * Retrieves a summary of auctions with limited details.
     *
     * @return a list of auction summaries
     */
    @GetMapping("/auctionPendingSummary")
    public ResponseEntity<List<AuctionSummaryDTO>> auctionPendingSummary() {
        return new ResponseEntity<>(auctionService.getPendingAuctionSummary(), HttpStatus.OK);
    }

    /**
     * Retrieves a summary of auctions with limited details by user id.
     *
     * @return a list of auction summaries
     */
    @GetMapping("/summaryByUserId")
    public ResponseEntity<List<AuctionSummaryDTO>> auctionActiveAndPendingByUser(@RequestParam("userId") long userId) {
            return new ResponseEntity<>(auctionService.getAuctionSummaryByUserId(userId), HttpStatus.OK);
    }

    /**
     * Subscribes the user to notifications for a specific auction when auction status is PENDING.
     *
     * @param auctionId the ID of the auction to subscribe to
     * @return a boolean indicating the success of the operation
     */
    @PostMapping("/subscribeUserNotification")
    public ResponseEntity<Boolean> subscribeUserNotification(@RequestParam("auctionId") long auctionId) {
        return new ResponseEntity<>(auctionService.subscribeUserNotification(auctionId), HttpStatus.OK);
    }

    /**
     * Retrieves detailed information about a specific auction.
     *
     * @param auctionId the ID of the auction to retrieve
     * @return the details of the auction
     */
    @GetMapping("/details")
    public ResponseEntity<AuctionDetailsDTO> getAuctionDetails(@RequestParam("auctionId") long auctionId) {
        return new ResponseEntity<>(auctionService.getAuctionDetails(auctionId), HttpStatus.OK);
    }

    /**
     * Retrieves a list of all active auctions.
     *
     * @return a list of active auctions
     */
    @GetMapping("/active")
    public ResponseEntity<List<AuctionDetailsDTO>> listActiveAuctions() {
        return new ResponseEntity<>(auctionService.listActiveAuctions(), HttpStatus.OK);
    }

    /**
     * Retrieves a list of all closed auctions.
     *
     * @return a list of closed auctions
     */
    @GetMapping("/closed")
    public ResponseEntity<List<AuctionDetailsDTO>> listClosedAuctions() {
        return new ResponseEntity<>(auctionService.listClosedAuctions(), HttpStatus.OK);
    }

    /**
     * Retrieves a list of all pending auctions.
     *
     * @return a list of pending auctions
     */
    @GetMapping("/pending")
    public ResponseEntity<List<AuctionDetailsDTO>> listPendingAuctions() {
        return new ResponseEntity<>(auctionService.listPendingAuctions(), HttpStatus.OK);
    }

    /**
     * Retrieves a list of auctions the user is subscribed to for notifications.
     *
     * @return a list of subscribed auctions
     */
    @GetMapping("/subscribed")
    public ResponseEntity<List<AuctionDetailsDTO>> listSubscribedAuctions() {
        return new ResponseEntity<>(auctionService.listSubscribedAuctions(), HttpStatus.OK);
    }

    /**
     * Assigns a winner to a specific auction.
     *
     * @param auctionId the ID of the auction to assign a winner to
     * @param winnerId the ID of the user to assign as the winner
     * @return a boolean indicating the success of the operation
     */
    @PostMapping("/assignWinner")
    public ResponseEntity<Boolean> assignWinner(@RequestParam("auctionId") long auctionId, @RequestParam("winnerId") long winnerId) {
        return new ResponseEntity<>(auctionService.assignWinner(auctionId, winnerId), HttpStatus.OK);
    }

    /**
     * Retrieves a list of closed auctions of logged user.
     *
     * @return a list of won auctions
     */
    @GetMapping("/getClosedAuctionsByUser")
    public ResponseEntity<List<AuctionSummaryDTO>> getClosedAuctionsByUser() {
        return new ResponseEntity<>(auctionService.getClosedAndWithoutWinnerAuctionSummary(), HttpStatus.OK);
    }

    /**
     * Retrieves a list of pending auctions of logged user.
     *
     * @return a list of won auctions
     */
    @GetMapping("/getPendingAuctionsByUser")
    public ResponseEntity<List<AuctionSummaryDTO>> getPendingAuctionsByUser() {
        return new ResponseEntity<>(auctionService.getPendingAndWithoutWinnerAuctionSummary(), HttpStatus.OK);
    }

    /**
     * Retrieves a list of open auctions of logged user.
     *
     * @return a list of won auctions
     */
    @GetMapping("/getOpenAuctionsByUser")
    public ResponseEntity<List<AuctionSummaryDTO>> getOpenAuctionsByUser() {
        return new ResponseEntity<>(auctionService.getOpenAndWithoutWinnerAuctionSummary(), HttpStatus.OK);
    }

    /**
     * Check if the user is subscribed to notifications for a specific auction.
     * @param auctionId the ID of the auction to check
     * @return a boolean indicating if the user is subscribed to the auction
     */
    @GetMapping("/getAuctionSubscriptionByAuctionId")
    public ResponseEntity<Boolean> getAuctionSubscriptionByAuctionId(@RequestParam("auctionId") long auctionId) {
        return new ResponseEntity<>(auctionService.getAuctionSubscriptionByAuctionId(auctionId), HttpStatus.OK);
    }
}
