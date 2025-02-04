package org.elis.progettoing.repository;

import org.elis.progettoing.models.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the {@link OrderProduct} entity.
 */
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
