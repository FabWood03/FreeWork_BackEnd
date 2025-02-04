package org.elis.progettoing.models;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.models.product.PurchasedProduct;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cart.
 * <p>
 * A cart is a container for products that a user wants to buy.
 * </p>
 */
@Data
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(mappedBy = "cart")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PurchasedProduct> purchasedProducts = new ArrayList<>();

    @Column(name = "created_date")
    private LocalDate createdDate;

    public Cart(long id, User user, List<PurchasedProduct> purchasedProducts, LocalDate createdDate) {
        this.id = id;
        this.user = user;
        this.createdDate = createdDate;
        this.purchasedProducts = purchasedProducts;
    }

    public Cart() {}

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", createdDate=" + createdDate +
                '}';
    }
}
