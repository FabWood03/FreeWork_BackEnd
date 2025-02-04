package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.request.category.MacroCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.MacroCategoryResponseDTO;

import java.util.List;

/**
 * Interface for the MacroCategoryService class. Provides methods for creating, deleting, and updating macro categories,
 * as well as retrieving macro categories by ID, name, and all macro categories.
 */
public interface MacroCategoryService {

    MacroCategoryResponseDTO create(MacroCategoryRequestDTO macroCategoryRequestDTO);

    boolean delete(MacroCategoryRequestDTO macroCategoryRequestDTO);

    MacroCategoryResponseDTO update(MacroCategoryRequestDTO macroCategoryRequestDTO);

    List<MacroCategoryResponseDTO> findFilteredMacroCategory(String nameFilter);

    MacroCategoryResponseDTO findById(long id);

    List<MacroCategoryResponseDTO> findAll();

}
