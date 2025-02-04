package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.response.cart.PurchasedProductResponseDTO;

import java.util.List;

public interface PurchasedProductService {
    PurchasedProductResponseDTO findById(Long id);

    List<PurchasedProductResponseDTO> findByCartId();

    List<PurchasedProductResponseDTO> findAllPurchasedProducts();
}
