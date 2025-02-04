package org.elis.progettoing.dto.response.ticket;

import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the response for a set of filtered tickets.
 * This class organizes tickets into different categories based on their status, such as
 * pending, resolved, and taken on tickets.
 *
 * <p>The {@link FilteredTicketsResponse} class provides separate lists for all tickets
 * and for tickets that are pending, resolved, or assigned to someone.</p>
 */
@Data
public class FilteredTicketsResponse {

    private List<TicketResponseDTO> allTickets;

    private List<TicketResponseDTO> pendingTickets;

    private List<TicketResponseDTO> resolvedTickets;

    private List<TicketResponseDTO> takeOnTickets;
}
