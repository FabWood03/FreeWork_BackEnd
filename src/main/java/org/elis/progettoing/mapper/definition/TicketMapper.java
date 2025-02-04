package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.request.ticket.DemandSellerRequestDTO;
import org.elis.progettoing.dto.request.ticket.TicketRequestDTO;
import org.elis.progettoing.dto.response.ticket.TicketResponseDTO;
import org.elis.progettoing.models.Ticket;

import java.util.List;

/**
 * Interface for mapping between Ticket entities, TicketRequestDTOs, and TicketResponseDTOs.
 * This interface defines methods for converting between Ticket request DTOs and response DTOs,
 * as well as mapping Ticket entities to their corresponding DTO representations.
 */
public interface TicketMapper {

    /**
     * Converts a TicketRequestDTO to a Ticket entity.
     *
     * @param ticketRequestDTO the TicketRequestDTO to be converted
     * @return the Ticket entity populated with data from the TicketRequestDTO
     */
    Ticket ticketRequestDTOToTicket(TicketRequestDTO ticketRequestDTO);

    /**
     * Converts a DemandSellerRequestDTO to a Ticket entity.
     *
     * @param ticketRequestDTO the DemandSellerRequestDTO to be converted
     * @return the Ticket entity populated with data from the DemandSellerRequestDTO
     */
    Ticket sellerRequestDTOToTicket(DemandSellerRequestDTO ticketRequestDTO);

    /**
     * Converts a Ticket entity to a TicketResponseDTO.
     *
     * @param ticket the Ticket entity to be converted
     * @return the TicketResponseDTO populated with data from the Ticket entity
     */
    TicketResponseDTO ticketToTicketDTO(Ticket ticket);

    /**
     * Converts a list of Ticket entities to a list of TicketResponseDTOs.
     *
     * @param tickets the list of Ticket entities to be converted
     * @return the list of TicketResponseDTOs populated with data from the Ticket entities
     */
    List<TicketResponseDTO> ticketsToTicketDTOs(List<Ticket> tickets);
}
