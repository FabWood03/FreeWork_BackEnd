package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.request.ticket.DemandSellerRequestDTO;
import org.elis.progettoing.dto.request.ticket.TicketRequestDTO;
import org.elis.progettoing.dto.response.ticket.TicketResponseDTO;
import org.elis.progettoing.enumeration.TicketType;
import org.elis.progettoing.mapper.implementation.TicketMapperImpl;
import org.elis.progettoing.models.Ticket;
import org.elis.progettoing.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TicketMapperImplTest {

    private TicketMapperImpl ticketMapperImpl;

    private TicketRequestDTO ticketRequestDTO;
    private Ticket ticket;
    DemandSellerRequestDTO demandSellerRequestDTO;

    @BeforeEach
    void setUp() {
        ticketMapperImpl = new TicketMapperImpl();

        // Initialize TicketRequestDTO
        ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setTitle("Ticket title example");
        ticketRequestDTO.setDescription("Ticket description example");

        // Initialize Ticket entity
        ticket = new Ticket();
        ticket.setTitle("Ticket title example");
        ticket.setDescription("Ticket description example");
        ticket.setTicketRequester(new User());
        ticket.setState("OPEN");
        ticket.setType(TicketType.REPORT_PRODUCT);

        // Initialize TicketResponseDTO
        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Ticket title example");
        ticketResponseDTO.setDescription("Ticket description example");
        ticketResponseDTO.setState("OPEN");
        ticketResponseDTO.setType("REPORT_PRODUCT");

        demandSellerRequestDTO = new DemandSellerRequestDTO();
        demandSellerRequestDTO.setTitle("Ticket title example");
        demandSellerRequestDTO.setDescription("Ticket description example");
    }

    @Test
    void testTicketRequestDTOToTicket_withNonNullDTO() {
        // When
        Ticket result = ticketMapperImpl.ticketRequestDTOToTicket(ticketRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Ticket title example", result.getTitle());
        assertEquals("Ticket description example", result.getDescription());
    }

    @Test
    void testTicketRequestDTOToTicket_withNullDTO() {
        // When
        Ticket result = ticketMapperImpl.ticketRequestDTOToTicket(null);

        // Then
        assertNull(result);
    }

    @Test
    void testTicketToTicketDTO_withNonNullTicket() {
        // When
        TicketResponseDTO result = ticketMapperImpl.ticketToTicketDTO(ticket);

        // Then
        assertNotNull(result);
        assertEquals("Ticket title example", result.getTitle());
        assertEquals("Ticket description example", result.getDescription());
        assertEquals("OPEN", result.getState());
        assertEquals("REPORT_PRODUCT", result.getType());
    }

    @Test
    void testTicketToTicketDTO_withNullTicket() {
        // When
        TicketResponseDTO result = ticketMapperImpl.ticketToTicketDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void testTicketToTicketDTO_withNullCreationDate() {
        ticket.setCreationDate(null);  // Set creationDate to null

        // When
        TicketResponseDTO result = ticketMapperImpl.ticketToTicketDTO(ticket);

        // Then
        assertNotNull(result, "The result should not be null.");
        assertNull(result.getCreationDate(), "The creationDate in the response DTO should be null when the ticket's creationDate is null.");
    }

    @Test
    void testTicketToTicketDTO_withNonNullCreationDate() {
        LocalDateTime creationDate = LocalDateTime.of(2025, 1, 5, 15, 30, 0, 0);
        ticket.setCreationDate(creationDate);  // Set creationDate to a non-null value

        // When
        TicketResponseDTO result = ticketMapperImpl.ticketToTicketDTO(ticket);

        // Then
        assertNotNull(result, "The result should not be null.");
        assertNotNull(result.getCreationDate(), "The creationDate in the response DTO should not be null when the ticket's creationDate is not null.");
        assertEquals(DateTimeFormatter.ISO_LOCAL_DATE.format(creationDate), result.getCreationDate(),
                "The creationDate in the response DTO should be correctly formatted.");
    }

    @Test
    void testTicketsToTicketDTOs_withNonNullTickets() {
        // Given
        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Second Ticket");
        ticket2.setDescription("Second Description");
        ticket2.setTicketRequester(new User());
        ticket2.setState("CLOSED");
        ticket2.setType(TicketType.REPORT_USER);

        // When
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);
        tickets.add(ticket2);

        List<TicketResponseDTO> result = ticketMapperImpl.ticketsToTicketDTOs(tickets);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Ticket title example", result.get(0).getTitle());
        assertEquals("Second Ticket", result.get(1).getTitle());
    }

    @Test
    void testTicketsToTicketDTOs_withNullTickets() {
        // When
        List<TicketResponseDTO> result = ticketMapperImpl.ticketsToTicketDTOs(null);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testSellerRequestDTOToTicket_withNullDTO() {
        // When
        Ticket result = ticketMapperImpl.sellerRequestDTOToTicket(null);

        // Then
        assertNull(result, "The result should be null when the input DTO is null.");
    }

    @Test
    void testSellerRequestDTOToTicket_withNonNullDTO() {
        // When
        Ticket result = ticketMapperImpl.sellerRequestDTOToTicket(demandSellerRequestDTO);

        // Then
        assertNotNull(result, "The result should not be null when the input DTO is non-null.");
        assertEquals("Ticket title example", result.getTitle(), "The title should be correctly set.");
        assertEquals("Ticket description example", result.getDescription(), "The description should be correctly set.");
    }
}
