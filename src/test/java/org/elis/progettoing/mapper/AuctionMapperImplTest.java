package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.request.auction.AuctionRequestDTO;
import org.elis.progettoing.dto.response.auction.AuctionDetailsDTO;
import org.elis.progettoing.dto.response.auction.AuctionSummaryDTO;
import org.elis.progettoing.mapper.implementation.AuctionMapperImpl;
import org.elis.progettoing.mapper.implementation.MacroCategoryMapperImpl;
import org.elis.progettoing.mapper.implementation.SubCategoryMapperImpl;
import org.elis.progettoing.mapper.implementation.UserMapperImpl;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.models.category.MacroCategory;
import org.elis.progettoing.models.category.SubCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionMapperImplTest {
    @Mock
    private MacroCategoryMapperImpl macroCategoryMapper;

    @Mock
    private UserMapperImpl userMapper;

    @Mock
    private SubCategoryMapperImpl subCategoryMapper;

    @InjectMocks
    private AuctionMapperImpl auctionMapper;

    @Test
    void testAuctionRequestDTOToAuction() {
        AuctionRequestDTO requestDTO = new AuctionRequestDTO();
        requestDTO.setId(1L);
        requestDTO.setTitle("Test Auction");
        requestDTO.setDescriptionProduct("Test Description");
        requestDTO.setDeliveryDate(13);
        requestDTO.setStartAuctionDate(LocalDateTime.parse("2025-02-19T12:00:00"));
        requestDTO.setEndAuctionDate(LocalDateTime.parse("2025-03-19T12:00:00"));
        requestDTO.setMacroCategoryId(10L);
        requestDTO.setSubCategoryId(20L);

        Auction auction = auctionMapper.auctionRequestDTOToAuction(requestDTO);

        assertNotNull(auction);
        assertEquals(requestDTO.getId(), auction.getId());
        assertEquals(requestDTO.getTitle(), auction.getTitle());
        assertEquals(requestDTO.getDescriptionProduct(), auction.getDescriptionProduct());
        assertEquals(requestDTO.getDeliveryDate(), auction.getDeliveryDate());
        assertEquals(requestDTO.getStartAuctionDate(), auction.getStartAuctionDate());
        assertEquals(requestDTO.getEndAuctionDate(), auction.getEndAuctionDate());
        assertNotNull(auction.getMacroCategory());
        assertNotNull(auction.getSubCategory());
    }

    @Test
    void testAuctionRequestDTOToAuction_Null() {
        Auction auction = auctionMapper.auctionRequestDTOToAuction(null);
        assertNull(auction);
    }

    @Test
    void testAuctionToAuctionDetailsDTO() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setTitle("Test Auction");
        auction.setDescriptionProduct("Test Description");
        auction.setDeliveryDate(13);
        auction.setStartAuctionDate(LocalDateTime.parse("2025-02-19T12:00:00"));
        auction.setEndAuctionDate(LocalDateTime.parse("2025-03-19T12:00:00"));

        User owner = new User();
        auction.setOwner(owner);

        MacroCategory macroCategory = new MacroCategory();
        auction.setMacroCategory(macroCategory);

        SubCategory subCategory = new SubCategory();
        auction.setSubCategory(subCategory);

        when(userMapper.userToUserResponseDTO(owner)).thenReturn(null);
        when(macroCategoryMapper.macroCategoryToResponseDTO(macroCategory)).thenReturn(null);
        when(subCategoryMapper.subCategoryToResponseDTO(subCategory)).thenReturn(null);

        AuctionDetailsDTO detailsDTO = auctionMapper.auctionToAuctionResponseDTO(auction);

        assertNotNull(detailsDTO);
        assertEquals(auction.getId(), detailsDTO.getId());
        assertEquals(auction.getTitle(), detailsDTO.getTitle());
        assertEquals(auction.getDescriptionProduct(), detailsDTO.getDescription());
        assertEquals(auction.getDeliveryDate(), detailsDTO.getDeliveryDate());
        assertEquals(auction.getStartAuctionDate(), detailsDTO.getStartAuctionDate());
        assertEquals(auction.getEndAuctionDate(), detailsDTO.getEndAuctionDate());
    }

    @Test
    void testAuctionToAuctionDetailsDTO_Null() {
        AuctionDetailsDTO detailsDTO = auctionMapper.auctionToAuctionResponseDTO(null);
        assertNull(detailsDTO);
    }

    @Test
    void testAuctionToAuctionSummaryDTO() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setTitle("Test Auction");
        auction.setDescriptionProduct("Test Description");

        User owner = new User();
        auction.setOwner(owner);

        when(userMapper.userToUserResponseDTO(owner)).thenReturn(null);

        AuctionSummaryDTO summaryDTO = auctionMapper.auctionToAuctionSummaryDTO(auction);

        assertNotNull(summaryDTO);
        assertEquals(auction.getId(), summaryDTO.getId());
        assertEquals(auction.getTitle(), summaryDTO.getTitle());
        assertEquals(auction.getDescriptionProduct(), summaryDTO.getDescription());
    }

    @Test
    void testAuctionToAuctionSummaryDTO_Null() {
        AuctionSummaryDTO summaryDTO = auctionMapper.auctionToAuctionSummaryDTO(null);
        assertNull(summaryDTO);
    }

    @Test
    void testAuctionRequestDTOToMacroCategory_Null() {
        MacroCategory macroCategory = auctionMapper.auctionRequestDTOToMacroCategory(null);
        assertNull(macroCategory);
    }

    @Test
    void testAuctionRequestDTOToMacroCategory() {
        AuctionRequestDTO requestDTO = new AuctionRequestDTO();
        requestDTO.setMacroCategoryId(10L);

        MacroCategory macroCategory = auctionMapper.auctionRequestDTOToMacroCategory(requestDTO);

        assertNotNull(macroCategory);
        assertEquals(requestDTO.getMacroCategoryId(), macroCategory.getId());
    }

    @Test
    void testAuctionRequestDTOToSubCategory() {
        AuctionRequestDTO requestDTO = new AuctionRequestDTO();
        requestDTO.setSubCategoryId(20L);

        SubCategory subCategory = auctionMapper.auctionRequestDTOToSubCategory(requestDTO);

        assertNotNull(subCategory);
        assertEquals(requestDTO.getSubCategoryId(), subCategory.getId());
    }

    @Test
    void testAuctionRequestDTOToSubCategory_Null() {
        SubCategory subCategory = auctionMapper.auctionRequestDTOToSubCategory(null);
        assertNull(subCategory);
    }
}

