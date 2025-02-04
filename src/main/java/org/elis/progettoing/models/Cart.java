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

    /**
     * Constructs a new Cart with the specified ID, user, purchased products, and creation date.
     *
     * @param id the cart ID
     * @param user the user who owns the cart
     * @param purchasedProducts the list of purchased products in the cart
     * @param createdDate the creation date of the cart
     */
    public Cart(long id, User user, List<PurchasedProduct> purchasedProducts, LocalDate createdDate) {
        this.id = id;
        this.user = user;
        this.createdDate = createdDate;
        this.purchasedProducts = purchasedProducts;
    }

    /**
     * Default constructor.
     */
    public Cart() {}

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", createdDate=" + createdDate +
                '}';
    }
}
