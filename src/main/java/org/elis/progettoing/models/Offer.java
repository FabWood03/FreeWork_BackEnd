package org.elis.progettoing.models;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.models.auction.Auction;

import java.time.LocalDateTime;

/**
 * Represents an offer made by a user on an auction.
 * <p>
 * An offer is a proposal made by a user to purchase an item in an auction.
 * </p>
 */
@Data
@Entity
@Table(name = "offer")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Column(name = "delivery_time_proposed")
    private long deliveryTimeProposed;

    @Column(name = "price")
    private double price;

    @Column(name = "offer_date")
    private LocalDateTime offerDate;

    @Column(name = "score")
    private double score;

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", deliveryTimeProposed='" + deliveryTimeProposed + '\'' +
                ", price=" + price +
                '}';
    }
}
