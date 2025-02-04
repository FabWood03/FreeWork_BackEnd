package org.elis.progettoing.repository;

import org.elis.progettoing.models.Offer;
import org.elis.progettoing.models.auction.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for the {@link Offer} entity.
 */
public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findAllByAuctionId(long id);

    Offer findBySellerIdAndAuctionId(long sellerId, long auctionId);

    List<Offer> auction(Auction auction);
}
