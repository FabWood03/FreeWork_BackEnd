package org.elis.progettoing.repository;

import org.elis.progettoing.models.Review;
import org.elis.progettoing.models.Ticket;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the {@link Ticket} entity.
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketRequesterAndReportedUserId(User user, long reportedUserId);

    Optional<Ticket> findByTicketRequesterAndReportedReviewId(User user, long reportedReviewId);

    Optional<Ticket> findByTicketRequesterAndReportedProductId(User user, long reportedProductId);

    long countByReportedUser(User user);

    long countByReportedReview(Review review);

    long countByReportedProduct(Product product);

    @Modifying
    @Query("UPDATE Ticket t SET t.reportedProduct = NULL WHERE t.reportedProduct.id = :productId")
    void unsetProduct(@Param("productId") Long productId);

    @Modifying
    @Query("UPDATE Ticket t SET t.reportedUser = NULL WHERE t.reportedUser.id = :userId")
    void unsetReportedUser(@Param("userId") Long userId);

    @Query("SELECT t FROM Ticket t WHERE t.state IN ('Accettato', 'Rifiutato')")
    List<Ticket> findByStateAcceptedOrRefused();

    @Query("SELECT t FROM Ticket t WHERE t.state = 'In attesa'")
    List<Ticket> findByStatePending();

    @Query("SELECT t FROM Ticket t WHERE t.state = 'In lavorazione'")
    List<Ticket> findByStateTakeOn();

    @Query("SELECT t FROM Ticket t WHERE t.state IN ('Accettato', 'Rifiutato', 'In attesa', 'In lavorazione')")
    List<Ticket> findAllOpenTickets();

    @Modifying
    @Query("UPDATE Ticket t SET t.reportedReview = NULL WHERE t.reportedReview.id = :reviewId")
    void unsetReview(@Param("reviewId") long reviewId);

    @Modifying
    @Query("UPDATE Ticket t SET t.ticketRequester = NULL WHERE t.ticketRequester.id = :userId")
    void unsetRequesterId(long userId);
}
