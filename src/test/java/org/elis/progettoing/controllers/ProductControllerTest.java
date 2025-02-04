package org.elis.progettoing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elis.progettoing.dto.request.product.ProductRequestDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;
import org.elis.progettoing.dto.response.product.ProductSummaryDTO;
import org.elis.progettoing.dto.response.product.TagResponseDTO;
import org.elis.progettoing.dto.response.user.UserResponseDTO;
import org.elis.progettoing.service.definition.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductRequestDTO productRequestDTO;
    private ProductDetailsDTO productDetailsDTO;
    private ProductSummaryDTO productSummaryDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setTitle("Test Product");
        productRequestDTO.setDescription("Test Description");
        productRequestDTO.setSubCategoryId(1L);
        productRequestDTO.setPackages(Collections.emptyList());
        productRequestDTO.setTags(Collections.emptyList());

        productDetailsDTO = new ProductDetailsDTO();
        productDetailsDTO.setId(1L);
        productDetailsDTO.setTitle("Test Product");
        productDetailsDTO.setDescription("Test Description");

        productSummaryDTO = new ProductSummaryDTO();
        productSummaryDTO.setId(1L);
        productSummaryDTO.setTitle("Test Product");
        productSummaryDTO.setDescription("Test Description");
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productService.createProduct(any(ProductRequestDTO.class), anyList()))
                .thenReturn(productDetailsDTO);

        ObjectMapper objectMapper = new ObjectMapper();
        String productRequestJson = objectMapper.writeValueAsString(productRequestDTO);

        MockMultipartFile image = new MockMultipartFile("images", "image1.jpg", "image/jpeg", new byte[0]);

        MockMultipartFile productRequestDTOFile = new MockMultipartFile(
                "productRequestDTO",
                "productRequestDTO",
                "application/json",
                productRequestJson.getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/products/createProduct")
                        .file(image)
                        .file(productRequestDTOFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void testGetProductSummary() throws Exception {
        when(productService.getProductSummary())
                .thenReturn(Collections.singletonList(productSummaryDTO));

        mockMvc.perform(get("/api/products/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Product"))
                .andExpect(jsonPath("$[0].description").value("Test Description"));
    }

    @Test
    void testGetProductDetails() throws Exception {
        when(productService.findWithDetails(1L))
                .thenReturn(productDetailsDTO);

        mockMvc.perform(get("/api/products/details")
                        .param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void testRemoveProduct() throws Exception {
        when(productService.removeProduct(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/products/removeProduct")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testGetTags() throws Exception {
        when(productService.getTags(null))
                .thenReturn(Collections.singletonList(new TagResponseDTO()));

        mockMvc.perform(get("/api/products/getTags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").isNotEmpty());
    }

    @Test
    void testFindAllByUserId() throws Exception {
        // Create a UserResponseDTO for the mock user
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("John Doe");

        // Create a ProductSummaryDTO and assign the mock user
        productSummaryDTO.setId(1L);
        productSummaryDTO.setTitle("Test Product");
        productSummaryDTO.setDescription("Test Description");
        productSummaryDTO.setUser(userResponseDTO);  // Set the mock user
        productSummaryDTO.setUrlProductPhoto(Arrays.asList("url1", "url2"));
        productSummaryDTO.setStartPrice(100.0);

        List<ProductSummaryDTO> productSummaryDTOList = Collections.singletonList(productSummaryDTO);

        // Mock the service call
        when(productService.findAllSummaryByUserId(1L)).thenReturn(productSummaryDTOList);

        // Perform the GET request and check the response
        mockMvc.perform(get("/api/products/summaryByUserId")
                        .param("userId", "1"))
                .andExpect(status().isOk())  // Check for 200 OK
                .andExpect(jsonPath("$[0].id").value(1L))  // Check product ID
                .andExpect(jsonPath("$[0].title").value("Test Product"))  // Check title
                .andExpect(jsonPath("$[0].description").value("Test Description"))  // Check description
                .andExpect(jsonPath("$[0].user.id").value(1L))  // Check user ID
                .andExpect(jsonPath("$[0].user.name").value("John Doe"))  // Check user name
                .andExpect(jsonPath("$[0].urlProductPhoto[0]").value("url1"))  // Check first product photo
                .andExpect(jsonPath("$[0].urlProductPhoto[1]").value("url2"))  // Check second product photo
                .andExpect(jsonPath("$[0].startPrice").value(100.0));  // Check start price

        // Verify that the service method was called with the correct user ID
        verify(productService).findAllSummaryByUserId(1L);
    }
}

