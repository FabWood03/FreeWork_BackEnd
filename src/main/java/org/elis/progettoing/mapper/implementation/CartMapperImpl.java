package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.response.cart.CartResponseDTO;
import org.elis.progettoing.mapper.definition.CartMapper;
import org.elis.progettoing.models.Cart;
import org.springframework.stereotype.Component;

/**
 * This class implements the CartMapper interface and provides mapping functionality
 * between entities and their corresponding DTOs (Data Transfer Objects) for Cart,
 * Product, PurchasedProduct, PackageAttribute, ProductPackage, and User.
 */
@Component
public class CartMapperImpl implements CartMapper {

    private final PurchasedProductMapperImpl purchasedProductMapperImpl;

    /**
     * Constructs a new CartMapperImpl with the specified PurchasedProductMapper.
     *
     * @param purchasedProductMapperImpl the PurchasedProductMapper to be used for mapping
     */
    public CartMapperImpl(PurchasedProductMapperImpl purchasedProductMapperImpl) {
        this.purchasedProductMapperImpl = purchasedProductMapperImpl;
    }

    /**
     * Converts a Cart entity to a CartResponseDTO.
     *
     * @param cart the Cart entity to be converted
     * @return the CartResponseDTO populated with the Cart entity data, or null if the Cart is null
     */
    @Override
    public CartResponseDTO cartToCartResponseDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartResponseDTO cartResponseDTO = new CartResponseDTO();
        cartResponseDTO.setPurchasedProducts(purchasedProductMapperImpl.purchasedProductsToPurchasedProductDTOs(cart.getPurchasedProducts()));
        cartResponseDTO.setId(cart.getId());

        return cartResponseDTO;
    }
}
