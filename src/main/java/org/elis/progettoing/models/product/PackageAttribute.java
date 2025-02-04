package org.elis.progettoing.models.product;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.utils.customConverter.ObjectConverter;

/**
 * Represents a package attribute.
 * <p>
 * A package attribute is a key-value pair that describes a product package.
 * </p>
 */
@Data
@Entity
@Table(name = "package_attribute")
public class PackageAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "attribute_key", nullable = false)
    private String key;

    @Column(name = "attribute_value", nullable = false)
    @Convert(converter = ObjectConverter.class)
    private Object value;

    @ManyToOne
    @JoinColumn(name = "product_package_id", nullable = false)
    private ProductPackage productPackage;

    @Override
    public String toString() {
        return "PackageAttribute{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
