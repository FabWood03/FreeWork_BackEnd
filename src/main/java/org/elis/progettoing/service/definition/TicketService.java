package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.request.ticket.DemandSellerRequestDTO;
import org.elis.progettoing.dto.request.ticket.TicketFilterRequest;
import org.elis.progettoing.dto.request.ticket.TicketRequestDTO;
import org.elis.progettoing.dto.response.ticket.FilteredTicketsResponse;
import org.elis.progettoing.dto.response.ticket.TicketResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface for the TicketService class. Provides methods for creating, updating, and deleting tickets, as well as
 * retrieving tickets by ID, and filtering tickets.
 */
public interface TicketService {
    TicketResponseDTO requestToBeSeller(DemandSellerRequestDTO demandSellerRequestDTO, MultipartFile userPhoto, List<MultipartFile> portfolio);

    TicketResponseDTO reportReviews(TicketRequestDTO ticketRequestDTO);

    TicketResponseDTO reportUser(TicketRequestDTO ticketRequestDTO);

    TicketResponseDTO reportProduct(TicketRequestDTO ticketRequestDTO);

    TicketResponseDTO getTicketById(long id);

    List<TicketResponseDTO> getAllTickets();

    TicketResponseDTO acceptTicket(long id, String description);

    TicketResponseDTO refuseTicket(long id, String description);

    TicketResponseDTO takeOnTicket(long id);

    FilteredTicketsResponse getTicketFiltered(TicketFilterRequest ticketFilterRequest);

    List<TicketResponseDTO> getResolvedTickets();

    List<TicketResponseDTO> getTakenOnTickets();

    List<TicketResponseDTO> getPendingTickets();
}
