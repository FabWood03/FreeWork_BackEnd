package org.elis.progettoing.models.category;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.models.auction.Auction;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a macro category.
 * <p>
 * A macro category is a high-level category that groups together related sub-categories.
 * </p>
 */
@Data
@Entity
@Table(name = "macro_category")
public class MacroCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 30, nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "macroCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubCategory> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "macroCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Auction> auctions = new ArrayList<>();

    /**
     * Dafault constructor
     */
    public MacroCategory() {}

    @Override
    public String toString() {
        return "MacroCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
