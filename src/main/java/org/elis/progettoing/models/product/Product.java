package org.elis.progettoing.models.product;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.models.*;
import org.elis.progettoing.models.category.SubCategory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product.
 * <p>
 * A product is a service that can be bought or sold.
 * </p>
 */
@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", length = 80, nullable = false)
    private String title;

    @Column(name = "description", length = 1500, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @Column(name = "date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "url_product_photo", nullable = false)
    @CollectionTable(name = "product_photo", joinColumns = @JoinColumn(name = "product_id"))
    @ElementCollection
    private List<String> urlProductPhotos = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductPackage> packages = new ArrayList<>();

    @OneToMany(mappedBy = "reportedProduct")
    private List<Ticket> ticketsReporting = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderProduct> orderProduct = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "product_tag",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
