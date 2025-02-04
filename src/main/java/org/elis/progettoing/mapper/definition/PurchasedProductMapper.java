package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.request.product.PurchasedProductRequestDTO;
import org.elis.progettoing.dto.response.cart.PurchasedProductResponseDTO;
import org.elis.progettoing.models.product.PurchasedProduct;

import java.util.List;

/**
 * Interface for mapping between PurchasedProduct entities and their respective DTOs.
 * This interface defines methods for converting a PurchasedProduct entity to different types of DTOs
 * and converting a PurchasedProductRequestDTO to a PurchasedProduct entity.
 */
public interface PurchasedProductMapper {

    /**
     * Converts a PurchasedProductRequestDTO to a PurchasedProduct entity.
     *
     * @param requestDTO the PurchasedProductRequestDTO to be converted
     * @return the PurchasedProduct entity populated with data from the PurchasedProductRequestDTO
     */
    PurchasedProduct requestDTOToPurchasedProduct(PurchasedProductRequestDTO requestDTO);

    /**
     * Converts a list of PurchasedProduct entities to a list of PurchasedProductResponseDTOs.
     *
     * @param purchasedProducts the list of PurchasedProduct entities to be converted
     * @return a list of PurchasedProductResponseDTOs populated with data from the PurchasedProduct entities
     */
    List<PurchasedProductResponseDTO> purchasedProductsToPurchasedProductDTOs(List<PurchasedProduct> purchasedProducts);

    /**
     * Converts a list of PurchasedProductRequestDTOs to a list of PurchasedProduct entities.
     *
     * @param purchasedProductRequestDTOS the list of PurchasedProductRequestDTOs to be converted
     * @return a list of PurchasedProduct entities populated with data from the PurchasedProductRequestDTOs
     */
    List<PurchasedProduct> purchasedProductDTOsToPurchasedProducts(List<PurchasedProductRequestDTO> purchasedProductRequestDTOS);

    /**
     * Converts a PurchasedProduct entity to a PurchasedProductResponseDTO.
     *
     * @param purchasedProduct the PurchasedProduct entity to be converted
     * @return the PurchasedProductResponseDTO populated with data from the PurchasedProduct entity
     */
    PurchasedProductResponseDTO purchasedProductToResponseDTO(PurchasedProduct purchasedProduct);
}
