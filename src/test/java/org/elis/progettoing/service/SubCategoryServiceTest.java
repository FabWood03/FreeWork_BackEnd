package org.elis.progettoing.service;

import org.elis.progettoing.dto.request.category.SubCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.SubCategoryResponseDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityEditException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.ProductMapper;
import org.elis.progettoing.mapper.definition.SubCategoryMapper;
import org.elis.progettoing.models.category.SubCategory;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.repository.ProductRepository;
import org.elis.progettoing.repository.SubCategoryRepository;
import org.elis.progettoing.service.implementation.SubCategoryServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SubCategoryServiceTest {

    @InjectMocks
    SubCategoryServiceImpl subCategoryServiceImpl;

    @Mock
    SubCategoryRepository subCategoryRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    SubCategoryMapper subCategoryMapper;

    @Mock
    ProductMapper productMapper;

    private SubCategoryRequestDTO subCategoryRequestDTO;
    private SubCategory subCategory;
    private SubCategoryResponseDTO subCategoryResponseDTO;
    private Product product;
    private ProductDetailsDTO productDetailsDTO;

    @BeforeEach
    void setUp() {
        subCategoryRequestDTO = new SubCategoryRequestDTO();
        subCategoryRequestDTO.setId(1L);
        subCategoryRequestDTO.setName("Test SubCategory");

        subCategory = new SubCategory();
        subCategory.setId(1L);
        subCategory.setName("Test SubCategory");

        subCategoryResponseDTO = new SubCategoryResponseDTO();
        subCategoryResponseDTO.setId(1L);
        subCategoryResponseDTO.setName("Test SubCategory");

        product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");

        productDetailsDTO = new ProductDetailsDTO();
        productDetailsDTO.setId(1L);
        productDetailsDTO.setTitle("Test Product");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreate_Success() {
        Mockito.when(subCategoryMapper.requestDTOToSubCategory(subCategoryRequestDTO)).thenReturn(subCategory);
        Mockito.when(subCategoryRepository.save(subCategory)).thenReturn(subCategory);
        Mockito.when(subCategoryMapper.subCategoryToResponseDTO(subCategory)).thenReturn(subCategoryResponseDTO);

        SubCategoryResponseDTO result = subCategoryServiceImpl.create(subCategoryRequestDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Test SubCategory", result.getName());

        Mockito.verify(subCategoryRepository, Mockito.times(1)).save(subCategory);
        Mockito.verify(subCategoryMapper, Mockito.times(1)).requestDTOToSubCategory(subCategoryRequestDTO);
        Mockito.verify(subCategoryMapper, Mockito.times(1)).subCategoryToResponseDTO(subCategory);
    }

    @Test
    void testCreate_EntityCreationException() {
        Mockito.when(subCategoryMapper.requestDTOToSubCategory(subCategoryRequestDTO))
                .thenReturn(subCategory);

        Mockito.when(subCategoryRepository.save(subCategory))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class, () -> subCategoryServiceImpl.create(subCategoryRequestDTO));

        Mockito.verify(subCategoryRepository, Mockito.times(1)).save(subCategory);
        Mockito.verify(subCategoryMapper, Mockito.times(1)).requestDTOToSubCategory(subCategoryRequestDTO);
        Mockito.verify(subCategoryMapper, Mockito.never()).subCategoryToResponseDTO(Mockito.any());
    }

    @Test
    void testDelete_Success() {
        Mockito.when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = subCategoryServiceImpl.delete(1L);

        Assertions.assertTrue(result);
        Mockito.verify(subCategoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(subCategoryRepository, Mockito.times(1)).delete(subCategory);
    }

    @Test
    void testDelete_EntityNotFound() {
        Mockito.when(subCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> subCategoryServiceImpl.delete(1L));

        Assertions.assertEquals("Nessun SubCategory con id = 1 è stato trovato.", exception.getMessage());
        Mockito.verify(subCategoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(subCategoryRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void testDelete_EntityDeletion() {
        Mockito.when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));
        Mockito.doThrow(new RuntimeException()).when(subCategoryRepository).delete(subCategory);

        EntityDeletionException exception = Assertions.assertThrows(EntityDeletionException.class, () -> subCategoryServiceImpl.delete(1L));

        Assertions.assertEquals("Si è verificato un errore durante il tentativo di eliminare SubCategory con id = 1.", exception.getMessage());
        Mockito.verify(subCategoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(subCategoryRepository, Mockito.times(1)).delete(subCategory);
    }

    @Test
    void testUpdate_Success() {
        Mockito.when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));
        Mockito.when(subCategoryRepository.save(subCategory)).thenReturn(subCategory);
        Mockito.when(subCategoryMapper.subCategoryToResponseDTO(subCategory)).thenReturn(subCategoryResponseDTO);

        SubCategoryResponseDTO result = subCategoryServiceImpl.update(subCategoryRequestDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Test SubCategory", result.getName());

        Mockito.verify(subCategoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(subCategoryRepository, Mockito.times(1)).save(subCategory);
        Mockito.verify(subCategoryMapper, Mockito.times(1)).subCategoryToResponseDTO(subCategory);
    }

    @Test
    void testUpdate_EntityNotFound() {
        Mockito.when(subCategoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> subCategoryServiceImpl.update(subCategoryRequestDTO));

        Assertions.assertEquals("Nessun SubCategory con id = 1 è stato trovato.", exception.getMessage());
        Mockito.verify(subCategoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(subCategoryRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(subCategoryMapper, Mockito.never()).subCategoryToResponseDTO(Mockito.any());
    }

    @Test
    void testUpdate_EntityEdit() {
        Mockito.when(subCategoryRepository.findById(1L))
                .thenReturn(Optional.of(subCategory));

        Mockito.when(subCategoryRepository.save(subCategory))
                .thenThrow(new RuntimeException());

        EntityEditException exception = Assertions.assertThrows(EntityEditException.class, () -> subCategoryServiceImpl.update(subCategoryRequestDTO));

        Assertions.assertEquals("Si è verificato un errore nell'aggiornamento dell'entità nome con SubCategory = 1.", exception.getMessage());
        Mockito.verify(subCategoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(subCategoryRepository, Mockito.times(1)).save(subCategory);
        Mockito.verify(subCategoryMapper, Mockito.never()).subCategoryToResponseDTO(Mockito.any());
    }

    @Test
    void testFindById_Success() {
        Mockito.when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));
        Mockito.when(subCategoryMapper.subCategoryToResponseDTO(subCategory)).thenReturn(subCategoryResponseDTO);

        SubCategoryResponseDTO result = subCategoryServiceImpl.findById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Test SubCategory", result.getName());

        Mockito.verify(subCategoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(subCategoryMapper, Mockito.times(1)).subCategoryToResponseDTO(subCategory);
    }

    @Test
    void testFindById_EntityNotFound() {
        Mockito.when(subCategoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> subCategoryServiceImpl.findById(1L));

        Assertions.assertEquals("Nessun SubCategory con id = 1 è stato trovato.", exception.getMessage());
        Mockito.verify(subCategoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(subCategoryMapper, Mockito.never()).subCategoryToResponseDTO(Mockito.any());
    }

    @Test
    void testFindAll_Success() {
        List<SubCategory> subCategories = List.of(subCategory);
        List<SubCategoryResponseDTO> subCategoryResponseDTOs = List.of(subCategoryResponseDTO);

        Mockito.when(subCategoryRepository.findAll()).thenReturn(subCategories);
        Mockito.when(subCategoryMapper.subCategoriesToSubCategoryDTOs(subCategories)).thenReturn(subCategoryResponseDTOs);

        List<SubCategoryResponseDTO> result = subCategoryServiceImpl.findAll();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1L, result.getFirst().getId());
        Assertions.assertEquals("Test SubCategory", result.getFirst().getName());

        Mockito.verify(subCategoryRepository, Mockito.times(1)).findAll();
        Mockito.verify(subCategoryMapper, Mockito.times(1)).subCategoriesToSubCategoryDTOs(subCategories);
    }

    @Test
    void testFindProductByCategory_Success() {
        List<Product> products = List.of(product);
        List<ProductDetailsDTO> productDetailsDTOS = List.of(productDetailsDTO);

        Mockito.when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));
        Mockito.when(productRepository.findBySubCategory(subCategory)).thenReturn(products);
        Mockito.when(productMapper.productsToResponseDTOs(products)).thenReturn(productDetailsDTOS);

        List<ProductDetailsDTO> result = subCategoryServiceImpl.findProductsByCategory(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1L, result.getFirst().getId());
        Assertions.assertEquals("Test Product", result.getFirst().getTitle());

        Mockito.verify(subCategoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(productRepository, Mockito.times(1)).findBySubCategory(subCategory);
        Mockito.verify(productMapper, Mockito.times(1)).productsToResponseDTOs(products);
    }
}

