package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.ticket.DemandSellerRequestDTO;
import org.elis.progettoing.dto.request.ticket.TicketRequestDTO;
import org.elis.progettoing.dto.response.ticket.TicketResponseDTO;
import org.elis.progettoing.mapper.definition.TicketMapper;
import org.elis.progettoing.models.Ticket;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the TicketMapper interface, which provides methods to convert
 * between Ticket-related request and response DTOs and their corresponding entity models.
 */
@Component
public class TicketMapperImpl implements TicketMapper {

    /**
     * Converts a TicketRequestDTO to a Ticket entity.
     *
     * @param ticketRequestDTO the DTO containing ticket data to be converted
     * @return a Ticket entity populated with data from the DTO, or null if the DTO is null
     */
    @Override
    public Ticket ticketRequestDTOToTicket(TicketRequestDTO ticketRequestDTO) {
        if (ticketRequestDTO == null) {
            return null;
        }

        Ticket ticket = new Ticket();

        ticket.setTitle(ticketRequestDTO.getTitle());
        ticket.setDescription(ticketRequestDTO.getDescription());

        return ticket;
    }

    /**
     * Converts a DemandSellerRequestDTO to a Ticket entity.
     *
     * @param ticketRequestDTO the DTO containing ticket data to be converted
     * @return a Ticket entity populated with data from the DTO, or null if the DTO is null
     */
    @Override
    public Ticket sellerRequestDTOToTicket(DemandSellerRequestDTO ticketRequestDTO) {
        if (ticketRequestDTO == null) {
            return null;
        }

        Ticket ticket = new Ticket();

        ticket.setTitle(ticketRequestDTO.getTitle());
        ticket.setDescription(ticketRequestDTO.getDescription());

        return ticket;
    }

    /**
     * Converts a Ticket entity to a TicketResponseDTO.
     *
     * @param ticket the Ticket entity to be converted
     * @return a TicketResponseDTO populated with data from the Ticket entity, or null if the entity is null
     */
    @Override
    public TicketResponseDTO ticketToTicketDTO(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();

        ticketResponseDTO.setUserId(ticket.getTicketRequester().getId());
        ticketResponseDTO.setUserName(ticket.getTicketRequester().getName());
        ticketResponseDTO.setUserSurname(ticket.getTicketRequester().getSurname());
        ticketResponseDTO.setUserPhoto(ticket.getTicketRequester().getUrlUserPhoto());
        ticketResponseDTO.setId(ticket.getId());
        ticketResponseDTO.setTitle(ticket.getTitle());
        ticketResponseDTO.setDescription(ticket.getDescription());
        ticketResponseDTO.setState(ticket.getState());
        ticketResponseDTO.setPriorityFlag(String.valueOf(ticket.getPriorityFlag()));
        if (ticket.getCreationDate() != null) {
            ticketResponseDTO.setCreationDate(DateTimeFormatter.ISO_LOCAL_DATE.format(ticket.getCreationDate()));
        }

        ticketResponseDTO.setType(ticket.getType().name());

        return ticketResponseDTO;
    }

    /**
     * Converts a list of Ticket entities to a list of TicketResponseDTOs.
     *
     * @param tickets the list of Ticket entities to be converted
     * @return a list of TicketResponseDTOs, or an empty list if the input list is null
     */
    @Override
    public List<TicketResponseDTO> ticketsToTicketDTOs(List<Ticket> tickets) {
        if (tickets == null) {
            return Collections.emptyList();
        }

        List<TicketResponseDTO> list = new ArrayList<>(tickets.size());
        for (Ticket ticket : tickets) {
            list.add(ticketToTicketDTO(ticket));
        }

        return list;
    }
}
