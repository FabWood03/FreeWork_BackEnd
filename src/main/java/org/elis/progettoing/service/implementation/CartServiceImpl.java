package org.elis.progettoing.service.implementation;

import org.elis.progettoing.dto.request.product.PurchasedProductRequestDTO;
import org.elis.progettoing.dto.response.cart.CartResponseDTO;
import org.elis.progettoing.dto.response.cart.PurchasedProductResponseDTO;
import org.elis.progettoing.exception.PurchasedProductException;
import org.elis.progettoing.exception.entity.EntityAlreadyExistsException;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.CartMapper;
import org.elis.progettoing.mapper.definition.PurchasedProductMapper;
import org.elis.progettoing.models.Cart;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.ProductPackage;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.elis.progettoing.repository.CartRepository;
import org.elis.progettoing.repository.ProductPackageRepository;
import org.elis.progettoing.repository.ProductRepository;
import org.elis.progettoing.repository.PurchasedProductRepository;
import org.elis.progettoing.service.definition.CartService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Implementation of {@link CartService} for handling cart operations.
 */
@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductPackageRepository productPackageRepository;
    private final CartMapper cartMapper;
    private final PurchasedProductMapper purchasedProductMapper;


    private static final String CART = "carrello";
    private final PurchasedProductRepository purchasedProductRepository;

    /**
     * CartServiceImpl constructor
     *
     * @param cartRepository             Repository of carts.
     * @param productRepository          Repository of products.
     * @param productPackageRepository   Repository of product packages.
     * @param cartMapper                 Mapper for conversion between Cart and DTO objects.
     * @param purchasedProductMapper     Mapper for conversion between PurchasedProduct objects and DTOs
     * @param purchasedProductRepository Repository of purchased products.
     */
    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, ProductPackageRepository productPackageRepository, CartMapper cartMapper, PurchasedProductMapper purchasedProductMapper, PurchasedProductRepository purchasedProductRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.productPackageRepository = productPackageRepository;
        this.cartMapper = cartMapper;
        this.purchasedProductMapper = purchasedProductMapper;
        this.purchasedProductRepository = purchasedProductRepository;
    }

    /**
     * Method that allows a cart to be found by id.
     *
     * @param id id of the cart to be found.
     * @return the cart associated with the id.
     * @throws EntityNotFoundException if the cart is not present.
     */
    @Transactional(readOnly = true)
    @Override
    public CartResponseDTO findById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CART, "ID", id));

        return cartMapper.cartToCartResponseDTO(cart);
    }

    /**
     * Method that allows a cart to be found by user id.
     *
     * @return the cart associated with the user id.
     * @throws EntityNotFoundException if the cart is not present.
     */
    @Transactional(readOnly = true)
    @Override
    public CartResponseDTO findByUserId() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(CART, "ID", user.getId()));

        return cartMapper.cartToCartResponseDTO(cart);
    }

    /**
     * Adds a purchased product to the user's cart.
     * This operation includes the following steps:
     *
     * <ul>
     * <li>Retrieve the authenticated user from the security context.</li>
     * <li>Gets product information from the repository.</li>
     * <li>Gets information about the selected package for the product.</li>
     * <li>Retrieve the user's cart.</li>
     * <li>Checks whether the product with the selected package is already present in the cart.</li>
     * <li>Creates a new `PurchasedProduct` object.</li>
     * <li>Save the new purchased product in the database.</li>
     * <li>Returns a DTO representing the added purchased product.</li>
     * </ul>
     *
     * @param purchasedProductRequest The DTO of the request containing the product and package information.
     * @return A `PurchasedProductResponseDTO` representing the added purchased product.
     * @throws EntityNotFoundException if the selected product or package is not found.
     * @throws EntityAlreadyExistsException if the same product with the same package is already in the cart.
     * @throws EntityCreationException if saving the purchased product fails.
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PurchasedProductResponseDTO addPurchasedProduct(PurchasedProductRequestDTO purchasedProductRequest) {
        // Ottieni l'utente autenticato
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User buyer = (User) authentication.getPrincipal();

        // Recupera il prodotto dal repository
        Product product = productRepository.findById(purchasedProductRequest.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("prodotto", "ID", purchasedProductRequest.getProductId()));

        // Recupera il pacchetto selezionato per il prodotto
        Optional<ProductPackage> selectedPackage = productPackageRepository.findByProductIdAndId(product.getId(), purchasedProductRequest.getPackageId());

        if (selectedPackage.isEmpty()) {
            throw new EntityNotFoundException("pacchetto", "ID prodotto", purchasedProductRequest.getProductId());
        }

        if (product.getUser().getId() == buyer.getId()) {
            throw new PurchasedProductException("Non puoi acquistare un prodotto che hai messo in vendita");
        }

        // Recupera il carrello dell'utente
        Cart cart = buyer.getCart();

        // Verifica se il prodotto con il pacchetto selezionato è già presente nel carrello
        if (cart.getPurchasedProducts().stream()
                .anyMatch(p -> p.getProduct().getId() == product.getId() && p.getSelectedPackage().getId() == selectedPackage.get().getId())) {
            throw new EntityAlreadyExistsException(CART, "ID prodotto", product.getId());
        }

        // Crea un nuovo oggetto PurchasedProduct
        PurchasedProduct purchasedProduct = purchasedProductMapper.requestDTOToPurchasedProduct(purchasedProductRequest);
        purchasedProduct.setBuyer(buyer);
        purchasedProduct.setProduct(product);
        purchasedProduct.setSelectedPackage(selectedPackage.get());
        purchasedProduct.setCart(cart);
        purchasedProduct.setPurchaseDate(LocalDate.now());

        try {
            purchasedProductRepository.save(purchasedProduct);
        } catch (Exception e) {
            throw new EntityCreationException("prodotto acquistato", "ID prodotto", product.getId());
        }

        return purchasedProductMapper.purchasedProductToResponseDTO(purchasedProduct);
    }

    /**
     * Removes a purchased product from the user's cart.
     * This operation includes the following steps:
     *
     * <ul>
     * <li>Retrieve the authenticated user from the security context.</li>
     * <li>Gets the user's cart.</li>
     * <li>Finds the purchased product with the specified ID in the user's cart.</li>
     * <li>Removes the found purchased product from the user's cart.</li>
     * <li>Save the updated cart in the database.</li>
     * <li>Returns `true` if the removal was successful, `false` otherwise.</li>
     * </ul>
     *
     * @param purchasedProductId The ID of the purchased product to remove.
     * @return `true` if the removal was successful, `false` otherwise.
     * @throws EntityNotFoundException if the purchased product is not found in the user's cart.
     * @throws EntityDeletionException if saving updated cart fails.
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removePurchasedProduct(long purchasedProductId) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User buyer = ((User) authentication.getPrincipal());

        Cart cart = buyer.getCart();

        PurchasedProduct purchasedProduct = cart.getPurchasedProducts().stream()
                .filter(pp -> pp.getId() == purchasedProductId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("prodotto acquistato", "ID", purchasedProductId));

        cart.getPurchasedProducts().remove(purchasedProduct);

        try {
            cartRepository.save(cart);
            return true;
        } catch (Exception e) {
            throw new EntityDeletionException("utente", "ID", buyer.getId());
        }
    }
}
