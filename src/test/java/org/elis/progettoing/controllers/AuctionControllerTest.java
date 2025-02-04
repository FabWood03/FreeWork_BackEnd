package org.elis.progettoing.controllers;

import org.elis.progettoing.dto.request.auction.AuctionRequestDTO;
import org.elis.progettoing.dto.response.auction.AuctionDetailsDTO;
import org.elis.progettoing.dto.response.auction.AuctionSummaryDTO;
import org.elis.progettoing.service.definition.AuctionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuctionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuctionService auctionService;

    @InjectMocks
    private AuctionController auctionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(auctionController).build();
    }

    @Test
    void testCreateAuction() throws Exception {
        AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO();
        auctionDetailsDTO.setId(1L);

        when(auctionService.createAuction(Mockito.any(AuctionRequestDTO.class))).thenReturn(auctionDetailsDTO);

        // Sending a valid JSON body with all required fields
        mockMvc.perform(post("/api/auction/create")
                        .contentType("application/json")
                        .content("{"
                                + "\"descriptionProduct\":\"This is a test product description.\","
                                + "\"title\":\"Test Auction Title\","
                                + "\"startAuctionDate\":\"2025-02-16T00:00:00\","
                                + "\"endAuctionDate\":\"2025-03-17T00:00:00\","
                                + "\"deliveryDate\":1674048000000"
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testDeleteAuction() throws Exception {
        long auctionId = 1L;
        when(auctionService.deleteAuction(auctionId)).thenReturn(true);

        mockMvc.perform(delete("/api/auction/delete")
                        .param("auctionId", String.valueOf(auctionId)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testUpdateAuction() throws Exception {
        AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO();
        auctionDetailsDTO.setId(1L);

        when(auctionService.updateAuction(Mockito.any(AuctionRequestDTO.class))).thenReturn(auctionDetailsDTO);

        // Perform the test with the updated auction request data
        mockMvc.perform(patch("/api/auction/update")
                        .contentType("application/json")
                        .content("{\"title\":\"Updated Auction\"," +
                                "\"startAuctionDate\":\"2025-03-16T10:00:00\",\"endAuctionDate\":\"2025-03-19T10:00:00\"," +
                                "\"descriptionProduct\":\"Updated product description\",\"deliveryDate\":1700000000000}"))
                .andExpect(status().isOk())  // Expecting 200 OK status
                .andExpect(jsonPath("$.id").value(1L));  // Assert the updated auction ID
    }

    @Test
    void testActiveAuctionSummary() throws Exception {
        AuctionSummaryDTO summaryDTO = new AuctionSummaryDTO();
        List<AuctionSummaryDTO> summaries = List.of(summaryDTO);

        when(auctionService.getActiveAuctionSummary()).thenReturn(summaries);

        mockMvc.perform(get("/api/auction/auctionActiveSummary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testPendingAuctionSummary() throws Exception {
        AuctionSummaryDTO summaryDTO = new AuctionSummaryDTO();
        List<AuctionSummaryDTO> summaries = List.of(summaryDTO);

        when(auctionService.getPendingAuctionSummary()).thenReturn(summaries);

        mockMvc.perform(get("/api/auction/auctionPendingSummary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testAuctionSummaryByUserId() throws Exception {
        long userId = 1L;
        AuctionSummaryDTO summaryDTO = new AuctionSummaryDTO();
        List<AuctionSummaryDTO> summaries = List.of(summaryDTO);

        when(auctionService.getAuctionSummaryByUserId(userId)).thenReturn(summaries);

        mockMvc.perform(get("/api/auction/summaryByUserId")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testSubscribeUserNotification() throws Exception {
        long auctionId = 1L;
        when(auctionService.subscribeUserNotification(auctionId)).thenReturn(true);

        mockMvc.perform(post("/api/auction/subscribeUserNotification")
                        .param("auctionId", String.valueOf(auctionId)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));  // Assert that the response is simply "true"
    }

    @Test
    void testGetAuctionDetails() throws Exception {
        long auctionId = 1L;
        AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO();
        auctionDetailsDTO.setId(auctionId);

        when(auctionService.getAuctionDetails(auctionId)).thenReturn(auctionDetailsDTO);

        mockMvc.perform(get("/api/auction/details")
                        .param("auctionId", String.valueOf(auctionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(auctionId));
    }

    @Test
    void testListActiveAuctions() throws Exception {
        AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO();
        List<AuctionDetailsDTO> auctionDetails = List.of(auctionDetailsDTO);

        when(auctionService.listActiveAuctions()).thenReturn(auctionDetails);

        mockMvc.perform(get("/api/auction/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testListClosedAuctions() throws Exception {
        AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO();
        List<AuctionDetailsDTO> auctionDetails = List.of(auctionDetailsDTO);

        when(auctionService.listClosedAuctions()).thenReturn(auctionDetails);

        mockMvc.perform(get("/api/auction/closed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testListPendingAuctions() throws Exception {
        AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO();
        List<AuctionDetailsDTO> auctionDetails = List.of(auctionDetailsDTO);

        when(auctionService.listPendingAuctions()).thenReturn(auctionDetails);

        mockMvc.perform(get("/api/auction/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testListSubscribedAuctions() throws Exception {
        AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO();
        List<AuctionDetailsDTO> auctionDetails = List.of(auctionDetailsDTO);

        when(auctionService.listSubscribedAuctions()).thenReturn(auctionDetails);

        mockMvc.perform(get("/api/auction/subscribed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void assignWinner_WithValidAuctionAndWinner_ReturnsTrue() throws Exception {
        long auctionId = 1L;
        long winnerId = 2L;

        when(auctionService.assignWinner(auctionId, winnerId)).thenReturn(true);

        mockMvc.perform(post("/api/auction/assignWinner")
                        .param("auctionId", String.valueOf(auctionId))
                        .param("winnerId", String.valueOf(winnerId)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getClosedAuctionsByUser_ReturnsListOfAuctionSummaryDTO() throws Exception {
        AuctionSummaryDTO auctionSummaryDTO = new AuctionSummaryDTO();
        auctionSummaryDTO.setId(1L);
        auctionSummaryDTO.setTitle("Closed Auction");

        when(auctionService.getClosedAndWithoutWinnerAuctionSummary()).thenReturn(List.of(auctionSummaryDTO));

        mockMvc.perform(get("/api/auction/getClosedAuctionsByUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Closed Auction"));
    }

    @Test
    void getPendingAuctionsByUser_ReturnsPendingAuctions() throws Exception {
        AuctionSummaryDTO auctionSummaryDTO = new AuctionSummaryDTO();
        when(auctionService.getPendingAndWithoutWinnerAuctionSummary()).thenReturn(List.of(auctionSummaryDTO));

        mockMvc.perform(get("/api/auction/getPendingAuctionsByUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getOpenAuctionsByUser_ReturnsOpenAuctions() throws Exception {
        AuctionSummaryDTO auctionSummaryDTO = new AuctionSummaryDTO();
        when(auctionService.getOpenAndWithoutWinnerAuctionSummary()).thenReturn(List.of(auctionSummaryDTO));

        mockMvc.perform(get("/api/auction/getOpenAuctionsByUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getAuctionSubscriptionByAuctionId_ReturnsTrue_WhenUserIsSubscribed() throws Exception {
        when(auctionService.getAuctionSubscriptionByAuctionId(1L)).thenReturn(true);

        mockMvc.perform(get("/api/auction/getAuctionSubscriptionByAuctionId")
                        .param("auctionId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
