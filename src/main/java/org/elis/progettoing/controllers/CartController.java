package org.elis.progettoing.controllers;

import jakarta.validation.Valid;
import org.elis.progettoing.dto.request.product.PurchasedProductRequestDTO;
import org.elis.progettoing.dto.response.cart.CartResponseDTO;
import org.elis.progettoing.dto.response.cart.PurchasedProductResponseDTO;
import org.elis.progettoing.exception.entity.EntityAlreadyExistsException;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.service.definition.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing shopping cart operations.
 * Provides endpoints to retrieve cart details, add purchased products, and remove products.
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:4200")
public class CartController {
    private final CartService cartService;

    /**
     * Constructs an instance of {@code CartController}.
     *
     * @param cartService the service managing cart-related business logic.
     */
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Endpoint to retrieve a cart by its ID.
     *
     * @param id the ID of the cart to retrieve.
     * @return a {@link ResponseEntity} containing the {@link CartResponseDTO} and HTTP status 200 (OK).
     * @throws EntityNotFoundException if the cart with the specified ID is not found.
     */
    @GetMapping("/findById")
    public ResponseEntity<CartResponseDTO> findById(@RequestParam("id") long id) throws EntityNotFoundException {
        return new ResponseEntity<>(cartService.findById(id), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve the current user's cart.
     *
     * @return a {@link ResponseEntity} containing the {@link CartResponseDTO} and HTTP status 200 (OK).
     * @throws EntityNotFoundException if the cart for the current user is not found.
     */
    @GetMapping("/findByUserId")
    public ResponseEntity<CartResponseDTO> findByUser() throws EntityNotFoundException {
        return new ResponseEntity<>(cartService.findByUserId(), HttpStatus.OK);
    }

    /**
     * Endpoint to add a purchased product to the cart.
     *
     * @param purchasedProductRequest the request data containing product details to add.
     * @return a {@link ResponseEntity} containing the {@link PurchasedProductResponseDTO} and HTTP status 201 (Created).
     * @throws EntityCreationException      if an error occurs during product creation.
     * @throws EntityAlreadyExistsException if the product already exists in the cart.
     */
    @PostMapping("/addPurchasedProduct")
    public ResponseEntity<PurchasedProductResponseDTO> addPurchasedProduct(@Valid @RequestBody PurchasedProductRequestDTO purchasedProductRequest)
            throws EntityCreationException, EntityAlreadyExistsException {
        return new ResponseEntity<>(cartService.addPurchasedProduct(purchasedProductRequest), HttpStatus.CREATED);
    }

    /**
     * Endpoint to remove a purchased product from the cart.
     *
     * @param productId the ID of the product to remove.
     * @return a {@link ResponseEntity} containing a boolean indicating the operation's success and HTTP status 200 (OK).
     * @throws EntityNotFoundException   if the product with the specified ID is not found in the cart.
     * @throws EntityDeletionException   if an error occurs during product removal.
     */
    @DeleteMapping("/removePurchasedProduct")
    public ResponseEntity<Boolean> removePurchasedProduct(@RequestParam("id") long productId)
            throws EntityNotFoundException, EntityDeletionException {
        return new ResponseEntity<>(cartService.removePurchasedProduct(productId), HttpStatus.OK);
    }
}
