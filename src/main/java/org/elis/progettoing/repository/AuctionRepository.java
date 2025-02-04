package org.elis.progettoing.repository;

import org.elis.progettoing.enumeration.AuctionStatus;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for the {@link Auction} entity.
 */
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByStatus(AuctionStatus status);

    @Query("SELECT a FROM Auction a WHERE (a.status = 'PENDING' AND a.startAuctionDate <= :now) " +
            "OR (a.status = 'OPEN' AND a.endAuctionDate <= :now)")
    List<Auction> findAllActiveAndPendingAuctions(LocalDateTime now);

    @Query("SELECT a FROM Auction a WHERE a.owner.id = :userId AND " +
            "(a.status = 'PENDING' OR a.status = 'OPEN')")
    List<Auction> findByOwnerIdAndStatus(@Param("userId") long userId);

    List<Auction> findByStatusAndWinnerIsNullAndOwner(AuctionStatus status, User owner);
}
