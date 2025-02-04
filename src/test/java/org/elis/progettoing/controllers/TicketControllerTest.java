package org.elis.progettoing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elis.progettoing.dto.request.ticket.DemandSellerRequestDTO;
import org.elis.progettoing.dto.request.ticket.TicketFilterRequest;
import org.elis.progettoing.dto.request.ticket.TicketRequestDTO;
import org.elis.progettoing.dto.response.ticket.FilteredTicketsResponse;
import org.elis.progettoing.dto.response.ticket.TicketResponseDTO;
import org.elis.progettoing.service.definition.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
    }

    @Test
    void testSellerRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        DemandSellerRequestDTO demandSellerRequestDTO = new DemandSellerRequestDTO();
        demandSellerRequestDTO.setTitle("Request to be Seller");
        demandSellerRequestDTO.setDescription("I want to sell products on the platform.");
        demandSellerRequestDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        demandSellerRequestDTO.setEducation("Bachelor's degree in Computer Science");
        demandSellerRequestDTO.setFiscalCode("ABCDEF1234567890");
        demandSellerRequestDTO.setSkills(Collections.singletonList("Programming"));
        demandSellerRequestDTO.setLanguages(Collections.singletonList("English")); // Campo richiesto
        demandSellerRequestDTO.setBasedIn("Italy"); // Campo richiesto

        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Seller Request");
        ticketResponseDTO.setDescription("This is a seller request.");
        ticketResponseDTO.setState("Pending");
        ticketResponseDTO.setType("Seller");

        MockMultipartFile userPhoto = new MockMultipartFile("userPhoto", "photo.jpg", "image/jpeg", "userPhotoContent".getBytes());
        MockMultipartFile portfolioFile = new MockMultipartFile("portfolio", "portfolio.pdf", "application/pdf", "portfolioContent".getBytes());

        when(ticketService.requestToBeSeller(demandSellerRequestDTO, userPhoto, Collections.singletonList(portfolioFile)))
                .thenReturn(ticketResponseDTO);

        String demandSellerJson = objectMapper.writeValueAsString(demandSellerRequestDTO);

        MockMultipartFile productRequestDTOFile = new MockMultipartFile(
                "demandSeller",
                "demandSeller",
                "application/json",
                demandSellerJson.getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/ticket/sellerRequest")
                        .file(userPhoto)
                        .file(portfolioFile)
                        .file(productRequestDTOFile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Seller Request"))
                .andExpect(jsonPath("$.state").value("Pending"))
                .andExpect(jsonPath("$.type").value("Seller"));

        verify(ticketService).requestToBeSeller(demandSellerRequestDTO, userPhoto, Collections.singletonList(portfolioFile));
    }

    @Test
    void testReportUser() throws Exception {
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setTitle("Report User");
        ticketRequestDTO.setDescription("This is a report for a user");
        ticketRequestDTO.setReportedUserId(1L);

        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Report User");
        ticketResponseDTO.setDescription("This is a report for a user");
        ticketResponseDTO.setState("Reported");
        ticketResponseDTO.setType("User");

        when(ticketService.reportUser(ticketRequestDTO)).thenReturn(ticketResponseDTO);

        mockMvc.perform(post("/api/ticket/reportUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Report User\",\"description\":\"This is a report for a user\",\"reportedUserId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Report User"))
                .andExpect(jsonPath("$.state").value("Reported"));

        verify(ticketService).reportUser(ticketRequestDTO);
    }

    @Test
    void testReportProduct() throws Exception {
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setTitle("Report Product");
        ticketRequestDTO.setDescription("This is a report for a product");
        ticketRequestDTO.setReportedProductId(1L);

        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Report Product");
        ticketResponseDTO.setDescription("This is a report for a product");
        ticketResponseDTO.setState("Reported");
        ticketResponseDTO.setType("Product");

        when(ticketService.reportProduct(ticketRequestDTO)).thenReturn(ticketResponseDTO);

        mockMvc.perform(post("/api/ticket/reportProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Report Product\",\"description\":\"This is a report for a product\",\"reportedProductId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Report Product"))
                .andExpect(jsonPath("$.state").value("Reported"));

        verify(ticketService).reportProduct(ticketRequestDTO);
    }

    @Test
    void testReportReview() throws Exception {
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setTitle("Report Review");
        ticketRequestDTO.setDescription("This is a report for a review");
        ticketRequestDTO.setReportedReviewId(1L);

        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Report Review");
        ticketResponseDTO.setDescription("This is a report for a review");
        ticketResponseDTO.setState("Reported");
        ticketResponseDTO.setType("Review");

        when(ticketService.reportReviews(ticketRequestDTO)).thenReturn(ticketResponseDTO);

        mockMvc.perform(post("/api/ticket/reportReviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Report Review\",\"description\":\"This is a report for a review\",\"reportedReviewId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Report Review"))
                .andExpect(jsonPath("$.state").value("Reported"));


        verify(ticketService).reportReviews(ticketRequestDTO);
    }

    @Test
    void testGetTicketsById() throws Exception {
        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Test Ticket");
        ticketResponseDTO.setDescription("Test description");
        ticketResponseDTO.setState("Open");
        ticketResponseDTO.setType("Seller Request");

        when(ticketService.getTicketById(1L)).thenReturn(ticketResponseDTO);

        mockMvc.perform(get("/api/ticket/getTicketById")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Ticket"))
                .andExpect(jsonPath("$.state").value("Open"));

        verify(ticketService).getTicketById(1L);
    }

    @Test
    void testGetAllTickets() throws Exception {
        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Test Ticket");
        ticketResponseDTO.setDescription("Test description");
        ticketResponseDTO.setState("Open");
        ticketResponseDTO.setType("Seller Request");

        when(ticketService.getAllTickets()).thenReturn(Collections.singletonList(ticketResponseDTO));

        mockMvc.perform(get("/api/ticket/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Ticket"))
                .andExpect(jsonPath("$[0].state").value("Open"));

        verify(ticketService).getAllTickets();
    }

    @Test
    void testAcceptTicket() throws Exception {
        String description = "Description for accepting the ticket";  // Provide a valid description

        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Test Ticket");
        ticketResponseDTO.setDescription(description);
        ticketResponseDTO.setState("Accepted");
        ticketResponseDTO.setType("Seller Request");

        // Mock the service call
        when(ticketService.acceptTicket(1L, description)).thenReturn(ticketResponseDTO);

        // Perform the POST request with both 'id' and 'description' parameters
        mockMvc.perform(post("/api/ticket/acceptTicket")
                        .param("id", "1")
                        .param("description", description))  // Add the description parameter
                .andExpect(status().isOk())  // Check for 200 OK status
                .andExpect(jsonPath("$.id").value(1L))  // Check ticket ID
                .andExpect(jsonPath("$.state").value("Accepted"));  // Check ticket state

        // Verify that the service method was called with the correct parameters
        verify(ticketService).acceptTicket(1L, description);
    }

    @Test
    void testRefuseTicket() throws Exception {
        String description = "Description for refusing the ticket";  // Provide a valid description

        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Test Ticket");
        ticketResponseDTO.setDescription(description);
        ticketResponseDTO.setState("Refused");
        ticketResponseDTO.setType("Seller Request");

        // Mock the service call
        when(ticketService.refuseTicket(1L, description)).thenReturn(ticketResponseDTO);

        // Perform the POST request with both 'id' and 'description' parameters
        mockMvc.perform(post("/api/ticket/refuseTicket")
                        .param("id", "1")
                        .param("description", description))  // Add the description parameter
                .andExpect(status().isOk())  // Check for 200 OK status
                .andExpect(jsonPath("$.id").value(1L))  // Check ticket ID
                .andExpect(jsonPath("$.state").value("Refused"));  // Check ticket state

        // Verify that the service method was called with the correct parameters
        verify(ticketService).refuseTicket(1L, description);
    }

    @Test
    void testTakeOnTicket() throws Exception {
        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Test Ticket");
        ticketResponseDTO.setDescription("Test description");
        ticketResponseDTO.setState("Taken");
        ticketResponseDTO.setType("Seller Request");

        when(ticketService.takeOnTicket(1L)).thenReturn(ticketResponseDTO);

        mockMvc.perform(post("/api/ticket/takeOnTicket")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.state").value("Taken"));

        verify(ticketService).takeOnTicket(1L);
    }

    @Test
    void testGetResolvedTickets() throws Exception {
        // Prepara la risposta mock
        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Resolved Ticket");
        ticketResponseDTO.setDescription("Test description");
        ticketResponseDTO.setState("Resolved");
        ticketResponseDTO.setType("Seller Request");

        when(ticketService.getResolvedTickets()).thenReturn(Collections.singletonList(ticketResponseDTO));

        mockMvc.perform(get("/api/ticket/getResolvedTickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Resolved Ticket"))
                .andExpect(jsonPath("$[0].state").value("Resolved"));

        verify(ticketService).getResolvedTickets();
    }

    @Test
    void testGetTakenOnTickets() throws Exception {
        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Taken On Ticket");
        ticketResponseDTO.setDescription("Test description");
        ticketResponseDTO.setState("Taken");
        ticketResponseDTO.setType("Seller Request");

        when(ticketService.getTakenOnTickets()).thenReturn(Collections.singletonList(ticketResponseDTO));

        mockMvc.perform(get("/api/ticket/getTakeOnTickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Taken On Ticket"))
                .andExpect(jsonPath("$[0].state").value("Taken"));

        verify(ticketService).getTakenOnTickets();
    }

    @Test
    void testGetPendingTickets() throws Exception {
        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Pending Ticket");
        ticketResponseDTO.setDescription("Test description");
        ticketResponseDTO.setState("Pending");
        ticketResponseDTO.setType("Seller Request");

        when(ticketService.getPendingTickets()).thenReturn(Collections.singletonList(ticketResponseDTO));

        mockMvc.perform(get("/api/ticket/getPendingTickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Pending Ticket"))
                .andExpect(jsonPath("$[0].state").value("Pending"));

        verify(ticketService).getPendingTickets();
    }

    @Test
    void testGetTicketById() throws Exception {
        // Prepara la risposta mock
        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Test Ticket");
        ticketResponseDTO.setDescription("Test description");
        ticketResponseDTO.setState("Open");
        ticketResponseDTO.setType("Seller Request");

        when(ticketService.getTicketById(1L)).thenReturn(ticketResponseDTO);

        mockMvc.perform(get("/api/ticket/getTicketById")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Ticket"))
                .andExpect(jsonPath("$.state").value("Open"));

        verify(ticketService).getTicketById(1L);
    }

    @Test
    void testFilterTickets() throws Exception {
        TicketFilterRequest ticketFilterRequest = new TicketFilterRequest();
//        ticketFilterRequest.setReportedProducts(true);
        ticketFilterRequest.setPriority("High");

        FilteredTicketsResponse filteredTicketsResponse = new FilteredTicketsResponse();
        TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
        ticketResponseDTO.setId(1L);
        ticketResponseDTO.setTitle("Filtered Ticket");
        filteredTicketsResponse.setAllTickets(Collections.singletonList(ticketResponseDTO));

        when(ticketService.getTicketFiltered(ticketFilterRequest)).thenReturn(filteredTicketsResponse);

        mockMvc.perform(post("/api/ticket/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(ticketFilterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allTickets[0].id").value(1L))
                .andExpect(jsonPath("$.allTickets[0].title").value("Filtered Ticket"));

        verify(ticketService).getTicketFiltered(ticketFilterRequest);
    }
}

