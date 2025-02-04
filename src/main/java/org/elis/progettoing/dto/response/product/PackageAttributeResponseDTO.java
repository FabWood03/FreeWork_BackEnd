package org.elis.progettoing.dto.response.product;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an attribute of a product package.
 * This class contains a key-value pair that represents a specific attribute associated with a product package.
 *
 * <p>This DTO is used to return the attributes of a product package, where the key is the name of the attribute
 * and the value is the value associated with that attribute.</p>
 */
@Data
public class PackageAttributeResponseDTO {

    private String key;

    private Object value;
}
