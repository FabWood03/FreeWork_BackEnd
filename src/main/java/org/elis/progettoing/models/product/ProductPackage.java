package org.elis.progettoing.models.product;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.enumeration.PackageType;
import org.elis.progettoing.models.OrderProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product package.
 * <p>
 * A product package is a set of features that can be purchased by a user.
 * </p>
 */
@Data
@Entity
@Table(name = "product_package")
public class ProductPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private PackageType type;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "description", length = 200, nullable = false)
    private String description;

    @Column(name = "delivery_time", nullable = false)
    private int deliveryTime;

    @Column(name = "revisions", nullable = false)
    private int revisions;

    @Column(name = "email_support", nullable = false)
    private boolean emailSupport;

    @Column(name = "chat_support", nullable = false)
    private boolean chatSupport;

    @OneToMany(mappedBy = "productPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackageAttribute> attributes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "selectedPackage", cascade = CascadeType.ALL)
    private List<PurchasedProduct> purchasedProducts = new ArrayList<>();

    @OneToMany(mappedBy = "selectedPackage", cascade = CascadeType.ALL)
    List<OrderProduct> orderProduct = new ArrayList<>();

    @Override
    public String toString() {
        return "ProductPackage{" +
                "id=" + id +
                ", type=" + type +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", deliveryTime=" + deliveryTime +
                ", revisions=" + revisions +
                ", emailSupport=" + emailSupport +
                ", chatSupport=" + chatSupport +
                '}';
    }
}
