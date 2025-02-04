package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.request.product.PurchasedProductRequestDTO;
import org.elis.progettoing.dto.response.cart.PurchasedProductResponseDTO;
import org.elis.progettoing.enumeration.PackageType;
import org.elis.progettoing.mapper.implementation.ProductMapperImpl;
import org.elis.progettoing.mapper.implementation.ProductPackageMapperImpl;
import org.elis.progettoing.mapper.implementation.PurchasedProductMapperImpl;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.ProductPackage;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PurchasedProductMapperImplTest {

    @Mock
    private ProductMapperImpl productMapperImpl;

    @Mock
    private ProductPackageMapperImpl productPackageMapperImpl;

    @InjectMocks
    private PurchasedProductMapperImpl purchasedProductMapperImpl;

    private PurchasedProductRequestDTO purchasedProductRequestDTO;
    private PurchasedProduct purchasedProduct;

    @BeforeEach
    void setUp() {
        // Creating the Product object
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Product Title");
        product.setDescription("Product Description");

        // Creating the ProductPackage object
        ProductPackage productPackage = new ProductPackage();
        productPackage.setId(1L); // Setting a sample package ID

        // Creating the PurchasedProduct object
        purchasedProduct = new PurchasedProduct();
        purchasedProduct.setId(1L);
        purchasedProduct.setProduct(product);
        purchasedProduct.setSelectedPackage(productPackage);

        // Creating the PurchasedProductRequestDTO object
        purchasedProductRequestDTO = new PurchasedProductRequestDTO();
        purchasedProductRequestDTO.setProductId(1L);
        purchasedProductRequestDTO.setPackageId(1L);

        // Creating the PurchasedProductResponseDTO object
        PurchasedProductResponseDTO purchasedProductResponseDTO = new PurchasedProductResponseDTO();
        purchasedProductResponseDTO.setId(1L);
        purchasedProductResponseDTO.setProductTitle(product.getTitle()); // Mocked product details mapping
        purchasedProductResponseDTO.setType(String.valueOf(productPackage.getType())); // Mocked package details mapping
    }

    @Test
    void testRequestDTOToPurchasedProduct() {
        // Call the method
        PurchasedProduct result = purchasedProductMapperImpl.requestDTOToPurchasedProduct(purchasedProductRequestDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(1L, result.getProduct().getId());
        assertEquals(1L, result.getSelectedPackage().getId());
    }

    @Test
    void testPurchasedProductRequestDTOToProduct() {
        // Call the method
        Product result = purchasedProductMapperImpl.purchasedProductRequestDTOToProduct(purchasedProductRequestDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(purchasedProductRequestDTO.getProductId(), result.getId());
    }

    @Test
    void testPurchasedProductRequestDTOToProductPackage() {
        // Call the method
        ProductPackage result = purchasedProductMapperImpl.purchasedProductRequestDTOToProductPackage(purchasedProductRequestDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(purchasedProductRequestDTO.getPackageId(), result.getId());
    }

    @Test
    void testPurchasedProductToResponseDTO_Success() {
        // Creazione di un'istanza di PurchasedProduct con dati di esempio
        PurchasedProduct purchasedProduct = new PurchasedProduct();
        purchasedProduct.setId(1L);

        // Creazione del prodotto
        Product product = new Product();
        product.setTitle("Test Product");
        product.setUrlProductPhotos(List.of("image.jpg"));
        purchasedProduct.setProduct(product);

        // Creazione del pacchetto
        ProductPackage productPackage = new ProductPackage();
        productPackage.setType(PackageType.valueOf("PREMIUM"));
        productPackage.setPrice(100L);
        purchasedProduct.setSelectedPackage(productPackage);

        // Creazione dell'utente
        User user = new User();
        user.setName("John");
        user.setSurname("Doe");
        purchasedProduct.setBuyer(user);

        // Chiamata al metodo da testare
        PurchasedProductResponseDTO responseDTO = purchasedProductMapperImpl.purchasedProductToResponseDTO(purchasedProduct);

        // Verifica dei valori
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Test Product", responseDTO.getProductTitle());
        assertEquals("image.jpg", responseDTO.getProductImagePhoto());
        assertEquals("PREMIUM", responseDTO.getType());
        assertEquals(100L, responseDTO.getPrice());
        assertEquals("John", responseDTO.getUserName());
        assertEquals("Doe", responseDTO.getUserSurname());
    }

    @Test
    void testPurchasedProductsToPurchasedProductDTOs_Success() {
        // Creazione di un oggetto PurchasedProduct
        PurchasedProduct purchasedProduct = new PurchasedProduct();
        purchasedProduct.setId(1L);

        Product product = new Product();
        product.setTitle("Test Product");
        product.setUrlProductPhotos(List.of("image.jpg"));
        purchasedProduct.setProduct(product);

        ProductPackage productPackage = new ProductPackage();
        productPackage.setType(PackageType.valueOf("PREMIUM"));
        productPackage.setPrice(100L);
        purchasedProduct.setSelectedPackage(productPackage);

        User user = new User();
        user.setName("John");
        user.setSurname("Doe");
        purchasedProduct.setBuyer(user);

        // Aggiunta dell'oggetto alla lista
        List<PurchasedProduct> purchasedProducts = List.of(purchasedProduct);

        // Chiamata al metodo da testare
        List<PurchasedProductResponseDTO> responseDTOs = purchasedProductMapperImpl.purchasedProductsToPurchasedProductDTOs(purchasedProducts);

        // Verifica dei risultati
        assertNotNull(responseDTOs);
        assertEquals(1, responseDTOs.size());

        PurchasedProductResponseDTO responseDTO = responseDTOs.getFirst();
        assertEquals(1L, responseDTO.getId());
        assertEquals("Test Product", responseDTO.getProductTitle());
        assertEquals("image.jpg", responseDTO.getProductImagePhoto());
        assertEquals("PREMIUM", responseDTO.getType());
        assertEquals(100L, responseDTO.getPrice());
        assertEquals("John", responseDTO.getUserName());
        assertEquals("Doe", responseDTO.getUserSurname());
    }

    @Test
    void testPurchasedProductDTOsToPurchasedProducts_WhenListIsNull() {
        // Chiamata al metodo con lista nulla
        List<PurchasedProduct> purchasedProducts = purchasedProductMapperImpl.purchasedProductDTOsToPurchasedProducts(null);

        // Verifica che venga restituita una lista vuota
        assertNotNull(purchasedProducts);
        assertTrue(purchasedProducts.isEmpty());
    }

    @Test
    void testPurchasedProductDTOsToPurchasedProducts_Success() {
        // Creazione di un oggetto PurchasedProductRequestDTO
        PurchasedProductRequestDTO requestDTO = new PurchasedProductRequestDTO(1L, 2L);

        // Chiamata al metodo da testare
        List<PurchasedProduct> purchasedProducts = purchasedProductMapperImpl.purchasedProductDTOsToPurchasedProducts(List.of(requestDTO));

        // Verifica dei risultati
        assertNotNull(purchasedProducts);
        assertEquals(1, purchasedProducts.size());

        PurchasedProduct purchasedProduct = purchasedProducts.get(0);
        assertEquals(1L, purchasedProduct.getProduct().getId());
        assertEquals(2L, purchasedProduct.getSelectedPackage().getId());
    }

    @Test
    void testRequestDTOToPurchasedProduct_NullDTO() {
        PurchasedProduct result = purchasedProductMapperImpl.requestDTOToPurchasedProduct(null);

        // Assertions
        assertNull(result);
    }

    @Test
    void testPurchasedProductsToPurchasedProductDTOs_NullList() {
        List<PurchasedProductResponseDTO> result = purchasedProductMapperImpl.purchasedProductsToPurchasedProductDTOs(null);

        // Assertions
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testPurchasedProductToResponseDTO_NullEntity() {
        PurchasedProductResponseDTO result = purchasedProductMapperImpl.purchasedProductToResponseDTO(null);

        // Assertions
        assertNull(result);
    }

    @Test
    void testPurchasedProductRequestDTOToProduct_NullDTO() {
        Product result = purchasedProductMapperImpl.purchasedProductRequestDTOToProduct(null);

        // Assertions
        assertNull(result);
    }

    @Test
    void testPurchasedProductRequestDTOToProductPackage_NullDTO() {
        ProductPackage result = purchasedProductMapperImpl.purchasedProductRequestDTOToProductPackage(null);

        // Assertions
        assertNull(result);
    }
}
