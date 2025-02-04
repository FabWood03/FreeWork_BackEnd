package org.elis.progettoing.dto.response.product;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.elis.progettoing.dto.response.category.SubCategoryResponseDTO;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing detailed information about a product.
 * This class extends {@link ProductSummaryDTO} to include additional details such as
 * subcategory, packages, and tags associated with the product.
 *
 * <p>The {@link ProductDetailsDTO} class is used to return comprehensive product details
 * including its subcategory, available packages, and associated tags.</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductDetailsDTO extends ProductSummaryDTO {

    private SubCategoryResponseDTO subCategory;

    private List<ProductPackageResponseDTO> packages;

    private List<TagResponseDTO> tags;

    public ProductDetailsDTO(SubCategoryResponseDTO subCategoryResponseDTO, List<ProductPackageResponseDTO> productPackageResponseDTOS, List<TagResponseDTO> tagResponseDTOS) {
        this.subCategory = subCategoryResponseDTO;
        this.packages = productPackageResponseDTOS;
        this.tags = tagResponseDTOS;
    }

    public ProductDetailsDTO() {

    }
}
