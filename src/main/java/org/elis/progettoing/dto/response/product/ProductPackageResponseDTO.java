package org.elis.progettoing.dto.response.product;

import lombok.Data;
import org.elis.progettoing.enumeration.PackageType;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a package of a product.
 * This class provides detailed information about a specific package available for a product.
 * It includes the type of package, price, description, delivery time, revisions, support options,
 * and any attributes associated with the package.
 *
 * <p>The {@link ProductPackageResponseDTO} class is used to return detailed information
 * about a specific package of a product in response to a request.</p>
 */
@Data
public class ProductPackageResponseDTO {

    private long id;

    private PackageType type;

    private double price;

    private String description;

    private int deliveryTime;

    private int revisions;

    private boolean emailSupport;

    private boolean chatSupport;

    private List<PackageAttributeResponseDTO> attributes;
}
