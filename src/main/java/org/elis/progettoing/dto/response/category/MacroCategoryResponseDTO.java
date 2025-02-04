package org.elis.progettoing.dto.response.category;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a macro category response.
 * This class contains the details of a macro category, including its unique identifier and name.
 *
 * <p>This DTO is used to return detailed information about a macro category, including the ID and name of the category.</p>
 */
@Data
public class MacroCategoryResponseDTO {

    private long id;

    private String name;

    /**
     * Default constructor.
     */
    public MacroCategoryResponseDTO() {}

    /**
     * Constructs a new {@code MacroCategoryResponseDTO} with the specified ID and name.
     *
     * @param id the unique identifier of the macro category.
     * @param name the name of the macro category.
     */
    public MacroCategoryResponseDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
