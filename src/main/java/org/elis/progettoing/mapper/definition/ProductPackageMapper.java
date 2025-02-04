package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.request.product.ProductPackageRequestDTO;
import org.elis.progettoing.models.product.ProductPackage;

/**
 * Interface for mapping between ProductPackage entities and their respective DTOs.
 * This interface defines a method for converting a ProductPackageRequestDTO to a ProductPackage entity.
 */
public interface ProductPackageMapper {

    /**
     * Converts a ProductPackageRequestDTO to a ProductPackage entity.
     *
     * @param packageRequest the ProductPackageRequestDTO to be converted
     * @return the ProductPackage entity populated with data from the ProductPackageRequestDTO
     */
    ProductPackage packageRequestDTOToProductPackage(ProductPackageRequestDTO packageRequest);
}
