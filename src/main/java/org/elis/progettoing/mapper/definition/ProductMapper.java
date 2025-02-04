package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.request.product.ProductRequestDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;
import org.elis.progettoing.dto.response.product.ProductSummaryDTO;
import org.elis.progettoing.models.product.Product;

import java.util.List;

/**
 * Interface for mapping between Product entities and their respective DTOs.
 * This interface defines methods for converting a Product entity to different types of DTOs
 * (including product details and summaries) and converting a ProductRequestDTO to a Product entity.
 */
public interface ProductMapper {

    /**
     * Converts a ProductRequestDTO to a Product entity.
     *
     * @param requestDTO the ProductRequestDTO to be converted
     * @return the Product entity populated with data from the ProductRequestDTO
     */
    Product productRequestDTOToProduct(ProductRequestDTO requestDTO);

    /**
     * Converts a Product entity to a ProductDetailsDTO.
     *
     * @param product the Product entity to be converted
     * @return the ProductDetailsDTO populated with data from the Product entity
     */
    ProductDetailsDTO productToResponseDTO(Product product);

    /**
     * Converts a list of Product entities to a list of ProductDetailsDTOs.
     *
     * @param products the list of Product entities to be converted
     * @return a list of ProductDetailsDTOs populated with data from the Product entities
     */
    List<ProductDetailsDTO> productsToResponseDTOs(List<Product> products);

    /**
     * Converts a list of Product entities to a list of ProductSummaryDTOs.
     *
     * @param products the list of Product entities to be converted
     * @return a list of ProductSummaryDTOs populated with data from the Product entities
     */
    List<ProductSummaryDTO> productsToSummaryResponseDTOs(List<Product> products);

    /**
     * Converts a Product entity to a ProductSummaryDTO.
     *
     * @param product the Product entity to be converted
     * @return the ProductSummaryDTO populated with data from the Product entity
     */
    ProductSummaryDTO productToSummaryDTO(Product product);
}
