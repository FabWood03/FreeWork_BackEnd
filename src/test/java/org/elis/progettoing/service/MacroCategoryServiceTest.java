package org.elis.progettoing.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.elis.progettoing.dto.request.category.MacroCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.MacroCategoryResponseDTO;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityEditException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.MacroCategoryMapper;
import org.elis.progettoing.models.category.MacroCategory;
import org.elis.progettoing.repository.MacroCategoryRepository;
import org.elis.progettoing.service.implementation.MacroCategoryServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MacroCategoryServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private MacroCategoryRepository macroCategoryRepository;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<MacroCategory> criteriaQuery;

    @Mock
    private Root<MacroCategory> root;

    @Mock
    private TypedQuery<MacroCategory> typedQuery;

    @Mock
    private MacroCategoryMapper macroCategoryMapper;

    @InjectMocks
    private MacroCategoryServiceImpl macroCategoryServiceImpl;

    private MacroCategoryRequestDTO macroCategoryRequestDTO;
    private MacroCategory macroCategory;
    private MacroCategoryResponseDTO macroCategoryResponseDTO;

    @BeforeEach
    void setUp() {
        macroCategoryRequestDTO = new MacroCategoryRequestDTO();
        macroCategoryRequestDTO.setId(1L);
        macroCategoryRequestDTO.setName("Test MacroCategory");

        macroCategory = new MacroCategory();
        macroCategory.setId(1L);
        macroCategory.setName("Test MacroCategory");

        macroCategoryResponseDTO = new MacroCategoryResponseDTO();
        macroCategoryResponseDTO.setId(1L);
        macroCategoryResponseDTO.setName("Test MacroCategory");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreate_Success() {
        when(macroCategoryMapper.requestDTOToMacroCategory(macroCategoryRequestDTO))
                .thenReturn(macroCategory);

        when(macroCategoryRepository.save(any(MacroCategory.class)))
                .thenReturn(macroCategory);

        when(macroCategoryMapper.macroCategoryToResponseDTO(macroCategory))
                .thenReturn(macroCategoryResponseDTO);

        MacroCategoryResponseDTO result = macroCategoryServiceImpl.create(macroCategoryRequestDTO);

        assertNotNull(result);
        assertEquals("Test MacroCategory", result.getName());

        verify(macroCategoryRepository, times(1)).save(macroCategory);
    }

    @Test
    void testCreate_EntityCreationException() {
        when(macroCategoryMapper.requestDTOToMacroCategory(macroCategoryRequestDTO))
                .thenReturn(macroCategory);

        when(macroCategoryRepository.save(any(MacroCategory.class)))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> macroCategoryServiceImpl.create(macroCategoryRequestDTO));

        verify(macroCategoryRepository, times(1)).save(macroCategory);
        verify(macroCategoryMapper, times(1)).requestDTOToMacroCategory(macroCategoryRequestDTO);
        verify(macroCategoryMapper, never()).macroCategoryToResponseDTO(any());
    }

    @Test
    void testDelete_Success() {
        when(macroCategoryRepository.findById(1L)).thenReturn(Optional.of(macroCategory));

        boolean result = macroCategoryServiceImpl.delete(macroCategoryRequestDTO);

        assertTrue(result);
        verify(macroCategoryRepository, times(1)).findById(1L);
        verify(macroCategoryRepository, times(1)).delete(macroCategory);
    }

    @Test
    void testDelete_EntityNotFound() {
        when(macroCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> macroCategoryServiceImpl.delete(macroCategoryRequestDTO));

        assertEquals("Nessun macro categoria con ID = 1 è stato trovato.", exception.getMessage());
        verify(macroCategoryRepository, times(1)).findById(1L);
        verify(macroCategoryRepository, never()).delete(any());
    }

    @Test
    void testDelete_EntityDeletion() {
        when(macroCategoryRepository.findById(1L)).thenReturn(Optional.of(macroCategory));
        doThrow(new RuntimeException("Deletion error")).when(macroCategoryRepository).delete(macroCategory);

        EntityDeletionException exception = assertThrows(EntityDeletionException.class, () -> macroCategoryServiceImpl.delete(macroCategoryRequestDTO));

        assertEquals("Si è verificato un errore durante il tentativo di eliminare macro categoria con nome = Test MacroCategory.", exception.getMessage());
        verify(macroCategoryRepository, times(1)).findById(1L);
        verify(macroCategoryRepository, times(1)).delete(macroCategory);
    }

    @Test
    void testFindFilteredMacroCategory_WithNameFilter() {
        String nameFilter = "example";
        MacroCategory mockCategory = new MacroCategory();
        mockCategory.setName("ExampleCategory");

        List<MacroCategory> mockResultList = List.of(mockCategory);
        MacroCategoryResponseDTO mockDTO = new MacroCategoryResponseDTO();
        mockDTO.setName("ExampleCategory");

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(MacroCategory.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(MacroCategory.class)).thenReturn(root);

        Expression<String> mockExpression = mock(Expression.class);
        when(criteriaBuilder.lower(root.get("name"))).thenReturn(mockExpression);

        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.like(mockExpression, "%example%")).thenReturn(mockPredicate);

        lenient().when(criteriaQuery.where(any(Predicate.class))).thenReturn(criteriaQuery);
        lenient().when(criteriaQuery.where((Predicate) null)).thenReturn(criteriaQuery);

        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(mockResultList);

        when(macroCategoryMapper.macroCategoriesToMacroCategoryDTOs(mockResultList))
                .thenReturn(List.of(mockDTO));

        List<MacroCategoryResponseDTO> result = macroCategoryServiceImpl.findFilteredMacroCategory(nameFilter);

        assertEquals(1, result.size());
        assertEquals("ExampleCategory", result.getFirst().getName());
        verify(entityManager).getCriteriaBuilder();
        verify(criteriaBuilder).createQuery(MacroCategory.class);
        verify(criteriaQuery).from(MacroCategory.class);
        verify(typedQuery).getResultList();
        verify(macroCategoryMapper).macroCategoriesToMacroCategoryDTOs(mockResultList);
    }

    @Test
    void testFindFilteredMacroCategory_WithNullFilter() {
        // Setup
        String nameFilter = null;
        List<MacroCategory> mockResultList = List.of();
        List<MacroCategoryResponseDTO> mockDTOList = List.of();

        // Mocking the behavior of the entity manager and other components
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(MacroCategory.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(MacroCategory.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(mockResultList);
        when(macroCategoryMapper.macroCategoriesToMacroCategoryDTOs(mockResultList)).thenReturn(mockDTOList);

        // Call the method under test
        List<MacroCategoryResponseDTO> result = macroCategoryServiceImpl.findFilteredMacroCategory(nameFilter);

        // Verify the results
        assertEquals(0, result.size());
        verify(entityManager).getCriteriaBuilder();
        verify(criteriaBuilder).createQuery(MacroCategory.class);
        verify(criteriaQuery).from(MacroCategory.class);
        verify(typedQuery).getResultList();
    }

    @Test
    void testFindFilteredMacroCategory_WithEmptyFilter() {
        String nameFilter = "   "; // filtro vuoto (spazi bianchi)
        List<MacroCategory> mockResultList = List.of();
        List<MacroCategoryResponseDTO> mockDTOList = List.of();

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(MacroCategory.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(MacroCategory.class)).thenReturn(root);

        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(mockResultList);

        when(macroCategoryMapper.macroCategoriesToMacroCategoryDTOs(mockResultList)).thenReturn(mockDTOList);

        List<MacroCategoryResponseDTO> result = macroCategoryServiceImpl.findFilteredMacroCategory(nameFilter);

        assertEquals(0, result.size());
        verify(entityManager).getCriteriaBuilder();
        verify(criteriaBuilder).createQuery(MacroCategory.class);
        verify(criteriaQuery).from(MacroCategory.class);
        verify(typedQuery).getResultList();
    }

    @Test
    void update_SuccessfulUpdate() {
        // Mock repository findById and save methods
        when(macroCategoryRepository.findById(macroCategoryRequestDTO.getId())).thenReturn(Optional.of(macroCategory));
        when(macroCategoryRepository.save(macroCategory)).thenReturn(macroCategory);
        when(macroCategoryMapper.macroCategoryToResponseDTO(macroCategory)).thenReturn(macroCategoryResponseDTO);

        // Call the method under test
        MacroCategoryResponseDTO result = macroCategoryServiceImpl.update(macroCategoryRequestDTO);

        // Assert the results
        assertNotNull(result);
        assertEquals(macroCategoryRequestDTO.getId(), result.getId());
        assertEquals(macroCategoryRequestDTO.getName(), result.getName());

        // Verify interactions
        verify(macroCategoryRepository).findById(macroCategoryRequestDTO.getId());
        verify(macroCategoryRepository).save(macroCategory);
        verify(macroCategoryMapper).macroCategoryToResponseDTO(macroCategory);
    }

    @Test
    void update_EntityNotFound() {
        // Mock repository findById method to return empty
        when(macroCategoryRepository.findById(macroCategoryRequestDTO.getId())).thenReturn(Optional.empty());

        // Assert exception is thrown
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                macroCategoryServiceImpl.update(macroCategoryRequestDTO)
        );

        assertEquals("Nessun macro categoria con ID = 1 è stato trovato.", exception.getMessage());

        // Verify interactions
        verify(macroCategoryRepository).findById(macroCategoryRequestDTO.getId());
        verify(macroCategoryRepository, never()).save(any());
        verifyNoInteractions(macroCategoryMapper);
    }

    @Test
    void update_EntityEditException() {
        // Mock repository findById method to return the macroCategory
        when(macroCategoryRepository.findById(macroCategoryRequestDTO.getId())).thenReturn(Optional.of(macroCategory));

        // Mock repository save method to throw an exception
        when(macroCategoryRepository.save(macroCategory)).thenThrow(new DataAccessException("Database error") {
        });

        // Assert exception is thrown
        EntityEditException exception = assertThrows(EntityEditException.class, () ->
                macroCategoryServiceImpl.update(macroCategoryRequestDTO)
        );

        assertEquals("Si è verificato un errore nell'aggiornamento dell'entità ID con macro categoria = 1.", exception.getMessage());

        // Verify interactions
        verify(macroCategoryRepository).findById(macroCategoryRequestDTO.getId());
        verify(macroCategoryRepository).save(macroCategory);
        verifyNoInteractions(macroCategoryMapper);
    }

    @Test
    void findById_Success() {
        // Mock repository findById method
        when(macroCategoryRepository.findById(1L)).thenReturn(Optional.of(macroCategory));
        when(macroCategoryMapper.macroCategoryToResponseDTO(macroCategory)).thenReturn(macroCategoryResponseDTO);

        // Call the method under test
        MacroCategoryResponseDTO result = macroCategoryServiceImpl.findById(1L);

        // Assert the results
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test MacroCategory", result.getName());

        // Verify interactions
        verify(macroCategoryRepository).findById(1L);
        verify(macroCategoryMapper).macroCategoryToResponseDTO(macroCategory);
    }

    @Test
    void findById_EntityNotFound() {
        // Mock repository findById method to return empty
        when(macroCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert exception is thrown
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                macroCategoryServiceImpl.findById(1L)
        );

        assertEquals("Nessun macro categoria con ID = 1 è stato trovato.", exception.getMessage());

        // Verify interactions
        verify(macroCategoryRepository).findById(1L);
        verifyNoInteractions(macroCategoryMapper);
    }

    @Test
    void findAll_Success() {
        // Mock repository findAll method
        List<MacroCategory> mockCategories = Collections.singletonList(macroCategory);
        List<MacroCategoryResponseDTO> mockResponseDTOs = Collections.singletonList(macroCategoryResponseDTO);

        when(macroCategoryRepository.findAll()).thenReturn(mockCategories);
        when(macroCategoryMapper.macroCategoriesToMacroCategoryDTOs(mockCategories)).thenReturn(mockResponseDTOs);

        // Call the method under test
        List<MacroCategoryResponseDTO> result = macroCategoryServiceImpl.findAll();

        // Assert the results
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals("Test MacroCategory", result.getFirst().getName());

        // Verify interactions
        verify(macroCategoryRepository).findAll();
        verify(macroCategoryMapper).macroCategoriesToMacroCategoryDTOs(mockCategories);
    }

    @Test
    void findAll_EmptyList() {
        // Mock repository findAll method to return an empty list
        when(macroCategoryRepository.findAll()).thenReturn(List.of());
        when(macroCategoryMapper.macroCategoriesToMacroCategoryDTOs(List.of())).thenReturn(List.of());

        // Call the method under test
        List<MacroCategoryResponseDTO> result = macroCategoryServiceImpl.findAll();

        // Assert the results
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interactions
        verify(macroCategoryRepository).findAll();
        verify(macroCategoryMapper).macroCategoriesToMacroCategoryDTOs(List.of());
    }
}
