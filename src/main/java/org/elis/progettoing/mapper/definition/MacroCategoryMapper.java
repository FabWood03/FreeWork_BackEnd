package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.request.category.MacroCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.MacroCategoryResponseDTO;
import org.elis.progettoing.models.category.MacroCategory;

import java.util.List;

/**
 * Interface for mapping between MacroCategory entities and their respective DTOs.
 * This interface defines methods for converting a MacroCategory entity to a MacroCategoryResponseDTO,
 * converting a list of MacroCategory entities to a list of MacroCategoryResponseDTOs,
 * and converting a MacroCategoryRequestDTO to a MacroCategory entity.
 */
public interface MacroCategoryMapper {

    /**
     * Converts a MacroCategory entity to a MacroCategoryResponseDTO.
     *
     * @param macroCategory the MacroCategory entity to be converted
     * @return the MacroCategoryResponseDTO populated with data from the MacroCategory entity
     */
    MacroCategoryResponseDTO macroCategoryToResponseDTO(MacroCategory macroCategory);

    /**
     * Converts a list of MacroCategory entities to a list of MacroCategoryResponseDTOs.
     *
     * @param categories the list of MacroCategory entities to be converted
     * @return the list of MacroCategoryResponseDTOs populated with data from the list of MacroCategory entities
     */
    List<MacroCategoryResponseDTO> macroCategoriesToMacroCategoryDTOs(List<MacroCategory> categories);

    /**
     * Converts a MacroCategoryRequestDTO to a MacroCategory entity.
     *
     * @param requestDTO the MacroCategoryRequestDTO to be converted
     * @return the MacroCategory entity populated with data from the MacroCategoryRequestDTO
     */
    MacroCategory requestDTOToMacroCategory(MacroCategoryRequestDTO requestDTO);
}
