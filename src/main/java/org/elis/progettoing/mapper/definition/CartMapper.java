package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.response.cart.CartResponseDTO;
import org.elis.progettoing.models.Cart;

/**
 * Interface for mapping between Cart entity and CartResponseDTO.
 * This interface defines the method for converting a Cart entity to a CartResponseDTO.
 */
public interface CartMapper {

    /**
     * Converts a Cart entity to a CartResponseDTO.
     *
     * @param cart the Cart entity to be converted
     * @return the CartResponseDTO populated with data from the Cart entity
     */
    CartResponseDTO cartToCartResponseDTO(Cart cart);
}
