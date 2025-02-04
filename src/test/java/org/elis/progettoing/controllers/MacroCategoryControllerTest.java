package org.elis.progettoing.controllers;

import org.elis.progettoing.dto.request.category.MacroCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.MacroCategoryResponseDTO;
import org.elis.progettoing.service.definition.MacroCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MacroCategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MacroCategoryService macroCategoryService;

    @InjectMocks
    private MacroCategoryController macroCategoryController;

    private MacroCategoryResponseDTO macroCategoryResponseDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(macroCategoryController).build();

        MacroCategoryRequestDTO macroCategoryRequestDTO = new MacroCategoryRequestDTO();
        macroCategoryRequestDTO.setName("Test Category");

        macroCategoryResponseDTO = new MacroCategoryResponseDTO(1L, "Test Category");
    }

    @Test
    void testCreate() throws Exception {
        when(macroCategoryService.create(any(MacroCategoryRequestDTO.class)))
                .thenReturn(macroCategoryResponseDTO);

        mockMvc.perform(post("/api/macroCategory/create")
                        .contentType("application/json")
                        .content("{\"name\":\"Test Category\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Category"));
    }

    @Test
    void testDelete() throws Exception {
        when(macroCategoryService.delete(any(MacroCategoryRequestDTO.class)))
                .thenReturn(true);

        mockMvc.perform(delete("/api/macroCategory/delete")
                        .contentType("application/json")
                        .content("{\"name\":\"Test Category\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testUpdate() throws Exception {
        when(macroCategoryService.update(any(MacroCategoryRequestDTO.class)))
                .thenReturn(macroCategoryResponseDTO);

        mockMvc.perform(patch("/api/macroCategory/update")
                        .contentType("application/json")
                        .content("{\"name\":\"Test Category\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Category"));
    }

    @Test
    void testFindFiltered() throws Exception {
        when(macroCategoryService.findFilteredMacroCategory(anyString()))
                .thenReturn(Collections.singletonList(macroCategoryResponseDTO));

        mockMvc.perform(get("/api/macroCategory/findFiltered")
                        .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Category"));
    }

    @Test
    void testFindAll() throws Exception {
        when(macroCategoryService.findAll())
                .thenReturn(Collections.singletonList(macroCategoryResponseDTO));

        mockMvc.perform(get("/api/macroCategory/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Category"));
    }

    @Test
    void testFindById() throws Exception {
        when(macroCategoryService.findById(1L))
                .thenReturn(macroCategoryResponseDTO);

        mockMvc.perform(get("/api/macroCategory/findById")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Category"));
    }
}

