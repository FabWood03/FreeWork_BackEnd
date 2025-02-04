package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.category.MacroCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.MacroCategoryResponseDTO;
import org.elis.progettoing.mapper.definition.MacroCategoryMapper;
import org.elis.progettoing.models.category.MacroCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the MacroCategoryMapper interface. Provides methods to map between
 * MacroCategory entities, their corresponding DTOs (Data Transfer Objects), and request DTOs.
 */
@Component
public class MacroCategoryMapperImpl implements MacroCategoryMapper {

    /**
     * Converts a MacroCategory entity to a MacroCategoryResponseDTO.
     *
     * @param macroCategory the MacroCategory entity to be converted
     * @return a MacroCategoryResponseDTO populated with the MacroCategory entity data, or null if the MacroCategory is null
     */
    @Override
    public MacroCategoryResponseDTO macroCategoryToResponseDTO(MacroCategory macroCategory) {
        if (macroCategory == null) {
            return null;
        }

        MacroCategoryResponseDTO macroCategoryResponseDTO = new MacroCategoryResponseDTO();
        macroCategoryResponseDTO.setId(macroCategory.getId());
        macroCategoryResponseDTO.setName(macroCategory.getName());

        return macroCategoryResponseDTO;
    }

    /**
     * Converts a list of MacroCategory entities to a list of MacroCategoryResponseDTOs.
     *
     * @param categories the list of MacroCategory entities to be converted
     * @return a list of MacroCategoryResponseDTOs populated with the MacroCategory entity data, or null if the input list is null
     */
    @Override
    public List<MacroCategoryResponseDTO> macroCategoriesToMacroCategoryDTOs(List<MacroCategory> categories) {
        if (categories == null) {
            return Collections.emptyList();
        }

        List<MacroCategoryResponseDTO> list = new ArrayList<>(categories.size());
        for (MacroCategory macroCategory : categories) {
            list.add(macroCategoryToResponseDTO(macroCategory));
        }

        return list;
    }

    /**
     * Converts a MacroCategoryRequestDTO to a MacroCategory entity.
     *
     * @param requestDTO the MacroCategoryRequestDTO to be converted
     * @return a MacroCategory entity populated with the data from the requestDTO, or null if the requestDTO is null
     */
    @Override
    public MacroCategory requestDTOToMacroCategory(MacroCategoryRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        MacroCategory macroCategory = new MacroCategory();
        macroCategory.setId(requestDTO.getId());
        macroCategory.setName(requestDTO.getName());

        return macroCategory;
    }
}
