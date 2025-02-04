package org.elis.progettoing.models;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.models.product.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tag.
 * <p>
 * A tag is a keyword that can be associated with a product.
 * </p>
 */
@Data
@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 50, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Product> products = new ArrayList<>();

    /**
     * Constructs a new tag with the specified name.
     *
     * @param name the name of the tag
     */
    public Tag(String name) {
        this.name = name;
    }

    public Tag() {

    }
}
