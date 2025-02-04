package org.elis.progettoing.dto.request.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a product request.
 * This DTO is used for submitting product information including its title, description,
 * associated subcategory, product packages, and tags.
 * The class allows the user to provide essential details for creating or updating a product in the system.
 * It includes validation rules to ensure required fields are provided.
 */
@Data
public class ProductRequestDTO {
    @NotNull(message = "Il titolo è obbligatorio")
    @Max(value = 80)
    @Min(value = 25)
    private String title;

    @NotNull(message = "La descrizione è obbligatoria")
    @Max(value = 1500)
    @Min(value = 25)
    private String description;

    @NotNull(message = "La sotto categoria è obbligatoria")
    private long subCategoryId;

    @NotNull(message = "I pacchetti non possono essere vuoti")
    private List<ProductPackageRequestDTO> packages = new ArrayList<>();

    @NotNull(message = "I tag non possono essere vuoti")
    private List<TagDTO> tags = new ArrayList<>();
}
