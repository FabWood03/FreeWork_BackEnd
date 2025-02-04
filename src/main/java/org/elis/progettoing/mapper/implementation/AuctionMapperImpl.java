package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.auction.AuctionRequestDTO;
import org.elis.progettoing.dto.response.auction.AuctionDetailsDTO;
import org.elis.progettoing.dto.response.auction.AuctionSummaryDTO;
import org.elis.progettoing.mapper.definition.AuctionMapper;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.models.category.MacroCategory;
import org.elis.progettoing.models.category.SubCategory;
import org.springframework.stereotype.Component;

/**
 * This class implements the AuctionMapper interface and provides the mapping functionality
 * between DTOs and entities related to Auction, User, and Category.
 * It converts AuctionRequestDTO to Auction, Auction to AuctionDetailsDTO and AuctionSummaryDTO,
 * and also maps various other related DTOs for categories and users.
 */
@Component
public class AuctionMapperImpl implements AuctionMapper {

    private final MacroCategoryMapperImpl macroCategoryMapper;
    private final UserMapperImpl userMapperImpl;
    private final SubCategoryMapperImpl subCategoryMapperImpl;

    /**
     * Constructs a new AuctionMapperImpl with the specified MacroCategoryMapper and UserMapper.
     *
     * @param macroCategoryMapper the MacroCategoryMapper to be used for mapping
     * @param userMapperImpl the UserMapper to be used for mapping
     * @param subCategoryMapperImpl the SubCategoryMapper to be used for mapping
     */
    public AuctionMapperImpl(MacroCategoryMapperImpl macroCategoryMapper, UserMapperImpl userMapperImpl, SubCategoryMapperImpl subCategoryMapperImpl) {
        this.macroCategoryMapper = macroCategoryMapper;
        this.userMapperImpl = userMapperImpl;
        this.subCategoryMapperImpl = subCategoryMapperImpl;
    }

    /**
     * Converts an AuctionRequestDTO to an Auction entity.
     *
     * @param auctionRequestDTO the AuctionRequestDTO containing the auction data
     * @return the Auction entity populated with data from the DTO, or null if the DTO is null
     */
    @Override
    public Auction auctionRequestDTOToAuction(AuctionRequestDTO auctionRequestDTO) {
        if (auctionRequestDTO == null) {
            return null;
        }

        Auction auction = new Auction();

        auction.setMacroCategory(auctionRequestDTOToMacroCategory(auctionRequestDTO));
        auction.setSubCategory(auctionRequestDTOToSubCategory(auctionRequestDTO));
        auction.setId(auctionRequestDTO.getId());
        auction.setTitle(auctionRequestDTO.getTitle());
        auction.setDescriptionProduct(auctionRequestDTO.getDescriptionProduct());
        auction.setDeliveryDate(auctionRequestDTO.getDeliveryDate());
        auction.setStartAuctionDate(auctionRequestDTO.getStartAuctionDate());
        auction.setEndAuctionDate(auctionRequestDTO.getEndAuctionDate());

        return auction;
    }

    /**
     * Converts an Auction entity to an AuctionDetailsDTO.
     *
     * @param auction the Auction entity to be converted
     * @return the AuctionDetailsDTO populated with the Auction entity data, or null if the Auction is null
     */
    @Override
    public AuctionDetailsDTO auctionToAuctionResponseDTO(Auction auction) {
        if (auction == null) {
            return null;
        }

        AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO();

        auctionDetailsDTO.setUser(userMapperImpl.userToUserResponseDTO(auction.getOwner()));
        auctionDetailsDTO.setMacroCategory(macroCategoryMapper.macroCategoryToResponseDTO(auction.getMacroCategory()));
        auctionDetailsDTO.setSubCategory(subCategoryMapperImpl.subCategoryToResponseDTO(auction.getSubCategory()));
        auctionDetailsDTO.setId(auction.getId());
        auctionDetailsDTO.setTitle(auction.getTitle());
        auctionDetailsDTO.setDescription(auction.getDescriptionProduct());
        auctionDetailsDTO.setDeliveryDate(auction.getDeliveryDate());
        auctionDetailsDTO.setStartAuctionDate(auction.getStartAuctionDate());
        auctionDetailsDTO.setEndAuctionDate(auction.getEndAuctionDate());

        return auctionDetailsDTO;
    }

    /**
     * Converts an Auction entity to an AuctionSummaryDTO.
     *
     * @param auction the Auction entity to be converted
     * @return the AuctionSummaryDTO populated with the Auction entity data, or null if the Auction is null
     */
    @Override
    public AuctionSummaryDTO auctionToAuctionSummaryDTO(Auction auction) {
        if (auction == null) {
            return null;
        }

        AuctionSummaryDTO auctionSummaryDTO = new AuctionSummaryDTO();

        auctionSummaryDTO.setId(auction.getId());
        auctionSummaryDTO.setTitle(auction.getTitle());
        auctionSummaryDTO.setDescription(auction.getDescriptionProduct());
        auctionSummaryDTO.setState(auction.getStatus().toString());
        auctionSummaryDTO.setUser(userMapperImpl.userToUserResponseDTO(auction.getOwner()));

        return auctionSummaryDTO;
    }

    /**
     * Converts an AuctionRequestDTO to a MacroCategory entity.
     *
     * @param auctionRequestDTO the AuctionRequestDTO containing the macro category ID
     * @return the MacroCategory entity, or null if the DTO is null
     */
    public MacroCategory auctionRequestDTOToMacroCategory(AuctionRequestDTO auctionRequestDTO) {
        if (auctionRequestDTO == null) {
            return null;
        }

        MacroCategory macroCategory = new MacroCategory();

        macroCategory.setId(auctionRequestDTO.getMacroCategoryId());

        return macroCategory;
    }

    /**
     * Converts an AuctionRequestDTO to a SubCategory entity.
     *
     * @param auctionRequestDTO the AuctionRequestDTO containing the sub category ID
     * @return the SubCategory entity, or null if the DTO is null
     */
    public SubCategory auctionRequestDTOToSubCategory(AuctionRequestDTO auctionRequestDTO) {
        if (auctionRequestDTO == null) {
            return null;
        }

        SubCategory subCategory = new SubCategory();

        subCategory.setId(auctionRequestDTO.getSubCategoryId());

        return subCategory;
    }
}
