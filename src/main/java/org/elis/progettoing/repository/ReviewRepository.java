package org.elis.progettoing.repository;

import org.elis.progettoing.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Repository for the {@link Review} entity.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Review findByUserIdAndProductId(long userId, long productId);

    Page<Review> findByProductId(long productId, Pageable pageable);

    List<Review> findByUserId(long userId);

    @Query("SELECT r FROM Review r JOIN r.product p WHERE p.user.id = :userId")
    List<Review> findByProductOwnerId(@Param("userId") long userId);

    @Query("SELECT COALESCE(AVG(r.totalRating), 0) " +
            "FROM Review r " +
            "WHERE r.user.id = :userId AND r.dateCreation > :startDate")
    double getAverageRatingForSeller(@Param("userId") long userId, @Param("startDate") LocalDateTime startDate);


    @Query("SELECT r.totalRating, COUNT(r) " +
            "FROM Review r " +
            "WHERE r.product.id = :productId " +
            "GROUP BY r.totalRating " +
            "HAVING r.totalRating BETWEEN 1 AND 5")
    List<Object[]> countReviewsByProductId(@Param("productId") long productId);

    @Query("SELECT r.totalRating, COUNT(r) " +
            "FROM Review r " +
            "JOIN r.product p " +
            "WHERE p.user.id = :userId " +
            "GROUP BY r.totalRating")
    List<Object[]> countReviewsByProductUserId(@Param("userId") long userId);

    @Modifying
    @Query("UPDATE Review r SET r.user.id = NULL WHERE r.user.id = :userId")
    void unsetUser(@Param("userId") long userId);

    @Query("SELECT r.product.id FROM Review r WHERE r.user.id = :userId AND r.product.id IN :productIds")
    Set<Long> findReviewedProductIdsByUser(@Param("userId") long userId, @Param("productIds") Set<Long> productIds);

    boolean existsByUserIdAndProductId(long id, long productId);
}
