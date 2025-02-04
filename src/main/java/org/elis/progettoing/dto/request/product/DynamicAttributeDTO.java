package org.elis.progettoing.dto.request.product;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a dynamic attribute associated with a product.
 * This DTO allows for storing an arbitrary attribute and its corresponding value, which can be of any type.
 * The class provides a flexible structure for associating additional, customizable attributes to products,
 * supporting different key-value pairs as needed.
 */
@Data
public class DynamicAttributeDTO {

    private String key;

    private Object value;

}
