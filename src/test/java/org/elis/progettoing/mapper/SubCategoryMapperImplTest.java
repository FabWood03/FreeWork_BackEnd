package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.request.category.SubCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.MacroCategoryResponseDTO;
import org.elis.progettoing.dto.response.category.SubCategoryResponseDTO;
import org.elis.progettoing.mapper.definition.MacroCategoryMapper;
import org.elis.progettoing.mapper.implementation.SubCategoryMapperImpl;
import org.elis.progettoing.models.category.MacroCategory;
import org.elis.progettoing.models.category.SubCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubCategoryMapperImplTest {

    @InjectMocks
    private SubCategoryMapperImpl subCategoryMapperImpl;

    @Mock
    private MacroCategoryMapper macroCategoryMapper;

    @Mock
    private MacroCategory macroCategory;

    private SubCategoryRequestDTO subCategoryRequestDTO;
    private SubCategory subCategory;

    @BeforeEach
    void setUp() {
        // Create the SubCategoryRequestDTO object
        subCategoryRequestDTO = new SubCategoryRequestDTO();
        subCategoryRequestDTO.setId(1L);
        subCategoryRequestDTO.setName("SubCategory Test");
        subCategoryRequestDTO.setMacroCategoryId(2L);

        // Create the MacroCategory object
        macroCategory = new MacroCategory();
        macroCategory.setId(2L);

        // Create the SubCategory entity
        subCategory = new SubCategory();
        subCategory.setId(1L);
        subCategory.setName("SubCategory Test");
        subCategory.setMacroCategory(macroCategory);
    }

    @Test
    void testRequestDTOToSubCategory() {
        // Call the method
        SubCategory result = subCategoryMapperImpl.requestDTOToSubCategory(subCategoryRequestDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(subCategoryRequestDTO.getId(), result.getId());
        assertEquals(subCategoryRequestDTO.getName(), result.getName());
        assertNotNull(result.getMacroCategory());
        assertEquals(subCategoryRequestDTO.getMacroCategoryId(), result.getMacroCategory().getId());
    }

    @Test
    void testSubCategoryToResponseDTO() {
        // Mock di MacroCategory e il suo DTO
        MacroCategory macroCategory = mock(MacroCategory.class);
        MacroCategoryResponseDTO macroCategoryResponseDTO = mock(MacroCategoryResponseDTO.class);

        // Mock dei metodi
        when(macroCategory.getId()).thenReturn(1L);
        when(macroCategory.getName()).thenReturn("Category Name");
        when(macroCategoryMapper.macroCategoryToResponseDTO(macroCategory)).thenReturn(macroCategoryResponseDTO);
        when(macroCategoryResponseDTO.getId()).thenReturn(1L);
        when(macroCategoryResponseDTO.getName()).thenReturn("Category Name");

        // Crea un oggetto SubCategory
        SubCategory subCategory = mock(SubCategory.class);
        when(subCategory.getId()).thenReturn(1L);
        when(subCategory.getName()).thenReturn("SubCategory Name");
        when(subCategory.getMacroCategory()).thenReturn(macroCategory);

        // Call the method
        SubCategoryResponseDTO result = subCategoryMapperImpl.subCategoryToResponseDTO(subCategory);

        // Assertions
        assertNotNull(result);
        assertEquals(subCategory.getId(), result.getId());
        assertEquals(subCategory.getName(), result.getName());
        assertEquals(subCategory.getMacroCategory().getId(), result.getMacroCategory().getId());
        assertEquals(subCategory.getMacroCategory().getName(), result.getMacroCategory().getName());
    }

    @Test
    void testSubCategoriesToSubCategoryDTOs() {
        // Mock del MacroCategory e del suo DTO
        MacroCategory macroCategory = mock(MacroCategory.class);
        MacroCategoryResponseDTO macroCategoryResponseDTO = mock(MacroCategoryResponseDTO.class);

        // Mock dei metodi
        when(macroCategory.getId()).thenReturn(1L);
        when(macroCategory.getName()).thenReturn("Category Name");
        when(macroCategoryMapper.macroCategoryToResponseDTO(macroCategory)).thenReturn(macroCategoryResponseDTO);
        when(macroCategoryResponseDTO.getId()).thenReturn(1L);
        when(macroCategoryResponseDTO.getName()).thenReturn("Category Name");

        // Crea un oggetto SubCategory
        SubCategory subCategory = mock(SubCategory.class);
        when(subCategory.getId()).thenReturn(1L);
        when(subCategory.getName()).thenReturn("SubCategory Name");
        when(subCategory.getMacroCategory()).thenReturn(macroCategory);

        // Prepara la lista con una SubCategory
        List<SubCategory> subCategoryList = Collections.singletonList(subCategory);

        // Chiamata al metodo
        List<SubCategoryResponseDTO> result = subCategoryMapperImpl.subCategoriesToSubCategoryDTOs(subCategoryList);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(subCategory.getId(), result.getFirst().getId());
        assertEquals(subCategory.getName(), result.getFirst().getName());
        assertEquals(subCategory.getMacroCategory().getId(), result.getFirst().getMacroCategory().getId());
        assertEquals(subCategory.getMacroCategory().getName(), result.getFirst().getMacroCategory().getName());
    }

    @Test
    void subCategoriesToSubCategoryDTOs_ReturnsEmptyList_WhenInputListIsEmpty() {
        List<SubCategoryResponseDTO> result = subCategoryMapperImpl.subCategoriesToSubCategoryDTOs(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSubCategoryRequestDTOToMacroCategory() {
        // Call the method
        MacroCategory result = subCategoryMapperImpl.subCategoryRequestDTOToMacroCategory(subCategoryRequestDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(subCategoryRequestDTO.getMacroCategoryId(), result.getId());
    }

    @Test
    void testSubCategoryRequestDTOToMacroCategory_withNullDTO() {
        // Call the method with the null DTO
        MacroCategory result = subCategoryMapperImpl.subCategoryRequestDTOToMacroCategory(null);

        // Assertions
        assertNull(result, "The result should be null when the input DTO is null.");
    }

    @Test
    void testSubCategoryMacroCategoryId() {
        // Call the method
        long result = subCategoryMapperImpl.subCategoryMacroCategoryId(subCategory);

        // Assertions
        assertEquals(subCategory.getMacroCategory().getId(), result);
    }

    @Test
    void testSubCategoryMacroCategoryId_NullSubCategory() {
        // Call the method with null SubCategory
        long result = subCategoryMapperImpl.subCategoryMacroCategoryId(null);

        // Assertions
        assertEquals(0L, result);
    }

    @Test
    void testSubCategoryMacroCategoryId_NullMacroCategory() {
        // Set MacroCategory to null
        subCategory.setMacroCategory(null);

        // Call the method
        long result = subCategoryMapperImpl.subCategoryMacroCategoryId(subCategory);

        // Assertions
        assertEquals(0L, result);
    }

    @Test
    void testRequestDTOToSubCategory_NullDTO() {
        SubCategory result = subCategoryMapperImpl.requestDTOToSubCategory(null);

        // Assertions
        assertNull(result);
    }

    @Test
    void testSubCategoryToResponseDTO_NullEntity() {
        SubCategoryResponseDTO result = subCategoryMapperImpl.subCategoryToResponseDTO(null);

        // Assertions
        assertNull(result);
    }
}
