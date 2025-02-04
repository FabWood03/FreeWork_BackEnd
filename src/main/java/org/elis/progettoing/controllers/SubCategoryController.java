package org.elis.progettoing.controllers;

import jakarta.validation.Valid;
import org.elis.progettoing.dto.request.category.SubCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.SubCategoryResponseDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;
import org.elis.progettoing.service.definition.SubCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing subcategories.
 * Provides endpoints for creating, updating, deleting, and retrieving subcategories,
 * as well as fetching products associated with a specific subcategory.
 */
@RestController
@RequestMapping("/api/subCategory")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    /**
     * Constructs an instance of {@code SubCategoryController}.
     *
     * @param subCategoryService the service managing subcategory-related business logic.
     */
    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    /**
     * Endpoint to create a new subcategory.
     *
     * @param subCategoryRequestDTO the data for the new subcategory.
     * @return a {@link ResponseEntity} containing the created {@link SubCategoryResponseDTO}
     *         and HTTP status 201 (Created).
     */
    @PostMapping("/create")
    public ResponseEntity<SubCategoryResponseDTO> create(@Valid @RequestBody SubCategoryRequestDTO subCategoryRequestDTO) {
        return new ResponseEntity<>(subCategoryService.create(subCategoryRequestDTO), HttpStatus.CREATED);
    }

    /**
     * Endpoint to delete an existing subcategory by its ID.
     *
     * @param id the ID of the subcategory to delete.
     * @return a {@link ResponseEntity} containing a boolean indicating the success of the deletion
     *         and HTTP status 201 (Created).
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> delete(@RequestParam long id) {
        return new ResponseEntity<>(subCategoryService.delete(id), HttpStatus.CREATED);
    }

    /**
     * Endpoint to update an existing subcategory.
     *
     * @param subCategoryRequestDTO the updated data for the subcategory.
     * @return a {@link ResponseEntity} containing the updated {@link SubCategoryResponseDTO}
     *         and HTTP status 201 (Created).
     */
    @PatchMapping("/update")
    public ResponseEntity<SubCategoryResponseDTO> update(@Valid @RequestBody SubCategoryRequestDTO subCategoryRequestDTO) {
        return new ResponseEntity<>(subCategoryService.update(subCategoryRequestDTO), HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve all subcategories.
     *
     * @return a {@link ResponseEntity} containing a list of {@link SubCategoryResponseDTO}
     *         and HTTP status 200 (OK).
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<SubCategoryResponseDTO>> findAll() {
        return new ResponseEntity<>(subCategoryService.findAll(), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve a specific subcategory by its ID.
     *
     * @param id the ID of the subcategory to retrieve.
     * @return a {@link ResponseEntity} containing the {@link SubCategoryResponseDTO}
     *         for the specified ID and HTTP status 200 (OK).
     */
    @GetMapping("/getById")
    public ResponseEntity<SubCategoryResponseDTO> findByName(@RequestParam long id) {
        return new ResponseEntity<>(subCategoryService.findById(id), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve products associated with a specific subcategory.
     *
     * @param id the ID of the subcategory.
     * @return a {@link ResponseEntity} containing a list of {@link ProductDetailsDTO}
     *         for the products in the specified subcategory and HTTP status 200 (OK).
     */
    @GetMapping("/findProductsBySubCategoryId")
    public ResponseEntity<List<ProductDetailsDTO>> findProductsBySubCategoryId(@RequestParam long id) {
        return new ResponseEntity<>(subCategoryService.findProductsByCategory(id), HttpStatus.OK);
    }
}
