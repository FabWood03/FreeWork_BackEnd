package org.elis.progettoing.controllers;

import jakarta.validation.Valid;
import org.elis.progettoing.dto.request.product.ProductRequestDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;
import org.elis.progettoing.dto.response.product.ProductSummaryDTO;
import org.elis.progettoing.dto.response.product.TagResponseDTO;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.service.definition.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for managing products.
 * Provides endpoints for product creation, retrieval, and deletion, along with analysis and tag management.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Constructs an instance of {@code ProductController}.
     *
     * @param productService the service managing product-related business logic.
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Endpoint to create a new product.
     *
     * @param productRequestDTO the request data containing product details.
     * @param images            the list of images to associate with the product.
     * @return a {@link ResponseEntity} containing the created {@link ProductDetailsDTO} and HTTP status 200 (OK).
     * @throws EntityCreationException if there is an error during product creation.
     */
    @PostMapping("/createProduct")
    public ResponseEntity<ProductDetailsDTO> createProduct(
            @Valid @RequestPart("productRequestDTO") ProductRequestDTO productRequestDTO,
            @RequestPart("images") List<MultipartFile> images) throws EntityCreationException {
        return new ResponseEntity<>(productService.createProduct(productRequestDTO, images), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve a summary of all products.
     *
     * @return a {@link ResponseEntity} containing a list of {@link ProductSummaryDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/summary")
    public ResponseEntity<List<ProductSummaryDTO>> summary() {
        return new ResponseEntity<>(productService.getProductSummary(), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve detailed information for a specific product.
     *
     * @param id the ID of the product to retrieve.
     * @return a {@link ResponseEntity} containing the {@link ProductDetailsDTO} and HTTP status 200 (OK).
     * @throws EntityNotFoundException if the product is not found.
     */
    @GetMapping("/details")
    public ResponseEntity<ProductDetailsDTO> details(@RequestParam("productId") long id) throws EntityNotFoundException {
        return new ResponseEntity<>(productService.findWithDetails(id), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve all products summary associated with a specific user.
     *
     * @param id the ID of the user.
     * @return a {@link ResponseEntity} containing a list of {@link ProductDetailsDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/summaryByUserId")
    public ResponseEntity<List<ProductSummaryDTO>> findAllByUserId(@RequestParam("userId") long id) {
        return new ResponseEntity<>(productService.findAllSummaryByUserId(id), HttpStatus.OK);
    }

    /**
     * Endpoint to remove a product by its ID.
     *
     * @param id the ID of the product to remove.
     * @return a {@link ResponseEntity} containing a boolean indicating success and HTTP status 200 (OK).
     * @throws EntityNotFoundException    if the product is not found.
     * @throws EntityDeletionException if there is an error during product deletion.
     */
    @DeleteMapping("/removeProduct")
    public ResponseEntity<Boolean> removeProduct(@RequestParam long id) throws EntityNotFoundException, EntityDeletionException {
        return new ResponseEntity<>(productService.removeProduct(id), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve tags for products.
     *
     * @param nameFilter optional filter for tag names.
     * @return a {@link ResponseEntity} containing a list of {@link TagResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getTags")
    public ResponseEntity<List<TagResponseDTO>> getTags(@RequestParam(required = false, name = "nameFilter") String nameFilter) {
        return new ResponseEntity<>(productService.getTags(nameFilter), HttpStatus.OK);
    }
}
