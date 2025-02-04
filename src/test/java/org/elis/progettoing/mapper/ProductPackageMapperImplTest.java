package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.request.product.DynamicAttributeDTO;
import org.elis.progettoing.dto.request.product.ProductPackageRequestDTO;
import org.elis.progettoing.dto.response.product.PackageAttributeResponseDTO;
import org.elis.progettoing.dto.response.product.ProductPackageResponseDTO;
import org.elis.progettoing.enumeration.PackageType;
import org.elis.progettoing.mapper.implementation.ProductPackageMapperImpl;
import org.elis.progettoing.models.product.PackageAttribute;
import org.elis.progettoing.models.product.ProductPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductPackageMapperImplTest {

    @InjectMocks
    private ProductPackageMapperImpl productPackageMapperImpl;

    private ProductPackageRequestDTO productPackageRequestDTO;
    private ProductPackage productPackage;
    private DynamicAttributeDTO dynamicAttributeDTO;
    private PackageAttribute packageAttribute;

    @BeforeEach
    void setUp() {
        // Setup for ProductPackageRequestDTO
        productPackageRequestDTO = new ProductPackageRequestDTO();
        productPackageRequestDTO.setType("BASIC");
        productPackageRequestDTO.setPrice(100.0);
        productPackageRequestDTO.setDescription("Basic package");
        productPackageRequestDTO.setDeliveryTime(5);
        productPackageRequestDTO.setRevisions(1);
        productPackageRequestDTO.setEmailSupport(true);
        productPackageRequestDTO.setChatSupport(false);

        dynamicAttributeDTO = new DynamicAttributeDTO();
        dynamicAttributeDTO.setKey("color");
        dynamicAttributeDTO.setValue("red");
        productPackageRequestDTO.setAttributes(List.of(dynamicAttributeDTO));

        // Setup for ProductPackage
        productPackage = new ProductPackage();
        productPackage.setType(PackageType.BASIC);
        productPackage.setPrice(100.0);
        productPackage.setDescription("Basic package");
        productPackage.setDeliveryTime(5);
        productPackage.setRevisions(1);
        productPackage.setEmailSupport(true);
        productPackage.setChatSupport(false);
        packageAttribute = new PackageAttribute();
        packageAttribute.setKey("color");
        packageAttribute.setValue("red");
        productPackage.setAttributes(List.of(packageAttribute));

        // Setup for ProductPackageResponseDTO
        ProductPackageResponseDTO productPackageResponseDTO = new ProductPackageResponseDTO();
        productPackageResponseDTO.setType(PackageType.BASIC);
        productPackageResponseDTO.setPrice(100.0);
        productPackageResponseDTO.setDescription("Basic package");
        productPackageResponseDTO.setDeliveryTime(5);
        productPackageResponseDTO.setRevisions(1);
        productPackageResponseDTO.setEmailSupport(true);
        productPackageResponseDTO.setChatSupport(false);
    }

    @Test
    void testPackageRequestDTOToProductPackage() {
        ProductPackage result = productPackageMapperImpl.packageRequestDTOToProductPackage(productPackageRequestDTO);

        assertNotNull(result);
        assertEquals(PackageType.BASIC, result.getType());
        assertEquals(100.0, result.getPrice());
        assertEquals("Basic package", result.getDescription());
        assertEquals(5, result.getDeliveryTime());
        assertEquals(1, result.getRevisions());
        assertTrue(result.isEmailSupport());
        assertFalse(result.isChatSupport());
        assertNotNull(result.getAttributes());
        assertEquals(1, result.getAttributes().size());
        assertEquals("color", result.getAttributes().getFirst().getKey());
    }

    @Test
    void testPackageRequestDTOToProductPackageNull() {
        ProductPackage result = productPackageMapperImpl.packageRequestDTOToProductPackage(null);

        assertNull(result);
    }

    @Test
    void testDynamicAttributeDTOToPackageAttribute() {
        PackageAttribute result = productPackageMapperImpl.dynamicAttributeDTOToPackageAttribute(dynamicAttributeDTO);

        assertNotNull(result);
        assertEquals("color", result.getKey());
        assertEquals("red", result.getValue());
    }

    @Test
    void testDynamicAttributeDTOToPackageAttributeNull() {
        PackageAttribute result = productPackageMapperImpl.dynamicAttributeDTOToPackageAttribute(null);

        assertNull(result);
    }

    @Test
    void testDynamicAttributeDTOListToPackageAttributeList() {
        List<PackageAttribute> result = productPackageMapperImpl.dynamicAttributeDTOListToPackageAttributeList(List.of(dynamicAttributeDTO));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("color", result.getFirst().getKey());
        assertEquals("red", result.getFirst().getValue());
    }

    @Test
    void testDynamicAttributeDTOListToPackageAttributeListNull() {
        List<PackageAttribute> result = productPackageMapperImpl.dynamicAttributeDTOListToPackageAttributeList(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProductPackageToProductPackageResponseDTO() {
        ProductPackageResponseDTO result = productPackageMapperImpl.productPackageToProductPackageResponseDTO(productPackage);

        assertNotNull(result);
        assertEquals(PackageType.BASIC, result.getType());
        assertEquals(100.0, result.getPrice());
        assertEquals("Basic package", result.getDescription());
        assertEquals(5, result.getDeliveryTime());
        assertEquals(1, result.getRevisions());
        assertTrue(result.isEmailSupport());
        assertFalse(result.isChatSupport());
        assertNotNull(result.getAttributes());
        assertEquals(1, result.getAttributes().size());
        assertEquals("color", result.getAttributes().getFirst().getKey());
    }

    @Test
    void testProductPackageToProductPackageResponseDTONull() {
        ProductPackageResponseDTO result = productPackageMapperImpl.productPackageToProductPackageResponseDTO(null);

        assertNull(result);
    }

    @Test
    void testPackageAttributeListToPackageAttributeResponseDTOList() {
        List<PackageAttributeResponseDTO> result = productPackageMapperImpl.packageAttributeListToPackageAttributeResponseDTOList(List.of(packageAttribute));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("color", result.getFirst().getKey());
    }

    @Test
    void testPackageAttributeListToPackageAttributeResponseDTOListNull() {
        List<PackageAttributeResponseDTO> result = productPackageMapperImpl.packageAttributeListToPackageAttributeResponseDTOList(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProductPackageRequestDTOListToProductPackageList() {
        List<ProductPackage> result = productPackageMapperImpl.productPackageRequestDTOListToProductPackageList(List.of(productPackageRequestDTO));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PackageType.BASIC, result.getFirst().getType());
    }

    @Test
    void testProductPackageRequestDTOListToProductPackageListNull() {
        List<ProductPackage> result = productPackageMapperImpl.productPackageRequestDTOListToProductPackageList(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProductPackageRequestDTOToProductPackage_allFieldsNotNull() {
        // Creiamo un DTO con tutti i campi valorizzati
        ProductPackageRequestDTO dto = new ProductPackageRequestDTO();
        dto.setType("BASIC");
        dto.setPrice(100.0);
        dto.setDescription("Test description");
        dto.setDeliveryTime(3);
        dto.setRevisions(2);
        dto.setEmailSupport(true);
        dto.setChatSupport(false);

        // Convertiamo il DTO in ProductPackage
        ProductPackage productPackageNew = productPackageMapperImpl.productPackageRequestDTOToProductPackage(dto);

        // Verifica che tutti i campi siano stati impostati correttamente
        assertNotNull(productPackageNew);
        assertEquals(PackageType.BASIC, productPackageNew.getType());
        assertEquals(100.0, productPackageNew.getPrice());
        assertEquals("Test description", productPackageNew.getDescription());
        assertEquals(3, productPackageNew.getDeliveryTime());
        assertEquals(2, productPackageNew.getRevisions());
        assertTrue(productPackageNew.isEmailSupport());
        assertFalse(productPackageNew.isChatSupport());
    }

    @Test
    void testProductPackageRequestDTOToProductPackage_typeNull() {
        // DTO con tipo null
        ProductPackageRequestDTO dto = new ProductPackageRequestDTO();
        dto.setType(null);
        dto.setPrice(100.0);
        dto.setDescription("Test description");

        // Convertiamo il DTO in ProductPackage
        ProductPackage productPackageNew = productPackageMapperImpl.productPackageRequestDTOToProductPackage(dto);

        // Verifica che il campo tipo sia null
        assertNotNull(productPackageNew);
        assertNull(productPackageNew.getType());  // Poiché il tipo è null, dovrebbe restare null
    }

    @Test
    void testProductPackageRequestDTOToProductPackage_priceNull() {
        // DTO con price null
        ProductPackageRequestDTO dto = new ProductPackageRequestDTO();
        dto.setType("BASIC");
        dto.setPrice(null);  // price is null in the DTO, but it will become 0.0 in ProductPackage

        // Convertiamo il DTO in ProductPackage
        ProductPackage productPackageNew = productPackageMapperImpl.productPackageRequestDTOToProductPackage(dto);

        // Verifica che il campo price sia 0.0 (default value for primitive double)
        assertNotNull(productPackageNew);
        assertEquals(0.0, productPackageNew.getPrice(), 0.0);  // price should be 0.0, as it's a primitive double
    }


    @Test
    void testProductPackageRequestDTOToProductPackage_descriptionNull() {
        // DTO con description null
        ProductPackageRequestDTO dto = new ProductPackageRequestDTO();
        dto.setType("BASIC");
        dto.setPrice(100.0);
        dto.setDescription(null);

        // Convertiamo il DTO in ProductPackage
        ProductPackage productPackageNew = productPackageMapperImpl.productPackageRequestDTOToProductPackage(dto);

        // Verifica che il campo description sia impostato correttamente
        assertNotNull(productPackageNew);
        assertNull(productPackageNew.getDescription());  // La description è null, quindi non dovrebbe essere impostata
    }

    @Test
    void testProductPackageRequestDTOToProductPackage_deliveryTimeNull() {
        // DTO con deliveryTime null
        ProductPackageRequestDTO dto = new ProductPackageRequestDTO();
        dto.setType("BASIC");
        dto.setPrice(100.0);
        dto.setDescription("Test description");
        dto.setDeliveryTime(null);  // Setting deliveryTime to null (it will become 0 in the ProductPackage)

        // Convertiamo il DTO in ProductPackage
        ProductPackage productPackageNew = productPackageMapperImpl.productPackageRequestDTOToProductPackage(dto);

        // Verifica che il campo deliveryTime sia 0 (default value for primitive int)
        assertNotNull(productPackageNew);
        assertEquals(0, productPackageNew.getDeliveryTime());  // deliveryTime should be 0, as it's a primitive int
    }


    @Test
    void testProductPackageRequestDTOToProductPackage_revisionsNull() {
        // DTO con revisions null
        ProductPackageRequestDTO dto = new ProductPackageRequestDTO();
        dto.setType("BASIC");
        dto.setPrice(100.0);
        dto.setDescription("Test description");
        dto.setRevisions(null);

        // Convertiamo il DTO in ProductPackage
        ProductPackage productPackageNew = productPackageMapperImpl.productPackageRequestDTOToProductPackage(dto);

        // Verifica che il campo revisions sia null
        assertNotNull(productPackageNew);
        assertEquals(0, productPackageNew.getRevisions());  // La revisions è null, quindi non dovrebbe essere impostata
    }

    @Test
    void testProductPackageRequestDTOToProductPackage_emailSupportNull() {
        // DTO con emailSupport null
        ProductPackageRequestDTO dto = new ProductPackageRequestDTO();
        dto.setType("BASIC");
        dto.setPrice(100.0);
        dto.setDescription("Test description");
        dto.setEmailSupport(null);

        // Convertiamo il DTO in ProductPackage
        ProductPackage productPackageNew = productPackageMapperImpl.productPackageRequestDTOToProductPackage(dto);

        // Verifica che il campo emailSupport sia false
        assertNotNull(productPackageNew);
        assertFalse(productPackageNew.isEmailSupport());  // Se è null, il valore predefinito dovrebbe essere false
    }

    @Test
    void testProductPackageRequestDTOToProductPackage_chatSupportNull() {
        // DTO con chatSupport null
        ProductPackageRequestDTO dto = new ProductPackageRequestDTO();
        dto.setType("BASIC");
        dto.setPrice(100.0);
        dto.setDescription("Test description");
        dto.setChatSupport(null);

        // Convertiamo il DTO in ProductPackage
        ProductPackage productPackageNew = productPackageMapperImpl.productPackageRequestDTOToProductPackage(dto);

        // Verifica che il campo chatSupport sia false
        assertNotNull(productPackageNew);
        assertFalse(productPackageNew.isChatSupport());  // Se è null, il valore predefinito dovrebbe essere false
    }

    @Test
    void testProductPackageRequestDTOToProductPackage_dtoNull() {
        // Caso in cui il DTO è null
        ProductPackage productPackageNew = productPackageMapperImpl.productPackageRequestDTOToProductPackage(null);

        // Verifica che venga restituito null
        assertNull(productPackageNew);  // Se il DTO è null, la conversione dovrebbe restituire null
    }

    @Test
    void testProductPackageListToProductPackageResponseDTOList() {
        // Given: Create a list of ProductPackage entities
        ProductPackage productPackage1 = new ProductPackage();
        productPackage1.setId(1L);
        productPackage1.setDescription("Package 1");
        productPackage1.setPrice(100.0);
        productPackage1.setType(PackageType.BASIC);

        ProductPackage productPackage2 = new ProductPackage();
        productPackage2.setId(2L);
        productPackage2.setDescription("Package 2");
        productPackage2.setPrice(200.0);
        productPackage2.setType(PackageType.PREMIUM);

        List<ProductPackage> productPackageList = new ArrayList<>();
        productPackageList.add(productPackage1);
        productPackageList.add(productPackage2);

        // When: Converting the list of ProductPackage entities to ProductPackageResponseDTOs
        List<ProductPackageResponseDTO> responseDTOList = productPackageMapperImpl.productPackageListToProductPackageResponseDTOList(productPackageList);

        // Then: Verify that the size of the list is the same
        assertNotNull(responseDTOList);
        assertEquals(2, responseDTOList.size());

        // Verify the content of the first response DTO
        ProductPackageResponseDTO dto1 = responseDTOList.getFirst();
        assertEquals(1L, dto1.getId());
        assertEquals("Package 1", dto1.getDescription());
        assertEquals(100.0, dto1.getPrice());
        assertEquals(PackageType.BASIC, dto1.getType());

        // Verify the content of the second response DTO
        ProductPackageResponseDTO dto2 = responseDTOList.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Package 2", dto2.getDescription());
        assertEquals(200.0, dto2.getPrice());
        assertEquals(PackageType.PREMIUM, dto2.getType());
    }

    @Test
    void testProductPackageListToProductPackageResponseDTOList_emptyList() {
        // Given: An empty list
        List<ProductPackage> emptyList = new ArrayList<>();

        // When: Converting the empty list
        List<ProductPackageResponseDTO> responseDTOList = productPackageMapperImpl.productPackageListToProductPackageResponseDTOList(emptyList);

        // Then: Verify that the result is an empty list
        assertNotNull(responseDTOList);
        assertTrue(responseDTOList.isEmpty());
    }

    @Test
    void testProductPackageListToProductPackageResponseDTOList_nullList() {
        List<ProductPackageResponseDTO> responseDTOList = productPackageMapperImpl.productPackageListToProductPackageResponseDTOList(null);

        assertNotNull(responseDTOList);
        assertTrue(responseDTOList.isEmpty());
    }

    @Test
    void testPackageAttributeToPackageAttributeResponseDTO_nullInput() {
        PackageAttributeResponseDTO responseDTO = productPackageMapperImpl.packageAttributeToPackageAttributeResponseDTO(null);

        assertNull(responseDTO, "Expected null responseDTO when input is null");
    }
}

