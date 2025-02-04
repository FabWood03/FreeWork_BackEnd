package org.elis.progettoing.service.implementation;

import org.elis.progettoing.dto.request.category.SubCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.SubCategoryResponseDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityEditException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.ProductMapper;
import org.elis.progettoing.mapper.definition.SubCategoryMapper;
import org.elis.progettoing.models.category.SubCategory;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.repository.ProductRepository;
import org.elis.progettoing.repository.SubCategoryRepository;
import org.elis.progettoing.service.definition.SubCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the service for managing subcategories.
 * Manages the creation, updating, deletion and search of subcategories.
 */
@Service
public class SubCategoryServiceImpl implements SubCategoryService {
    private final SubCategoryRepository subCategoryRepository;
    private final ProductMapper productMapper;
    private final SubCategoryMapper subCategoryMapper;
    private final ProductRepository productRepository;
    
    private static final String SUB_CATEGORY_NAME = "SubCategory";

    /**
     * Constructor for SubCategoryServiceImpl.
     *
     * @param subCategoryRepository the repository for accessing subcategories.
     * @param subCategoryMapper the mapper for converting between entities and DTOs.
     * @param productRepository the repository for accessing products.
     * @param productMapper the mapper for converting between entities and DTOs.
     */
    public SubCategoryServiceImpl(SubCategoryRepository subCategoryRepository, SubCategoryMapper subCategoryMapper, ProductRepository productRepository, ProductMapper productMapper) {
        this.subCategoryRepository = subCategoryRepository;
        this.subCategoryMapper = subCategoryMapper;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * Create a new subcategory.
     *
     * @param subCategoryRequestDTO the subcategory data to create.
     * @return the response containing the details of the created subcategory.
     * @throws EntityCreationException if an error occurs while creating the subcategory.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubCategoryResponseDTO create(SubCategoryRequestDTO subCategoryRequestDTO) {
        SubCategory subCategory = subCategoryMapper.requestDTOToSubCategory(subCategoryRequestDTO);

        try {
            subCategoryRepository.save(subCategory);
        } catch (Exception e) {
            throw new EntityCreationException("sotto categoria", "nome", subCategoryRequestDTO.getName());
        }

        return subCategoryMapper.subCategoryToResponseDTO(subCategory);
    }

    /**
     * Delete an existing subcategory.
     *
     * @param id the id of the subcategory to delete.
     * @return {@code true} if deletion was successful, {@code false} otherwise.
     * @throws EntityNotFoundException if the subcategory does not exist.
     * @throws EntityDeletionException if an error occurs while deleting the subcategory.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(long id) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SUB_CATEGORY_NAME, "id", id));

        try {
            subCategoryRepository.delete(subCategory);
        } catch (Exception e) {
            throw new EntityDeletionException(SUB_CATEGORY_NAME, "id", id);
        }
        return true;
    }

    /**
     * Update an existing subcategory.
     *
     * @param subCategoryRequestDTO the subcategory data to update.
     * @return the response containing the updated subcategory details.
     * @throws EntityNotFoundException if the subcategory with the provided ID does not exist.
     * @throws EntityEditException if an error occurs while updating the subcategory.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubCategoryResponseDTO update(SubCategoryRequestDTO subCategoryRequestDTO) {
        SubCategory subCategory = subCategoryRepository.findById(subCategoryRequestDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(SUB_CATEGORY_NAME, "id", subCategoryRequestDTO.getId()));

        subCategory.setName(subCategoryRequestDTO.getName());

        try {
            subCategoryRepository.save(subCategory);
        } catch (Exception e) {
            throw new EntityEditException(SUB_CATEGORY_NAME, "nome", subCategory.getId());
        }

        return subCategoryMapper.subCategoryToResponseDTO(subCategory);
    }

    /**
     * Returns a subcategory by ID.
     *
     * @param id the ID of the subcategory to search for.
     * @return the response containing the details of the subcategory found.
     * @throws EntityNotFoundException if the subcategory with the provided ID does not exist.
     */
    @Override
    @Transactional(readOnly = true)
    public SubCategoryResponseDTO findById(long id) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(SUB_CATEGORY_NAME, "id", id));

        return subCategoryMapper.subCategoryToResponseDTO(subCategory);
    }

    /**
     * Returns all subcategories.
     *
     * @return the list of all subcategories.
     */
    @Override
    @Transactional(readOnly = true)
    public List<SubCategoryResponseDTO> findAll() {
        List<SubCategory> subCategories = subCategoryRepository.findAll();

        return subCategoryMapper.subCategoriesToSubCategoryDTOs(subCategories);
    }

    /**
     * Returns all products in a subcategory.
     *
     * @param id the ID of the subcategory to search for products.
     * @return the list of all products in the subcategory.
     * @throws EntityNotFoundException if the subcategory with the provided ID does not exist.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductDetailsDTO> findProductsByCategory(long id) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SUB_CATEGORY_NAME, "id", id));

        List<Product> product = productRepository.findBySubCategory(subCategory);

        return productMapper.productsToResponseDTOs(product);
    }
}
