package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.request.product.PurchasedProductRequestDTO;
import org.elis.progettoing.dto.response.cart.CartResponseDTO;
import org.elis.progettoing.dto.response.cart.PurchasedProductResponseDTO;

/**
 * Interface for the CartService class. Provides methods for retrieving and updating the cart.
 */
public interface CartService {
    CartResponseDTO findById(Long id);

    CartResponseDTO findByUserId();

    PurchasedProductResponseDTO addPurchasedProduct(PurchasedProductRequestDTO purchasedProductRequest);

    boolean removePurchasedProduct(long productId);
}
