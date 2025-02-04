package org.elis.progettoing.models.category;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.models.product.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sub-category.
 * <p>
 * A sub-category is a category that is a subset of a macro category.
 * </p>
 */
@Data
@Entity
@Table(name = "sub_category")
public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "macro_category_id")
    private MacroCategory macroCategory;

    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL)
    private List<Product> events = new ArrayList<>();

    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Auction> auctions = new ArrayList<>();

    @Override
    public String toString() {
        return "SubCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", macroCategory=" + macroCategory +
                '}';
    }
}
