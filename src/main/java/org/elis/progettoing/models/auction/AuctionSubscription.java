package org.elis.progettoing.models.auction;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.models.User;

/**
 * Represents a user's subscription to an auction.
 * <p>
 * A subscription is a user's request to receive notifications about an auction.
 * </p>
 */
@Data
@Entity
@Table(name = "auction_subscription")
public class AuctionSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;
}
