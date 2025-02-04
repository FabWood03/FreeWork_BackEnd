package org.elis.progettoing.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.elis.progettoing.dto.request.product.DynamicAttributeDTO;
import org.elis.progettoing.dto.request.product.ProductPackageRequestDTO;
import org.elis.progettoing.dto.request.product.ProductRequestDTO;
import org.elis.progettoing.dto.request.product.TagDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;
import org.elis.progettoing.dto.response.product.ProductSummaryDTO;
import org.elis.progettoing.dto.response.product.TagResponseDTO;
import org.elis.progettoing.enumeration.PackageType;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.exception.entity.InvalidEntityDataException;
import org.elis.progettoing.mapper.implementation.ProductMapperImpl;
import org.elis.progettoing.mapper.implementation.ProductPackageMapperImpl;
import org.elis.progettoing.mapper.implementation.TagMapperImpl;
import org.elis.progettoing.models.Tag;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.PackageAttribute;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.ProductPackage;
import org.elis.progettoing.repository.*;
import org.elis.progettoing.service.implementation.LocalStorageService;
import org.elis.progettoing.service.implementation.ProductServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Tag> criteriaQuery;

    @Mock
    private Root<Tag> root;

    @Mock
    private TagMapperImpl tagMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductPackageRepository productPackageRepository;

    @Mock
    private PackageAttributeRepository packageAttributeRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private LocalStorageService localStorageService;

    @Mock
    private ProductMapperImpl productMapper;

    @Mock
    private ProductPackageMapperImpl productPackageMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private User user;

    @Mock
    private MultipartFile imageFile;

    private Product product;
    private ProductDetailsDTO productDetailsDTO;
    private final long productId = 1L;
    private List<Tag> tags;
    private List<TagResponseDTO> tagResponseDTOs;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setTitle("Test Product");
        productRequestDTO.setDescription("Test Description");
        productRequestDTO.setSubCategoryId(1L);

        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("testTag");
        productRequestDTO.setTags(List.of(tagDTO));

        ProductPackageRequestDTO packageRequestDTO = new ProductPackageRequestDTO();
        packageRequestDTO.setPrice(10.0);

        DynamicAttributeDTO attributeRequestDTO = new DynamicAttributeDTO();
        attributeRequestDTO.setKey("color");
        attributeRequestDTO.setValue("red");
        packageRequestDTO.setAttributes(List.of(attributeRequestDTO));

        productRequestDTO.setPackages(List.of(packageRequestDTO));

        product = new Product();
        product.setId(1L);
        product.setTitle("Product 1");

        productDetailsDTO = new ProductDetailsDTO();
        productDetailsDTO.setId(1L);
        productDetailsDTO.setTitle("Product 1");

        tags = new ArrayList<>();
        Tag tag1 = new Tag();
        tag1.setName("Technology");
        tags.add(tag1);

        Tag tag2 = new Tag();
        tag2.setName("Science");
        tags.add(tag2);

        tagResponseDTOs = new ArrayList<>();
        TagResponseDTO dto1 = new TagResponseDTO();
        dto1.setName("Technology");
        tagResponseDTOs.add(dto1);

        TagResponseDTO dto2 = new TagResponseDTO();
        dto2.setName("Science");
        tagResponseDTOs.add(dto2);

        SecurityContext securityContext = mock(SecurityContext.class);

        UsernamePasswordAuthenticationToken authenticationToken = mock(UsernamePasswordAuthenticationToken.class);
        when(authenticationToken.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authenticationToken);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateProduct_Success_WithPackages() {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setTitle("Test Product");
        productRequestDTO.setDescription("Test Description");
        productRequestDTO.setSubCategoryId(1L);

        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("testTag");
        productRequestDTO.setTags(List.of(tagDTO));

        ProductPackageRequestDTO packageRequestDTO = new ProductPackageRequestDTO();
        packageRequestDTO.setPrice(10.0);

        DynamicAttributeDTO attributeRequestDTO = new DynamicAttributeDTO();
        attributeRequestDTO.setKey("color");
        attributeRequestDTO.setValue("red");
        packageRequestDTO.setAttributes(List.of(attributeRequestDTO));

        productRequestDTO.setPackages(List.of(packageRequestDTO));

        imageFile = Mockito.mock(MultipartFile.class);
        List<MultipartFile> images = List.of(imageFile);

        product = new Product();
        when(productMapper.productRequestDTOToProduct(productRequestDTO)).thenReturn(product);
        when(tagRepository.findAll()).thenReturn(List.of(new Tag("testTag")));
        when(localStorageService.saveProductImages(images, user.getId())).thenReturn(List.of("imageUrl"));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.productToResponseDTO(product)).thenReturn(new ProductDetailsDTO());

        ProductPackage productPackage = new ProductPackage();
        when(productPackageMapper.packageRequestDTOToProductPackage(any(ProductPackageRequestDTO.class)))
                .thenReturn(productPackage);
        when(productPackageRepository.save(any(ProductPackage.class))).thenReturn(productPackage);

        PackageAttribute packageAttribute = new PackageAttribute();
        when(packageAttributeRepository.save(any(PackageAttribute.class))).thenReturn(packageAttribute);

        ProductDetailsDTO result = productService.createProduct(productRequestDTO, images);

        verify(productRepository).save(any(Product.class));
        verify(productPackageRepository, times(1)).save(any(ProductPackage.class));
        verify(packageAttributeRepository, times(1)).save(any(PackageAttribute.class));

        assertNotNull(result);
    }

    @Test
    void testCreateProduct_TagNotFound_ThrowsInvalidEntityDataException() {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setTitle("Test Product");
        productRequestDTO.setDescription("Test Description");
        productRequestDTO.setSubCategoryId(1L);

        productRequestDTO.setTags(List.of(new TagDTO("nonExistingTag")));

        Tag tag = new Tag("testTag");
        tags = List.of(tag);
        when(tagRepository.findAll()).thenReturn(tags);

        when(productMapper.productRequestDTOToProduct(productRequestDTO)).thenReturn(product);

        InvalidEntityDataException thrown = assertThrows(InvalidEntityDataException.class, () -> productService.createProduct(productRequestDTO, List.of()));

        assertEquals("Dati non validi per tag con nome = nonExistingTag. Il tag specificato non esiste.", thrown.getMessage());
    }

    @Test
    void testCreateProduct_RepositoryThrowsException_ThrowsEntityCreationException() {
        user.setEmail("example@example.com");
        product.setUser(user);

        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setTitle("Test Product");
        productRequestDTO.setDescription("Test Description");
        productRequestDTO.setSubCategoryId(1L);

        Tag tag = new Tag("testTag");
        tags = List.of(tag);
        productRequestDTO.setTags(List.of(new TagDTO("testTag")));
        List<MultipartFile> images = List.of(imageFile);

        when(tagRepository.findAll()).thenReturn(tags);
        when(localStorageService.saveProductImages(images, user.getId())).thenReturn(List.of("imageUrl"));
        when(productMapper.productRequestDTOToProduct(productRequestDTO)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("Database error"));

        EntityCreationException thrown = assertThrows(EntityCreationException.class, () -> productService.createProduct(productRequestDTO, images));

        assertEquals("Si è verificato un errore durante la creazione di prodotto con email utente = null", thrown.getMessage());
    }

    @Test
    void testFindAll_Success() {
        List<Product> products = List.of(product);
        List<ProductDetailsDTO> expectedDtos = List.of(productDetailsDTO);

        when(productRepository.findAll()).thenReturn(products);

        when(productMapper.productsToResponseDTOs(products)).thenReturn(expectedDtos);

        List<ProductDetailsDTO> result = productService.findAll();

        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).productsToResponseDTOs(products);
        assertEquals(expectedDtos, result);
    }

    @Test
    void testFindAll_EmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());
        when(productMapper.productsToResponseDTOs(List.of())).thenReturn(List.of());

        List<ProductDetailsDTO> result = productService.findAll();

        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).productsToResponseDTOs(List.of());
        assertEquals(0, result.size());
    }

    @Test
    void testFindWithDetails_Success() {
        product.setId(productId);
        product.setTitle("Product 1");

        productDetailsDTO.setId(productId);
        productDetailsDTO.setTitle("Product 1");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.productToResponseDTO(product)).thenReturn(productDetailsDTO);

        ProductDetailsDTO result = productService.findWithDetails(productId);

        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, times(1)).productToResponseDTO(product);
        assertEquals(productDetailsDTO, result);
    }

    @Test
    void testFindWithDetails_ProductNotFound() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act and Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> productService.findWithDetails(productId));
        assertEquals("Nessun prodotto con ID = 1 è stato trovato.", exception.getMessage());
    }


    @Test
    void findAllSummaryByUserId_UserExists_ReturnsProductSummaryList() {
        long userId = 1L;
        user.setId(userId);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setTitle("Product 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setTitle("Product 2");

        List<Product> products = List.of(product1, product2);
        List<ProductSummaryDTO> productSummaryDTOs = List.of(
                new ProductSummaryDTO(1L, "Product 1"),
                new ProductSummaryDTO(2L, "Product 2")
        );

        when(userRepository.existsById(userId)).thenReturn(true);
        when(productRepository.findAllByUserId(userId)).thenReturn(products);
        when(productMapper.productsToSummaryResponseDTOs(products)).thenReturn(productSummaryDTOs);

        List<ProductSummaryDTO> result = productService.findAllSummaryByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getTitle());
        assertEquals("Product 2", result.get(1).getTitle());
    }

    @Test
    void findAllSummaryByUserId_UserNotFound_ThrowsEntityNotFoundException() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> productService.findAllSummaryByUserId(userId));
        assertEquals("Nessun utente con ID = 1 è stato trovato.", exception.getMessage());
    }

    @Test
    void findAllSummaryByUserId_UserExists_NoProducts_ReturnsEmptyList() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(productRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        List<ProductSummaryDTO> result = productService.findAllSummaryByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testRemoveProduct_Success() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(ticketRepository).unsetProduct(productId);
        doNothing().when(localStorageService).deleteImages(product.getUrlProductPhotos());

        // Act
        boolean result = productService.removeProduct(productId);

        // Assert
        verify(productRepository, times(1)).findById(productId);  // Verifica che il repository sia stato chiamato per trovare il prodotto
        verify(ticketRepository, times(1)).unsetProduct(productId);  // Verifica che i ticket siano stati scollegati
        verify(localStorageService, times(1)).deleteImages(product.getUrlProductPhotos());  // Verifica che le immagini siano state eliminate
        verify(productRepository, times(1)).delete(product);  // Verifica che il prodotto sia stato eliminato
        assertTrue(result);  // Verifica che il risultato sia true
    }

    @Test
    void testRemoveProduct_ProductNotFound() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> productService.removeProduct(productId));
        assertEquals("Nessun prodotto con ID = 1 è stato trovato.", exception.getMessage());
    }

    @Test
    void testRemoveProduct_ExceptionDuringDeletion() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(ticketRepository).unsetProduct(productId);
        doNothing().when(localStorageService).deleteImages(product.getUrlProductPhotos());
        doThrow(new RuntimeException("Database error")).when(productRepository).delete(product);

        // Act & Assert
        EntityDeletionException exception = assertThrows(EntityDeletionException.class, () -> productService.removeProduct(productId));
        assertEquals("Si è verificato un errore durante il tentativo di eliminare prodotto con ID = 1.", exception.getMessage());
    }


    @Test
    void testGetTags_WithNameFilter() {
        // Arrange
        String nameFilter = "Tech";

        // Mock dell'EntityManager e CriteriaBuilder
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Tag.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Tag.class)).thenReturn(root);

        // Mock del comportamento dei Predicati
        Predicate namePredicate = mock(Predicate.class);
        when(criteriaBuilder.like(any(), anyString())).thenReturn(namePredicate);

        // Mock dei risultati della query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(mock(TypedQuery.class));
        when(entityManager.createQuery(criteriaQuery).getResultList()).thenReturn(tags);

        // Mock del TagMapper
        when(tagMapper.tagToTagListResponseDTO(tags)).thenReturn(tagResponseDTOs);

        // Act
        List<TagResponseDTO> result = productService.getTags(nameFilter);

        // Assert
        verify(entityManager, times(1)).getCriteriaBuilder();
        verify(criteriaBuilder, times(1)).createQuery(Tag.class);
        verify(criteriaBuilder, times(1)).like(any(), anyString());  // Verifica che il filtro LIKE sia stato applicato
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Technology", result.getFirst().getName());
    }

    @Test
    void testGetTags_WithEmptyNameFilter() {
        // Arrange
        String nameFilter = "";

        // Mock dell'EntityManager e CriteriaBuilder
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Tag.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Tag.class)).thenReturn(root);

        // Mock dei risultati della query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(mock(TypedQuery.class));
        when(entityManager.createQuery(criteriaQuery).getResultList()).thenReturn(tags);

        // Mock del TagMapper
        when(tagMapper.tagToTagListResponseDTO(tags)).thenReturn(tagResponseDTOs);

        // Act
        List<TagResponseDTO> result = productService.getTags(nameFilter);

        // Assert
        verify(entityManager, times(1)).getCriteriaBuilder();
        verify(criteriaBuilder, times(1)).createQuery(Tag.class);
        verify(criteriaQuery, times(1)).from(Tag.class);
        assertNotNull(result);
        assertEquals(2, result.size());  // Dovrebbe restituire entrambi i tag
    }

    @Test
    void testGetTags_WithNullNameFilter() {
        // Arrange
        String nameFilter = null;

        // Mock dell'EntityManager e CriteriaBuilder
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Tag.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Tag.class)).thenReturn(root);

        // Mock dei risultati della query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(mock(TypedQuery.class));
        when(entityManager.createQuery(criteriaQuery).getResultList()).thenReturn(tags);

        // Mock del TagMapper
        when(tagMapper.tagToTagListResponseDTO(tags)).thenReturn(tagResponseDTOs);

        // Act
        List<TagResponseDTO> result = productService.getTags(nameFilter);

        // Assert
        verify(entityManager, times(1)).getCriteriaBuilder();
        verify(criteriaBuilder, times(1)).createQuery(Tag.class);
        verify(criteriaQuery, times(1)).from(Tag.class);
        assertNotNull(result);
        assertEquals(2, result.size());  // Dovrebbe restituire entrambi i tag
    }

    @Test
    void testGetTags_WithNoMatchingTags() {
        // Arrange
        String nameFilter = "NonExistent";

        // Mock dell'EntityManager e CriteriaBuilder
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Tag.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Tag.class)).thenReturn(root);

        // Mock dei risultati della query (nessun tag corrisponde)
        when(entityManager.createQuery(criteriaQuery)).thenReturn(mock(TypedQuery.class));
        when(entityManager.createQuery(criteriaQuery).getResultList()).thenReturn(new ArrayList<>());

        // Mock del TagMapper
        when(tagMapper.tagToTagListResponseDTO(new ArrayList<>())).thenReturn(new ArrayList<>());

        // Act
        List<TagResponseDTO> result = productService.getTags(nameFilter);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());  // Non ci sono tag che corrispondono al filtro
    }

    @Test
    void testGetProductSummary_Success() {
        // Arrange: Creazione di mock per i dati
        ProductPackage basicPackage = new ProductPackage();
        basicPackage.setType(PackageType.BASIC);
        basicPackage.setPrice(19.99);

        product.setId(1L);
        product.setTitle("Product 1");
        product.setDescription("Description 1");
        product.setPackages(Collections.singletonList(basicPackage));

        ProductSummaryDTO productSummaryDTO = new ProductSummaryDTO();
        productSummaryDTO.setId(1L);
        productSummaryDTO.setTitle("Product 1");
        productSummaryDTO.setDescription("Description 1");

        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));
        when(productMapper.productToSummaryDTO(product)).thenReturn(productSummaryDTO);

        // Act: Esegui il metodo da testare
        List<ProductSummaryDTO> result = productService.getProductSummary();

        // Assert: Verifica il risultato
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(19.99, result.getFirst().getStartPrice());
        assertEquals("Product 1", result.getFirst().getTitle());

        // Verifica che i metodi siano stati chiamati
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).productToSummaryDTO(product);
    }

    @Test
    void testGetProductSummary_BasicPackageNotFound() {
        // Arrange: Creazione di un prodotto senza pacchetto BASIC
        ProductPackage premiumPackage = new ProductPackage();
        premiumPackage.setType(PackageType.PREMIUM);
        premiumPackage.setPrice(49.99);

        product.setId(1L);
        product.setTitle("Product 1");
        product.setDescription("Description 1");
        product.setPackages(Collections.singletonList(premiumPackage));

        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

        // Act & Assert: Verifica che venga lanciata un'eccezione
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> productService.getProductSummary());

        assertEquals("Nessun Pacchetto BASE non trovato per il prodotto con productId = 1 è stato trovato.", exception.getMessage());

        // Verifica che i metodi siano stati chiamati
        verify(productRepository, times(1)).findAll();
        verifyNoInteractions(productMapper);
    }

    @Test
    void testGetProductSummary_EmptyProductList() {
        // Arrange: Nessun prodotto restituito dal repository
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Act: Esegui il metodo da testare
        List<ProductSummaryDTO> result = productService.getProductSummary();

        // Assert: La lista deve essere vuota
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verifica che il repository sia stato chiamato
        verify(productRepository, times(1)).findAll();
        verifyNoInteractions(productMapper);
    }
}
