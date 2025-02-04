package org.elis.progettoing.repository;

import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.models.auction.AuctionSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for the {@link AuctionSubscription} entity.
 */
public interface AuctionSubscriptionRepository extends JpaRepository<AuctionSubscription, Long> {

    List<AuctionSubscription> findByAuction(Auction auction);

    List<AuctionSubscription> findByUser(User user);

    AuctionSubscription findByAuctionAndUser(Auction auction, User user);

    boolean existsByAuctionAndUser(Auction auction, User user);
}
