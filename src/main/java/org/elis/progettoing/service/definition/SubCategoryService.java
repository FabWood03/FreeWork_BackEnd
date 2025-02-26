package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.request.category.SubCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.SubCategoryResponseDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;

import java.util.List;

/**
 * Interface for the SubCategoryService class. Provides methods for creating, deleting, and updating subcategories,
 * as well as retrieving subcategories by ID, and all subcategories.
 */
public interface SubCategoryService {

    SubCategoryResponseDTO create(SubCategoryRequestDTO subCategoryRequestDTO);

    boolean delete(long id);

    SubCategoryResponseDTO update(SubCategoryRequestDTO subCategoryRequestDTO);

    SubCategoryResponseDTO findById(long id);

    List<SubCategoryResponseDTO> findAll();

    List<ProductDetailsDTO> findProductsByCategory(long id);
}
