package org.elis.progettoing.models;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.enumeration.OrderProductStatus;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.ProductPackage;

import java.time.LocalDateTime;

/**
 * Represents a product in an order.
 * <p>
 * An order product is a product that a user wants to buy.
 * </p>
 */
@Data
@Entity
@Table(name = "order_product")
public class OrderProduct {

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
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderProductStatus status;

    @Column(name = "seller_description")
    private String description;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Transient // Esclude il campo dal mapping Hibernate
    private boolean reviewExist;

    public OrderProduct() {
        // Default constructor
    }

    @Override
    public String toString() {
        return "OrderProduct{id=" + id +
                ", product=" + product.getTitle() +
                ", selectedPackage=" + selectedPackage.getType() +
                ", status=" + status +
                ", estimatedDeliveryDate=" + estimatedDeliveryDate + '}';
    }
}
