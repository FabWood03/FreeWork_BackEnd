package org.elis.progettoing.controllers;

import jakarta.validation.Valid;
import org.elis.progettoing.dto.request.ticket.DemandSellerRequestDTO;
import org.elis.progettoing.dto.request.ticket.TicketFilterRequest;
import org.elis.progettoing.dto.request.ticket.TicketRequestDTO;
import org.elis.progettoing.dto.response.ticket.FilteredTicketsResponse;
import org.elis.progettoing.dto.response.ticket.TicketResponseDTO;
import org.elis.progettoing.service.definition.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for managing ticket-related operations.
 * Provides endpoints for creating, updating, and retrieving tickets,
 * as well as handling seller requests and filtering tickets.
 */
@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    private final TicketService ticketService;

    /**
     * Constructs an instance of {@code TicketController}.
     *
     * @param ticketService the service managing ticket-related business logic.
     */
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Endpoint for submitting an application to become a seller
     *
     * @param demandSellerRequestDTO the data for the seller request.
     * @param userPhoto              an optional photo of the user.
     * @param portfolio              an optional portfolio as a list of files.
     * @return a {@link ResponseEntity} containing the created {@link TicketResponseDTO}
     * and HTTP status 201 (Created).
     */
    @PostMapping("/sellerRequest")
    public ResponseEntity<TicketResponseDTO> sellerRequest(@Valid @RequestPart("demandSeller") DemandSellerRequestDTO demandSellerRequestDTO,
                                                           @RequestPart(value = "userPhoto") MultipartFile userPhoto,
                                                           @RequestPart(value = "portfolio") List<MultipartFile> portfolio) {
        return new ResponseEntity<>(ticketService.requestToBeSeller(demandSellerRequestDTO, userPhoto, portfolio), HttpStatus.CREATED);
    }

    /**
     * Endpoint to report a review.
     *
     * @param ticketRequestDTO the data for the review report.
     * @return a {@link ResponseEntity} containing the created {@link TicketResponseDTO}
     * and HTTP status 201 (Created).
     */
    @PostMapping("/reportReviews")
    public ResponseEntity<TicketResponseDTO> reportReviews(@Valid @RequestBody TicketRequestDTO ticketRequestDTO) {
        return new ResponseEntity<>(ticketService.reportReviews(ticketRequestDTO), HttpStatus.CREATED);
    }

    /**
     * Endpoint to report a user.
     *
     * @param ticketRequestDTO the data for the user report.
     * @return a {@link ResponseEntity} containing the created {@link TicketResponseDTO}
     * and HTTP status 201 (Created).
     */
    @PostMapping("/reportUser")
    public ResponseEntity<TicketResponseDTO> reportUser(@Valid @RequestBody TicketRequestDTO ticketRequestDTO) {
        return new ResponseEntity<>(ticketService.reportUser(ticketRequestDTO), HttpStatus.CREATED);
    }

    /**
     * Endpoint to report a product.
     *
     * @param ticketRequestDTO the data for the product report.
     * @return a {@link ResponseEntity} containing the created {@link TicketResponseDTO}
     * and HTTP status 201 (Created).
     */
    @PostMapping("/reportProduct")
    public ResponseEntity<TicketResponseDTO> reportProduct(@Valid @RequestBody TicketRequestDTO ticketRequestDTO) {
        return new ResponseEntity<>(ticketService.reportProduct(ticketRequestDTO), HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve a ticket by its ID.
     *
     * @param id the ID of the ticket.
     * @return a {@link ResponseEntity} containing the {@link TicketResponseDTO}
     * and HTTP status 200 (OK).
     */
    @GetMapping("/getTicketById")
    public ResponseEntity<TicketResponseDTO> getTicketsById(@RequestParam("id") long id) {
        return new ResponseEntity<>(ticketService.getTicketById(id), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve all tickets.
     *
     * @return a {@link ResponseEntity} containing a list of {@link TicketResponseDTO}
     * and HTTP status 200 (OK).
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets() {
        return new ResponseEntity<>(ticketService.getAllTickets(), HttpStatus.OK);
    }

    /**
     * Endpoint to accept a ticket.
     *
     * @param id the ID of the ticket to accept.
     * @return a {@link ResponseEntity} containing the updated {@link TicketResponseDTO}
     * and HTTP status 200 (OK).
     */
    @PostMapping("/acceptTicket")
    public ResponseEntity<TicketResponseDTO> acceptTicket(@RequestParam("id") long id, @RequestParam("description") String description) {
        return new ResponseEntity<>(ticketService.acceptTicket(id, description), HttpStatus.OK);
    }

    /**
     * Endpoint to refuse a ticket.
     *
     * @param id the ID of the ticket to refuse.
     * @return a {@link ResponseEntity} containing the updated {@link TicketResponseDTO}
     * and HTTP status 200 (OK).
     */
    @PostMapping("/refuseTicket")
    public ResponseEntity<TicketResponseDTO> refuseTicket(@RequestParam("id") long id, @RequestParam("description") String description) {
        return new ResponseEntity<>(ticketService.refuseTicket(id, description), HttpStatus.OK);
    }

    /**
     * Endpoint to take on a ticket.
     *
     * @param id the ID of the ticket to take on.
     * @return a {@link ResponseEntity} containing the updated {@link TicketResponseDTO}
     * and HTTP status 200 (OK).
     */
    @PostMapping("/takeOnTicket")
    public ResponseEntity<TicketResponseDTO> takeOnTicket(@RequestParam("id") long id) {
        return new ResponseEntity<>(ticketService.takeOnTicket(id), HttpStatus.OK);
    }

    /**
     * Endpoint to filter tickets based on specific criteria.
     *
     * @param ticketFilterRequest the filtering criteria.
     * @return a {@link FilteredTicketsResponse} containing the filtered tickets.
     */
    @PostMapping("/filter")
    public FilteredTicketsResponse filterTickets(@Valid @RequestBody TicketFilterRequest ticketFilterRequest) {
        return ticketService.getTicketFiltered(ticketFilterRequest);
    }

    /**
     * Endpoint to retrieve resolved tickets.
     *
     * @return a {@link ResponseEntity} containing a list of resolved {@link TicketResponseDTO}
     * and HTTP status 200 (OK).
     */
    @GetMapping("/getResolvedTickets")
    public ResponseEntity<List<TicketResponseDTO>> getTicketRefused() {
        return new ResponseEntity<>(ticketService.getResolvedTickets(), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve tickets currently taken on.
     *
     * @return a {@link ResponseEntity} containing a list of taken-on {@link TicketResponseDTO}
     * and HTTP status 200 (OK).
     */
    @GetMapping("/getTakeOnTickets")
    public ResponseEntity<List<TicketResponseDTO>> getTakenOnTickets() {
        return new ResponseEntity<>(ticketService.getTakenOnTickets(), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve tickets currently pending.
     *
     * @return a {@link ResponseEntity} containing a list of pending {@link TicketResponseDTO}
     * and HTTP status 200 (OK).
     */
    @GetMapping("/getPendingTickets")
    public ResponseEntity<List<TicketResponseDTO>> getPendingTickets() {
        return new ResponseEntity<>(ticketService.getPendingTickets(), HttpStatus.OK);
    }
}
