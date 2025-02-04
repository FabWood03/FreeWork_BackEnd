package org.elis.progettoing.controllers;

import jakarta.validation.Valid;
import org.elis.progettoing.dto.request.auction.OfferRequestDTO;
import org.elis.progettoing.dto.response.auction.OfferResponseDTO;
import org.elis.progettoing.service.definition.OfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing offers in auctions.
 * Provides endpoints to create, update, delete, and retrieve offer data.
 */
@RestController
@RequestMapping("/api/offer")
public class OfferController {

    private final OfferService offerService;

    /**
     * Constructs an instance of {@code OfferController}.
     *
     * @param offerService the service managing offer-related business logic.
     */
    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    /**
     * Endpoint to create a new offer for an auction.
     *
     * @param offerRequestDTO the request data containing offer details to create.
     * @return a {@link ResponseEntity} containing the created {@link OfferResponseDTO} and HTTP status 201 (Created).
     */
    @PostMapping("/create")
    public ResponseEntity<OfferResponseDTO> createOffer(@Valid @RequestBody OfferRequestDTO offerRequestDTO) {
        return new ResponseEntity<>(offerService.createOffer(offerRequestDTO), HttpStatus.CREATED);
    }

    /**
     * Endpoint to delete an existing offer for an auction.
     *
     * @param auctionId the ID of the auction for which the offer is to be deleted.
     * @return a {@link ResponseEntity} containing a boolean indicating the success of the operation and HTTP status 200 (OK).
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteOffer(@RequestParam("auctionId") long auctionId) {
        return new ResponseEntity<>(offerService.deleteOffer(auctionId), HttpStatus.OK);
    }

    /**
     * Endpoint to update an existing offer.
     *
     * @param offerRequestDTO the request data containing updated offer details.
     * @return a {@link ResponseEntity} containing the updated {@link OfferResponseDTO} and HTTP status 200 (OK).
     */
    @PatchMapping("/update")
    public ResponseEntity<OfferResponseDTO> updateOffer(@Valid @RequestBody OfferRequestDTO offerRequestDTO) {
        return new ResponseEntity<>(offerService.updateOffer(offerRequestDTO), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve a specific offer by its ID.
     *
     * @param offerId the ID of the offer to retrieve.
     * @return a {@link ResponseEntity} containing the {@link OfferResponseDTO} for the specified offer and HTTP status 200 (OK).
     */
    @GetMapping("/getOfferById")
    public ResponseEntity<OfferResponseDTO> getOfferById(@RequestParam("offerId") long offerId) {
        return new ResponseEntity<>(offerService.getOfferById(offerId), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve all offers associated with a specific auction.
     *
     * @param auctionId the ID of the auction for which offers are to be retrieved.
     * @return a {@link ResponseEntity} containing a list of {@link OfferResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getOffersByAuctionId")
    public ResponseEntity<List<OfferResponseDTO>> getOffersByAuctionId(@RequestParam("auctionId") long auctionId) {
        return new ResponseEntity<>(offerService.getAllOffersByAuction(auctionId), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve the current user's offer for a specific auction.
     *
     * @param auctionId the ID of the auction for which the user's offer is to be retrieved.
     * @return a {@link ResponseEntity} containing the {@link OfferResponseDTO} of the user's offer and HTTP status 200 (OK).
     */
    @GetMapping("/getOfferByUser")
    public ResponseEntity<OfferResponseDTO> getOfferByUser(@RequestParam("auctionId") long auctionId) {
        return new ResponseEntity<>(offerService.getOfferByUser(auctionId), HttpStatus.OK);
    }
}
