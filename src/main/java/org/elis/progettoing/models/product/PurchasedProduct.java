package org.elis.progettoing.models.product;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.models.Cart;
import org.elis.progettoing.models.User;

import java.time.LocalDate;

/**
 * Represents a purchased product.
 * <p>
 * A purchased product is a product that has been bought by a user.
 * </p>
 */
@Data
@Entity
@Table(name = "purchased_product")
public class PurchasedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private ProductPackage selectedPackage;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User buyer;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Override
    public String toString() {
        return "PurchasedProduct{" +
                "id=" + id +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}
