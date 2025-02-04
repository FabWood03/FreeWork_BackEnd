package org.elis.progettoing.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a request to create or update a subcategory.
 * This DTO contains the necessary details for a subcategory, including its name and associated macro category ID.
 * The fields are validated using constraints to ensure that the data is valid before processing.
 */
@Data
public class SubCategoryRequestDTO {

    private long id;

    @NotBlank(message = "The name cannot be empty.")
    @Size(max = 50, message = "The name cannot exceed 50 characters.")
    private String name;

    @NotNull(message = "The macro category ID cannot be null.")
    private long macroCategoryId;

}
