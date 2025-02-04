package org.elis.progettoing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elis.progettoing.dto.request.FilterRequest;
import org.elis.progettoing.dto.response.FilteredEntitiesResponse;
import org.elis.progettoing.service.definition.FilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FilterControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private FilterController filterController;

    @Mock
    private FilterService filterService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(filterController).build();
    }

    @Test
    void filterHome_ReturnsFilteredEntities() throws Exception {
        FilterRequest filterRequest = new FilterRequest();
        FilteredEntitiesResponse response = new FilteredEntitiesResponse();
        when(filterService.getFilteredEntities(filterRequest)).thenReturn(response);

        mockMvc.perform(post("/api/filter/filterHome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
