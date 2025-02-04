package org.elis.progettoing.models.auction;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.enumeration.AuctionStatus;
import org.elis.progettoing.models.Offer;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.category.MacroCategory;
import org.elis.progettoing.models.category.SubCategory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an auction.
 * <p>
 * An auction is a process of buying and selling services by offering them up for bid, taking bids, and then selling the item to the highest bidder.
 * </p>
 */
@Data
@Entity
@Table(name = "auction")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description_service", length = 1000, nullable = false)
    private String descriptionProduct;

    @ManyToOne
    @JoinColumn(name = "macro_category_id", nullable = false)
    private MacroCategory macroCategory;

    @ManyToOne
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;

    @Column(name = "delivery_date", nullable = false)
    private long deliveryDate;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startAuctionDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endAuctionDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Offer> offers = new ArrayList<>();

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuctionSubscription> auctionSubscriptions = new ArrayList<>();

    @Override
    public String toString() {
        return "Auction{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", descriptionProduct='" + descriptionProduct + '\'' +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", startAuctionDate=" + startAuctionDate +
                ", endAuctionDate=" + endAuctionDate +
                '}';
    }
}
