package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.request.category.MacroCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.MacroCategoryResponseDTO;
import org.elis.progettoing.mapper.implementation.MacroCategoryMapperImpl;
import org.elis.progettoing.models.category.MacroCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MacroCategoryMapperImplTest {
    private MacroCategoryMapperImpl macroCategoryMapper;

    @BeforeEach
    void setUp() {
        macroCategoryMapper = new MacroCategoryMapperImpl();
    }

    @Test
    void testMacroCategoryToResponseDTO() {
        MacroCategory macroCategory = new MacroCategory();
        macroCategory.setId(1L);
        macroCategory.setName("Test Category");

        MacroCategoryResponseDTO responseDTO = macroCategoryMapper.macroCategoryToResponseDTO(macroCategory);

        assertNotNull(responseDTO);
        assertEquals(macroCategory.getId(), responseDTO.getId());
        assertEquals(macroCategory.getName(), responseDTO.getName());
    }

    @Test
    void testMacroCategoryToResponseDTO_Null() {
        MacroCategoryResponseDTO responseDTO = macroCategoryMapper.macroCategoryToResponseDTO(null);
        assertNull(responseDTO);
    }

    @Test
    void testMacroCategoriesToMacroCategoryDTOs() {
        MacroCategory category1 = new MacroCategory();
        category1.setId(1L);
        category1.setName("Category 1");

        MacroCategory category2 = new MacroCategory();
        category2.setId(2L);
        category2.setName("Category 2");

        List<MacroCategory> categories = Arrays.asList(category1, category2);

        List<MacroCategoryResponseDTO> responseDTOs = macroCategoryMapper.macroCategoriesToMacroCategoryDTOs(categories);

        assertNotNull(responseDTOs);
        assertEquals(2, responseDTOs.size());
        assertEquals(category1.getId(), responseDTOs.get(0).getId());
        assertEquals(category1.getName(), responseDTOs.get(0).getName());
        assertEquals(category2.getId(), responseDTOs.get(1).getId());
        assertEquals(category2.getName(), responseDTOs.get(1).getName());
    }

    @Test
    void testMacroCategoriesToMacroCategoryDTOs_Null() {
        List<MacroCategoryResponseDTO> responseDTOs = macroCategoryMapper.macroCategoriesToMacroCategoryDTOs(null);
        assertNotNull(responseDTOs);
        assertTrue(responseDTOs.isEmpty());
    }

    @Test
    void testMacroCategoriesToMacroCategoryDTOs_EmptyList() {
        List<MacroCategoryResponseDTO> responseDTOs = macroCategoryMapper.macroCategoriesToMacroCategoryDTOs(Collections.emptyList());
        assertNotNull(responseDTOs);
        assertTrue(responseDTOs.isEmpty());
    }

    @Test
    void testRequestDTOToMacroCategory() {
        MacroCategoryRequestDTO requestDTO = new MacroCategoryRequestDTO();
        requestDTO.setId(1L);
        requestDTO.setName("Test Category");

        MacroCategory macroCategory = macroCategoryMapper.requestDTOToMacroCategory(requestDTO);

        assertNotNull(macroCategory);
        assertEquals(requestDTO.getId(), macroCategory.getId());
        assertEquals(requestDTO.getName(), macroCategory.getName());
    }

    @Test
    void testRequestDTOToMacroCategory_Null() {
        MacroCategory macroCategory = macroCategoryMapper.requestDTOToMacroCategory(null);
        assertNull(macroCategory);
    }
}
