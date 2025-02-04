package org.elis.progettoing.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a request to create or update a macro category.
 * This DTO contains the necessary details for a macro category, including its name.
 * The fields are validated using constraints to ensure that the data is valid before processing.
 */
@Data
public class MacroCategoryRequestDTO {

    private long id;

    @NotBlank(message = "The name of the macro category cannot be empty.")
    @Size(max = 30, message = "The name of the macro category must be at most 30 characters long.")
    private String name;

}
