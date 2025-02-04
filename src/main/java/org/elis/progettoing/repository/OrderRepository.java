package org.elis.progettoing.repository;

import org.elis.progettoing.enumeration.OrderProductStatus;
import org.elis.progettoing.models.Order;
import org.elis.progettoing.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for the {@link Order} entity.
 */
public interface OrderRepository  extends JpaRepository<Order, Long> {
    List<Order> findByBuyer(User buyer);

    @Query("SELECT o FROM Order o JOIN o.orderProducts pp JOIN pp.product p WHERE p.user = :seller")
    List<Order> findAllBySeller(@Param("seller") User seller);

    @Query("SELECT o FROM Order o " +
            "JOIN o.orderProducts op " +
            "JOIN op.product p " +
            "WHERE p.user = :seller " +
            "AND op.status = :status")
    List<Order> findAllBySellerAndProductStatus(User seller, OrderProductStatus status);

}
