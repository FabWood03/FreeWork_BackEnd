package org.elis.progettoing.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an order made by a user.
 * <p>
 * An order is a request made by a user to purchase products.
 * </p>
 */
@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User buyer;

    @Column(name = "total_price")
    private long totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderProduct> orderProducts;

    @Override
    public String toString() {
        return "Order{id=" + id +
                ", orderDate=" + orderDate +
                ", buyer=" + buyer.getUsername() +
                ", totalPrice=" + totalPrice +
                ", numberOfProducts=" + (orderProducts != null ? orderProducts.size() : 0) + '}';
    }
}
