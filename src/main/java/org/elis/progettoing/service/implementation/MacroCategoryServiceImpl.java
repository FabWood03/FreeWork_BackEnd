package org.elis.progettoing.service.implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.elis.progettoing.dto.request.category.MacroCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.MacroCategoryResponseDTO;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityEditException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.MacroCategoryMapper;
import org.elis.progettoing.models.category.MacroCategory;
import org.elis.progettoing.repository.MacroCategoryRepository;
import org.elis.progettoing.service.definition.MacroCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the service for macro category management.
 * Manages the creation, updating, deletion and search of macro categories.
 */
@Service
public class MacroCategoryServiceImpl implements MacroCategoryService {

    private final EntityManager entityManager;
    private final MacroCategoryMapper macroCategoryMapper;
    private final MacroCategoryRepository macroCategoryRepository;

    private static final String MACRO_CATEGORY_NAME = "macro categoria";

    /**
     * Constructor for MacroCategoryServiceImpl.
     *
     * @param entityManager the EntityManager for interaction with the database.
     * @param macroCategoryMapper the mapper for conversion between entities and DTOs.
     * @param macroCategoryRepository the repository for accessing macro categories.
     */
    public MacroCategoryServiceImpl(EntityManager entityManager, MacroCategoryMapper macroCategoryMapper, MacroCategoryRepository macroCategoryRepository) {
        this.entityManager = entityManager;
        this.macroCategoryMapper = macroCategoryMapper;
        this.macroCategoryRepository = macroCategoryRepository;
    }

    /**
     * Creates a new macro category.
     *
     * @param macroCategoryRequestDTO the data of the macro category to be created.
     * @return the response containing the details of the macro category created.
     * @throws EntityCreationException if an error occurs while creating the macro category.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MacroCategoryResponseDTO create(MacroCategoryRequestDTO macroCategoryRequestDTO) {
        MacroCategory macroCategory = macroCategoryMapper.requestDTOToMacroCategory(macroCategoryRequestDTO);

        try {
            macroCategoryRepository.save(macroCategory);
        } catch (Exception e) {
            throw new EntityCreationException(MACRO_CATEGORY_NAME, "nome", macroCategoryRequestDTO.getName());
        }

        return macroCategoryMapper.macroCategoryToResponseDTO(macroCategory);
    }

    /**
     * Updates an existing macro category.
     *
     * @param macroCategoryRequestDTO the details of the macro category to be updated.
     * @return the response containing the details of the updated category macro.
     * @throws EntityNotFoundException if the category macro with the provided ID does not exist.
     * @throws EntityEditException if an error occurs while updating the category macro.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MacroCategoryResponseDTO update(MacroCategoryRequestDTO macroCategoryRequestDTO) {
        MacroCategory macroCategory = macroCategoryRepository.findById(macroCategoryRequestDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(MACRO_CATEGORY_NAME, "ID", macroCategoryRequestDTO.getId()));

        macroCategory.setName(macroCategoryRequestDTO.getName());

        try {
            macroCategoryRepository.save(macroCategory);
        } catch (Exception e) {
            throw new EntityEditException(MACRO_CATEGORY_NAME, "ID", macroCategoryRequestDTO.getId());
        }

        return macroCategoryMapper.macroCategoryToResponseDTO(macroCategory);
    }

    /**
     * Deletes an existing macro category.
     *
     * @param macroCategoryRequestDTO the data of the macro category to be deleted.
     * @return {@code true} if the macro category was successfully deleted.
     * @throws EntityNotFoundException if the macro category with the provided ID does not exist.
     * @throws EntityDeletionException if an error occurs while deleting the macro category.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(MacroCategoryRequestDTO macroCategoryRequestDTO) {
        MacroCategory macroCategory = macroCategoryRepository.findById(macroCategoryRequestDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(MACRO_CATEGORY_NAME, "ID", macroCategoryRequestDTO.getId()));

        try {
            macroCategoryRepository.delete(macroCategory);
        } catch (Exception e) {
            throw new EntityDeletionException(MACRO_CATEGORY_NAME, "nome", macroCategoryRequestDTO.getName());
        }

        return true;
    }

    /**
     * Finds macro categories based on a filter.
     *
     * @param nameFilter the filter to apply to the macro categories.
     * @return the list of macro categories that match the filter.
     */
    @Override
    public List<MacroCategoryResponseDTO> findFilteredMacroCategory(String nameFilter) {
        // Creazione di un'istanza di CriteriaBuilder e CriteriaQuery
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<MacroCategory> criteriaQuery = criteriaBuilder.createQuery(MacroCategory.class);

        // Definizione della radice della query (la tabella "MacroCategory")
        Root<MacroCategory> root = criteriaQuery.from(MacroCategory.class);

        // Crea una lista di predicati per il filtro
        List<Predicate> predicates = new ArrayList<>();

        // Aggiungi un filtro sul nome se "nameFilter" non è nullo o vuoto
        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            // Filtro case-insensitive con "LIKE" e corrispondenza parziale
            Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + nameFilter.trim().toLowerCase() + "%"
            );
            predicates.add(namePredicate);
        }

        // Aggiungi i predicati alla query
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Esecuzione della query
        List<MacroCategory> macroCategoryList = entityManager.createQuery(criteriaQuery).getResultList();

        // Mappatura dei risultati in DTO
        return macroCategoryMapper.macroCategoriesToMacroCategoryDTOs(macroCategoryList);
    }

    /**
     * Find a macro category by ID.
     *
     * @param id the ID of the macro category to be searched.
     * @return the response containing the details of the macro category found.
     * @throws EntityNotFoundException if the category macro with the given ID does not exist.
     */
    @Override
    @Transactional(readOnly = true)
    public MacroCategoryResponseDTO findById(long id) {
        MacroCategory macroCategory = macroCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MACRO_CATEGORY_NAME, "ID", id));

        return macroCategoryMapper.macroCategoryToResponseDTO(macroCategory);
    }

    /**
     * Find all macro categories.
     *
     * @return the list of all macro categories.
     */
    @Override
    @Transactional(readOnly = true)
    public List<MacroCategoryResponseDTO> findAll() {
        List<MacroCategory> macroCategories = macroCategoryRepository.findAll();
        return macroCategoryMapper.macroCategoriesToMacroCategoryDTOs(macroCategories);
    }
}