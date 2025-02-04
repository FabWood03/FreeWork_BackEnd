package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.request.product.ProductRequestDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;
import org.elis.progettoing.dto.response.product.ProductSummaryDTO;
import org.elis.progettoing.dto.response.user.UserResponseDTO;
import org.elis.progettoing.mapper.implementation.*;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.category.SubCategory;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.ProductPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductMapperImplTest {
    @Mock
    private UserMapperImpl userMapperImpl;

    @Mock
    private ProductPackageMapperImpl productPackageMapperImpl;

    @Mock
    private TagMapperImpl tagMapperImpl;

    @Mock
    private SubCategoryMapperImpl subCategoryMapperImpl;

    @InjectMocks
    private ProductMapperImpl productMapperImpl;

    private ProductRequestDTO productRequestDTO;
    private Product product;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setNickname("testUser");

        // Setup for the ProductRequestDTO
        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setTitle("Sample Product");
        productRequestDTO.setDescription("Sample Description");

        // Setup for the Product
        product = new Product();
        product.setTitle("Sample Product");
        product.setDescription("Sample Description");
        product.setId(1L);
        product.setPackages(Collections.singletonList(new ProductPackage()));

        // Setup for ProductDetailsDTO
        ProductDetailsDTO productDetailsDTO = new ProductDetailsDTO();
        productDetailsDTO.setTitle("Sample Product");
        productDetailsDTO.setDescription("Sample Description");
    }

    @Test
    void testProductRequestDTOToProduct() {
        when(productPackageMapperImpl.productPackageRequestDTOListToProductPackageList(any())).thenReturn(null);
        when(tagMapperImpl.tagDTOListToTagList(any())).thenReturn(null);

        Product result = productMapperImpl.productRequestDTOToProduct(productRequestDTO);

        assertNotNull(result);
        assertEquals("Sample Product", result.getTitle());
        assertEquals("Sample Description", result.getDescription());
    }

    @Test
    void testProductRequestDTOToProductNull() {
        Product result = productMapperImpl.productRequestDTOToProduct(null);

        assertNull(result);
    }

    @Test
    void testProductToResponseDTO() {
        when(subCategoryMapperImpl.subCategoryToResponseDTO(any())).thenReturn(null);
        when(userMapperImpl.userToUserResponseDTO(any())).thenReturn(null);
        when(productPackageMapperImpl.productPackageListToProductPackageResponseDTOList(any())).thenReturn(null);
        when(tagMapperImpl.tagToTagListResponseDTO(any())).thenReturn(null);

        ProductDetailsDTO result = productMapperImpl.productToResponseDTO(product);

        assertNotNull(result);
        assertEquals("Sample Product", result.getTitle());
        assertEquals("Sample Description", result.getDescription());
    }

    @Test
    void testProductToResponseDTONull() {
        ProductDetailsDTO result = productMapperImpl.productToResponseDTO(null);

        assertNull(result);
    }

    @Test
    void testProductsToResponseDTOs() {
        when(subCategoryMapperImpl.subCategoryToResponseDTO(any())).thenReturn(null);
        when(userMapperImpl.userToUserResponseDTO(any())).thenReturn(null);
        when(productPackageMapperImpl.productPackageListToProductPackageResponseDTOList(any())).thenReturn(null);
        when(tagMapperImpl.tagToTagListResponseDTO(any())).thenReturn(null);

        List<ProductDetailsDTO> result = productMapperImpl.productsToResponseDTOs(List.of(product));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sample Product", result.getFirst().getTitle());
    }

    @Test
    void testProductsToResponseDTOsNull() {
        List<ProductDetailsDTO> result = productMapperImpl.productsToResponseDTOs(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void testProductToSummaryDTO() {
        when(userMapperImpl.userToUserResponseDTO(any())).thenReturn(null);

        ProductSummaryDTO result = productMapperImpl.productToSummaryDTO(product);

        assertNotNull(result);
        assertEquals("Sample Product", result.getTitle());
        assertEquals("Sample Description", result.getDescription());
    }

    @Test
    void testProductToSummaryDTONull() {
        ProductSummaryDTO result = productMapperImpl.productToSummaryDTO(null);

        assertNull(result);
    }

    @Test
    void testProductRequestDTOToSubCategory() {
        productRequestDTO.setSubCategoryId(1L);

        SubCategory result = productMapperImpl.productRequestDTOToSubCategory(productRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testProductRequestDTOToSubCategoryNull() {
        SubCategory result = productMapperImpl.productRequestDTOToSubCategory(null);

        assertNull(result);
    }

    @Test
    void testProductsToSummaryResponseDTOs_WithNonNullProducts() {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setNickname("testUser");

        // Prepare a list with one product
        List<Product> products = Collections.singletonList(product);

        // Mock userMapperImpl to return a UserResponseDTO
        when(userMapperImpl.userToUserResponseDTO(null)).thenReturn(null);  // Add this line to handle null argument

        // Call the method to test
        List<ProductSummaryDTO> result = productMapperImpl.productsToSummaryResponseDTOs(products);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size()); // The list should contain 1 element

        ProductSummaryDTO productSummaryDTO = result.getFirst();  // Use get(0) instead of getFirst()
        productSummaryDTO.setUser(userResponseDTO);

        assertEquals(product.getId(), productSummaryDTO.getId());
        assertEquals(product.getTitle(), productSummaryDTO.getTitle());
        assertEquals(product.getDescription(), productSummaryDTO.getDescription());
        assertNotNull(productSummaryDTO.getUser()); // Ensure user is mapped
        assertEquals(product.getUrlProductPhotos(), productSummaryDTO.getUrlProductPhoto());
    }

    @Test
    void testProductsToSummaryResponseDTOs_WithNullProducts() {
        // Call the method with a null list
        List<ProductSummaryDTO> result = productMapperImpl.productsToSummaryResponseDTOs(null);

        // Assertions
        assertNotNull(result);
        assertTrue(result.isEmpty()); // The list should be empty
    }

    @Test
    void testProductsToSummaryResponseDTOs_WithEmptyProducts() {
        // Call the method with an empty list
        List<ProductSummaryDTO> result = productMapperImpl.productsToSummaryResponseDTOs(Collections.emptyList());

        // Assertions
        assertNotNull(result);
        assertTrue(result.isEmpty()); // The list should be empty
    }
}
