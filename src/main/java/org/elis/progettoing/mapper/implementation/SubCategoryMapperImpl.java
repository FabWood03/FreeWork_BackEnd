package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.category.SubCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.SubCategoryResponseDTO;
import org.elis.progettoing.mapper.definition.MacroCategoryMapper;
import org.elis.progettoing.mapper.definition.SubCategoryMapper;
import org.elis.progettoing.models.category.MacroCategory;
import org.elis.progettoing.models.category.SubCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the SubCategoryMapper interface. Provides methods for mapping between
 * SubCategory-related request and response DTOs and entity models.
 */
@Component
public class SubCategoryMapperImpl implements SubCategoryMapper {

    private final MacroCategoryMapper macroCategoryMapper;

    /**
     * Constructs a new SubCategoryMapperImpl with the specified MacroCategoryMapper.
     *
     * @param macroCategoryMapper the MacroCategoryMapper to be used for mapping
     */
    public SubCategoryMapperImpl(MacroCategoryMapper macroCategoryMapper) {
        this.macroCategoryMapper = macroCategoryMapper;
    }

    /**
     * Converts a SubCategoryRequestDTO to a SubCategory entity.
     *
     * @param requestDTO the SubCategoryRequestDTO to be converted
     * @return a SubCategory entity populated with data from the SubCategoryRequestDTO, or null if the requestDTO is null
     */
    @Override
    public SubCategory requestDTOToSubCategory(SubCategoryRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        SubCategory subCategory = new SubCategory();

        subCategory.setMacroCategory(subCategoryRequestDTOToMacroCategory(requestDTO));
        subCategory.setId(requestDTO.getId());
        subCategory.setName(requestDTO.getName());

        return subCategory;
    }

    /**
     * Converts a list of SubCategory entities to a list of SubCategoryResponseDTOs.
     *
     * @param categories the list of SubCategory entities to be converted
     * @return a list of SubCategoryResponseDTOs, or an empty list if the input list is null
     */
    @Override
    public List<SubCategoryResponseDTO> subCategoriesToSubCategoryDTOs(List<SubCategory> categories) {
        if (categories == null) {
            return Collections.emptyList();
        }

        List<SubCategoryResponseDTO> list = new ArrayList<>(categories.size());
        for (SubCategory subCategory : categories) {
            list.add(subCategoryToResponseDTO(subCategory));
        }

        return list;
    }

    /**
     * Converts a SubCategory entity to a SubCategoryResponseDTO.
     *
     * @param subCategory the SubCategory entity to be converted
     * @return a SubCategoryResponseDTO populated with data from the SubCategory entity, or null if the subCategory is null
     */
    @Override
    public SubCategoryResponseDTO subCategoryToResponseDTO(SubCategory subCategory) {
        if (subCategory == null) {
            return null;
        }

        SubCategoryResponseDTO subCategoryResponseDTO = new SubCategoryResponseDTO();

        subCategoryResponseDTO.setMacroCategory(macroCategoryMapper.macroCategoryToResponseDTO(subCategory.getMacroCategory()));
        subCategoryResponseDTO.setId(subCategory.getId());
        subCategoryResponseDTO.setName(subCategory.getName());

        return subCategoryResponseDTO;
    }

    /**
     * Converts a SubCategoryRequestDTO to a MacroCategory entity.
     *
     * @param subCategoryRequestDTO the SubCategoryRequestDTO to be converted
     * @return a MacroCategory entity populated with data from the SubCategoryRequestDTO, or null if the subCategoryRequestDTO is null
     */
    public MacroCategory subCategoryRequestDTOToMacroCategory(SubCategoryRequestDTO subCategoryRequestDTO) {
        if (subCategoryRequestDTO == null) {
            return null;
        }

        MacroCategory macroCategory = new MacroCategory();

        macroCategory.setId(subCategoryRequestDTO.getMacroCategoryId());

        return macroCategory;
    }

    /**
     * Extracts the ID of the MacroCategory associated with a SubCategory entity.
     *
     * @param subCategory the SubCategory entity from which the MacroCategory ID is extracted
     * @return the ID of the MacroCategory, or 0 if the SubCategory or its MacroCategory is null
     */
    public long subCategoryMacroCategoryId(SubCategory subCategory) {
        if (subCategory == null) {
            return 0L;
        }
        MacroCategory macroCategory = subCategory.getMacroCategory();
        if (macroCategory == null) {
            return 0L;
        }
        return macroCategory.getId();
    }
}
