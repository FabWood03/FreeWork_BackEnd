package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.product.PurchasedProductRequestDTO;
import org.elis.progettoing.dto.response.cart.PurchasedProductResponseDTO;
import org.elis.progettoing.mapper.definition.PurchasedProductMapper;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.ProductPackage;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the PurchasedProductMapper interface. Provides methods for mapping between
 * PurchasedProduct-related request and response DTOs and entity models.
 */
@Component
public class PurchasedProductMapperImpl implements PurchasedProductMapper {

    /**
     * Converts a PurchasedProductRequestDTO to a PurchasedProduct entity.
     *
     * @param requestDTO the PurchasedProductRequestDTO to be converted
     * @return a PurchasedProduct entity populated with data from the PurchasedProductRequestDTO, or null if the requestDTO is null
     */
    @Override
    public PurchasedProduct requestDTOToPurchasedProduct(PurchasedProductRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        PurchasedProduct purchasedProduct = new PurchasedProduct();
        purchasedProduct.setProduct(purchasedProductRequestDTOToProduct(requestDTO));
        purchasedProduct.setSelectedPackage(purchasedProductRequestDTOToProductPackage(requestDTO));

        return purchasedProduct;
    }

    /**
     * Converts a list of PurchasedProduct entities to a list of PurchasedProductResponseDTOs.
     *
     * @param purchasedProducts the list of PurchasedProduct entities to be converted
     * @return a list of PurchasedProductResponseDTOs populated with data from the PurchasedProduct entities
     */
    @Override
    public List<PurchasedProductResponseDTO> purchasedProductsToPurchasedProductDTOs(List<PurchasedProduct> purchasedProducts) {
        if (purchasedProducts == null) {
            return Collections.emptyList();
        }

        List<PurchasedProductResponseDTO> list = new ArrayList<>(purchasedProducts.size());
        for (PurchasedProduct purchasedProduct : purchasedProducts) {
            list.add(purchasedProductToResponseDTO(purchasedProduct));
        }

        return list;
    }

    /**
     * Converts a list of PurchasedProductRequestDTOs to a list of PurchasedProduct entities.
     *
     * @param purchasedProductRequestDTOS the list of PurchasedProductRequestDTOs to be converted
     * @return a list of PurchasedProduct entities populated with data from the PurchasedProductRequestDTOs
     */
    @Override
    public List<PurchasedProduct> purchasedProductDTOsToPurchasedProducts(List<PurchasedProductRequestDTO> purchasedProductRequestDTOS) {
        if (purchasedProductRequestDTOS == null) {
            return Collections.emptyList();
        }

        List<PurchasedProduct> list = new ArrayList<>(purchasedProductRequestDTOS.size());
        for (PurchasedProductRequestDTO purchasedProductRequestDTO : purchasedProductRequestDTOS) {
            list.add(requestDTOToPurchasedProduct(purchasedProductRequestDTO));
        }

        return list;
    }

    /**
     * Converts a PurchasedProduct entity to a PurchasedProductResponseDTO.
     *
     * @param purchasedProduct the PurchasedProduct entity to be converted
     * @return a PurchasedProductResponseDTO populated with data from the PurchasedProduct entity, or null if the purchasedProduct is null
     */
    @Override
    public PurchasedProductResponseDTO purchasedProductToResponseDTO(PurchasedProduct purchasedProduct) {
        if (purchasedProduct == null) {
            return null;
        }

        PurchasedProductResponseDTO purchasedProductResponseDTO = new PurchasedProductResponseDTO();
        purchasedProductResponseDTO.setId(purchasedProduct.getId());
        purchasedProductResponseDTO.setProductTitle(purchasedProduct.getProduct().getTitle());
        purchasedProductResponseDTO.setProductImagePhoto(purchasedProduct.getProduct().getUrlProductPhotos().getFirst());
        purchasedProductResponseDTO.setType(String.valueOf(purchasedProduct.getSelectedPackage().getType()));
        purchasedProductResponseDTO.setPrice((long) purchasedProduct.getSelectedPackage().getPrice());
        purchasedProductResponseDTO.setUserName(purchasedProduct.getBuyer().getName());
        purchasedProductResponseDTO.setUserSurname(purchasedProduct.getBuyer().getSurname());

        return purchasedProductResponseDTO;
    }

    /**
     * Converts a PurchasedProductRequestDTO to a Product entity.
     *
     * @param purchasedProductRequestDTO the PurchasedProductRequestDTO to be converted
     * @return a Product entity populated with data from the PurchasedProductRequestDTO
     */
    public Product purchasedProductRequestDTOToProduct(PurchasedProductRequestDTO purchasedProductRequestDTO) {
        if (purchasedProductRequestDTO == null) {
            return null;
        }

        Product product = new Product();
        product.setId(purchasedProductRequestDTO.getProductId());

        return product;
    }

    /**
     * Converts a PurchasedProductRequestDTO to a ProductPackage entity.
     *
     * @param purchasedProductRequestDTO the PurchasedProductRequestDTO to be converted
     * @return a ProductPackage entity populated with data from the PurchasedProductRequestDTO
     */
    public ProductPackage purchasedProductRequestDTOToProductPackage(PurchasedProductRequestDTO purchasedProductRequestDTO) {
        if (purchasedProductRequestDTO == null) {
            return null;
        }

        ProductPackage productPackage = new ProductPackage();
        productPackage.setId(purchasedProductRequestDTO.getPackageId());

        return productPackage;
    }
}
