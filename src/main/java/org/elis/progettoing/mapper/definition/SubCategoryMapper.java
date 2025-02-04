package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.request.category.SubCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.SubCategoryResponseDTO;
import org.elis.progettoing.models.category.SubCategory;

import java.util.List;

/**
 * Interface for mapping between SubCategory entities and their respective DTOs.
 * This interface defines methods for converting a SubCategoryRequestDTO to a SubCategory entity,
 * a SubCategory entity to a SubCategoryResponseDTO, and lists of SubCategories to lists of SubCategoryResponseDTOs.
 */
public interface SubCategoryMapper {

    /**
     * Converts a SubCategoryRequestDTO to a SubCategory entity.
     *
     * @param requestDTO the SubCategoryRequestDTO to be converted
     * @return the SubCategory entity populated with data from the SubCategoryRequestDTO
     */
    SubCategory requestDTOToSubCategory(SubCategoryRequestDTO requestDTO);

    /**
     * Converts a list of SubCategory entities to a list of SubCategoryResponseDTOs.
     *
     * @param categories the list of SubCategory entities to be converted
     * @return the list of SubCategoryResponseDTOs populated with data from the SubCategory entities
     */
    List<SubCategoryResponseDTO> subCategoriesToSubCategoryDTOs(List<SubCategory> categories);

    /**
     * Converts a SubCategory entity to a SubCategoryResponseDTO.
     *
     * @param subCategory the SubCategory entity to be converted
     * @return the SubCategoryResponseDTO populated with data from the SubCategory entity
     */
    SubCategoryResponseDTO subCategoryToResponseDTO(SubCategory subCategory);
}
