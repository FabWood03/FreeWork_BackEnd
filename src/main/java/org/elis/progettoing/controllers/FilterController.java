package org.elis.progettoing.controllers;

import org.elis.progettoing.dto.request.FilterRequest;
import org.elis.progettoing.dto.response.FilteredEntitiesResponse;
import org.elis.progettoing.service.definition.FilterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing filter operations.
 * Provides endpoints for filtering entities based on user preferences.
 */
@RestController
@RequestMapping("/api/filter")
public class FilterController {
    private final FilterService filterService;

    /**
     * Constructs an instance of {@code FilterController}.
     *
     * @param filterService the service managing filter-related business logic.
     */
    public FilterController(FilterService filterService) {
        this.filterService = filterService;
    }

    /**
     * Endpoint to filter entities based on user preferences.
     *
     * @param filterRequest the user preferences for filtering entities.
     * @return a {@link ResponseEntity} containing the filtered entities and HTTP status 200 (OK).
     */
    @PostMapping("/filterHome")
    public ResponseEntity<FilteredEntitiesResponse> filterHome(@RequestBody FilterRequest filterRequest) {
        return new ResponseEntity<>(filterService.getFilteredEntities(filterRequest), HttpStatus.OK);
    }
}
