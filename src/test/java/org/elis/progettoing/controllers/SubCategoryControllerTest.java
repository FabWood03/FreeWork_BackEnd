package org.elis.progettoing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elis.progettoing.dto.request.category.SubCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.MacroCategoryResponseDTO;
import org.elis.progettoing.dto.response.category.SubCategoryResponseDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;
import org.elis.progettoing.dto.response.product.ProductPackageResponseDTO;
import org.elis.progettoing.dto.response.product.TagResponseDTO;
import org.elis.progettoing.service.definition.SubCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SubCategoryControllerTest {

    @Mock
    private SubCategoryService subCategoryService;

    @InjectMocks
    private SubCategoryController subCategoryController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subCategoryController).build();
    }

    @Test
    void testCreateSubCategory() throws Exception {
        SubCategoryRequestDTO subCategoryRequestDTO = new SubCategoryRequestDTO();
        subCategoryRequestDTO.setName("Test SubCategory");
        subCategoryRequestDTO.setMacroCategoryId(1L);

        SubCategoryResponseDTO subCategoryResponseDTO = new SubCategoryResponseDTO();
        subCategoryResponseDTO.setId(1L);
        subCategoryResponseDTO.setName("Test SubCategory");

        when(subCategoryService.create(subCategoryRequestDTO)).thenReturn(subCategoryResponseDTO);

        ObjectMapper objectMapper = new ObjectMapper();
        String subCategoryRequestJson = objectMapper.writeValueAsString(subCategoryRequestDTO);

        mockMvc.perform(post("/api/subCategory/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subCategoryRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test SubCategory"));
    }

    @Test
    void testDeleteSubCategory() throws Exception {
        long subCategoryId = 1L;

        when(subCategoryService.delete(subCategoryId)).thenReturn(true);

        mockMvc.perform(delete("/api/subCategory/delete")
                        .param("id", String.valueOf(subCategoryId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testUpdateSubCategory() throws Exception {
        SubCategoryRequestDTO subCategoryRequestDTO = new SubCategoryRequestDTO();
        subCategoryRequestDTO.setId(1L);
        subCategoryRequestDTO.setName("Updated SubCategory");
        subCategoryRequestDTO.setMacroCategoryId(1L);

        SubCategoryResponseDTO subCategoryResponseDTO = new SubCategoryResponseDTO();
        subCategoryResponseDTO.setId(1L);
        subCategoryResponseDTO.setName("Updated SubCategory");

        when(subCategoryService.update(subCategoryRequestDTO)).thenReturn(subCategoryResponseDTO);

        ObjectMapper objectMapper = new ObjectMapper();
        String subCategoryRequestJson = objectMapper.writeValueAsString(subCategoryRequestDTO);

        mockMvc.perform(patch("/api/subCategory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subCategoryRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated SubCategory"));
    }

    @Test
    void testFindAllSubCategories() throws Exception {
        List<SubCategoryResponseDTO> subCategories = Arrays.asList(
                new SubCategoryResponseDTO(1L, "SubCategory 1", new MacroCategoryResponseDTO()),
                new SubCategoryResponseDTO(2L, "SubCategory 2", null)
        );

        when(subCategoryService.findAll()).thenReturn(subCategories);

        mockMvc.perform(get("/api/subCategory/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].name").value("SubCategory 2"));
    }

    @Test
    void testFindSubCategoryById() throws Exception {
        SubCategoryResponseDTO subCategoryResponseDTO = new SubCategoryResponseDTO();
        subCategoryResponseDTO.setId(1L);
        subCategoryResponseDTO.setName("Test SubCategory");

        when(subCategoryService.findById(1L)).thenReturn(subCategoryResponseDTO);

        mockMvc.perform(get("/api/subCategory/getById")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test SubCategory"));
    }

    @Test
    void testFindProductsBySubCategoryId() throws Exception {
        ProductPackageResponseDTO productPackageResponseDTO = new ProductPackageResponseDTO();
        TagResponseDTO tagResponseDTO = new TagResponseDTO();

        ProductDetailsDTO product1 = new ProductDetailsDTO(new SubCategoryResponseDTO(),
                Collections.singletonList(productPackageResponseDTO),
                Collections.singletonList(tagResponseDTO));
        product1.setId(1L);
        product1.setTitle("Product 1");

        ProductDetailsDTO product2 = new ProductDetailsDTO(new SubCategoryResponseDTO(),
                Collections.singletonList(productPackageResponseDTO),
                Collections.singletonList(tagResponseDTO));
        product2.setId(2L);
        product2.setTitle("Product 2");

        List<ProductDetailsDTO> products = Arrays.asList(product1, product2);

        when(subCategoryService.findProductsByCategory(1L)).thenReturn(products);

        mockMvc.perform(get("/api/subCategory/findProductsBySubCategoryId")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Product 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Product 2"));
    }
}

