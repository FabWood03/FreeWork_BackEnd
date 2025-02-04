package org.elis.progettoing.dto.response.category;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a subcategory response.
 * This class contains the details of a subcategory, including its unique identifier, name,
 * and the ID of the associated macro category.
 *
 * <p>This DTO is used to return detailed information about a subcategory, including the subcategory ID,
 * name, and the ID of the parent macro category.</p>
 */
@Data
public class SubCategoryResponseDTO {

    private long id;

    private String name;

    private MacroCategoryResponseDTO macroCategory;

    /**
     * Constructs a new {@code SubCategoryResponseDTO} with the specified ID, name, and macro category.
     *
     * @param id the unique identifier of the subcategory.
     * @param name the name of the subcategory.
     * @param macroCategoryResponseDTO the macro category associated with the subcategory.
     */
    public SubCategoryResponseDTO(long id, String name, MacroCategoryResponseDTO macroCategoryResponseDTO) {
        this.id = id;
        this.name = name;
        this.macroCategory = macroCategoryResponseDTO;
    }

    /**
     * Default constructor.
     */
    public SubCategoryResponseDTO() {}
}
