package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.response.cart.PurchasedProductResponseDTO;

import java.util.List;

/**
 * Interface for the PurchasedProductService class. Provides methods for retrieving purchased products by ID, cart ID,
 * and all purchased products.
 */
public interface PurchasedProductService {
    PurchasedProductResponseDTO findById(Long id);

    List<PurchasedProductResponseDTO> findByCartId();

    List<PurchasedProductResponseDTO> findAllPurchasedProducts();
}
