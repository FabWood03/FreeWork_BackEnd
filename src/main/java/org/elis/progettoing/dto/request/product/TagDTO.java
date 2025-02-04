package org.elis.progettoing.dto.request.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a tag associated with a product.
 * This DTO is used to provide the necessary details for a tag, which can be applied to products
 * for categorization or identification purposes.
 * The class ensures that a tag has a name, which is a mandatory field.
 */
@Data
public class TagDTO {

    @NotNull(message = "Il nome del tag non può essere nullo.")
    private String name;

    /**
     * Constructs a new TagDTO with the specified name.
     *
     * @param name the name of the tag
     */
    public TagDTO(String name) {
        this.name = name;
    }

    /**
     * Constructs a new TagDTO with no parameters.
     */
    public TagDTO() {
    }
}
