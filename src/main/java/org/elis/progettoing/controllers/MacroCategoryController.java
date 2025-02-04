package org.elis.progettoing.controllers;

import jakarta.validation.Valid;
import org.elis.progettoing.dto.request.category.MacroCategoryRequestDTO;
import org.elis.progettoing.dto.response.category.MacroCategoryResponseDTO;
import org.elis.progettoing.service.definition.MacroCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing macro-categories.
 * Provides endpoints to create, update, delete, and retrieve macro-category data.
 */
@RestController
@RequestMapping("/api/macroCategory")
public class MacroCategoryController {
    private final MacroCategoryService macroCategoryService;

    /**
     * Constructs an instance of {@code MacroCategoryController}.
     *
     * @param macroCategoryService the service managing macro-category-related business logic.
     */
    public MacroCategoryController(MacroCategoryService macroCategoryService) {
        this.macroCategoryService = macroCategoryService;
    }

    /**
     * Endpoint to create a new macro-category.
     *
     * @param macroCategoryRequestDTO the request data containing macro-category details to create.
     * @return a {@link ResponseEntity} containing the created {@link MacroCategoryResponseDTO} and HTTP status 201 (Created).
     */
    @PostMapping("/create")
    public ResponseEntity<MacroCategoryResponseDTO> create(@Valid @RequestBody MacroCategoryRequestDTO macroCategoryRequestDTO) {
        return new ResponseEntity<>(macroCategoryService.create(macroCategoryRequestDTO), HttpStatus.CREATED);
    }

    /**
     * Endpoint to delete an existing macro-category.
     *
     * @param macroCategoryRequestDTO the request data containing macro-category details to delete.
     * @return a {@link ResponseEntity} containing a boolean indicating the success of the operation and HTTP status 200 (OK).
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> delete(@Valid @RequestBody MacroCategoryRequestDTO macroCategoryRequestDTO) {
        return new ResponseEntity<>(macroCategoryService.delete(macroCategoryRequestDTO), HttpStatus.OK);
    }

    /**
     * Endpoint to update an existing macro-category.
     *
     * @param macroCategoryRequestDTO the request data containing updated macro-category details.
     * @return a {@link ResponseEntity} containing the updated {@link MacroCategoryResponseDTO} and HTTP status 201 (Created).
     */
    @PatchMapping("/update")
    public ResponseEntity<MacroCategoryResponseDTO> update(@Valid @RequestBody MacroCategoryRequestDTO macroCategoryRequestDTO) {
        return new ResponseEntity<>(macroCategoryService.update(macroCategoryRequestDTO), HttpStatus.OK);
    }

    /**
     * Endpoint to find macro-categories filtered by name.
     *
     * @param name the name filter for macro-categories (optional).
     * @return a {@link ResponseEntity} containing a list of {@link MacroCategoryResponseDTO} matching the filter and HTTP status 200 (OK).
     */
    @GetMapping("/findFiltered")
    public ResponseEntity<List<MacroCategoryResponseDTO>> findFiltered(@RequestParam(required = false) String name) {
        return new ResponseEntity<>(macroCategoryService.findFilteredMacroCategory(name), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve all macro-categories.
     *
     * @return a {@link ResponseEntity} containing a list of all {@link MacroCategoryResponseDTO} and HTTP status 200 (OK).
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<MacroCategoryResponseDTO>> findAll() {
        return new ResponseEntity<>(macroCategoryService.findAll(), HttpStatus.OK);
    }

    /**
     * Endpoint to find a macro-category by its ID.
     *
     * @param id the ID of the macro-category to retrieve.
     * @return a {@link ResponseEntity} containing the {@link MacroCategoryResponseDTO} for the specified ID and HTTP status 200 (OK).
     */
    @GetMapping("/findById")
    public ResponseEntity<MacroCategoryResponseDTO> findById(@RequestParam long id) {
        return new ResponseEntity<>(macroCategoryService.findById(id), HttpStatus.OK);
    }
}
