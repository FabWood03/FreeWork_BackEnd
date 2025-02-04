package org.elis.progettoing.service.implementation;

import org.elis.progettoing.dto.response.cart.PurchasedProductResponseDTO;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.PurchasedProductMapper;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.elis.progettoing.repository.PurchasedProductRepository;
import org.elis.progettoing.service.definition.PurchasedProductService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the {@link PurchasedProductService} interface.
 */
@Service
public class PurchasedProductServiceImpl implements PurchasedProductService {
    private final PurchasedProductRepository purchasedProductRepository;
    private final PurchasedProductMapper purchasedProductMapper;

    /**
     * Constructor for PurchasedProductServiceImpl.
     *
     * @param purchasedProductRepository the repository to access purchased products.
     * @param purchasedProductMapper the mapper for converting between entities and DTOs.
     */
    public PurchasedProductServiceImpl(PurchasedProductRepository purchasedProductRepository, PurchasedProductMapper purchasedProductMapper) {
        this.purchasedProductRepository = purchasedProductRepository;
        this.purchasedProductMapper = purchasedProductMapper;
    }

    /**
     * Method that allows you to find a purchased product via its id
     *
     * @param id id of the purchased product
     * @return the purchased product found
     * @throws EntityNotFoundException if no purchased product was found
     */
    @Override
    @Transactional(readOnly = true)
    public PurchasedProductResponseDTO findById(Long id) {
        PurchasedProduct purchasedProduct = purchasedProductRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("carrello", "ID", id));

        return purchasedProductMapper.purchasedProductToResponseDTO(purchasedProduct);
    }

    /**
     * Method that allows you to find a purchased product via its id
     *
     * @return the purchased product found
     * @throws EntityNotFoundException if no purchased product was found
     */
    @Override
    @Transactional(readOnly = true)
    public List<PurchasedProductResponseDTO> findByCartId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<PurchasedProduct> purchasedProducts = purchasedProductRepository.findAllByCartId(user.getCart().getId());

        if (purchasedProducts.isEmpty()) {
            throw new EntityNotFoundException("prodotto", "ID carrello", user.getCart().getId());
        }

        return purchasedProductMapper.purchasedProductsToPurchasedProductDTOs(purchasedProducts);
    }

    /**
     * Method that allows you to find a purchased product via its id
     *
     * @return the purchased product found
     */
    @Override
    @Transactional(readOnly = true)
    public List<PurchasedProductResponseDTO> findAllPurchasedProducts() {
        List<PurchasedProduct> allPurchasedProducts = purchasedProductRepository.findAll();

        return purchasedProductMapper.purchasedProductsToPurchasedProductDTOs(allPurchasedProducts);
    }
}
