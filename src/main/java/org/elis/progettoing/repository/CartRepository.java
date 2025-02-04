package org.elis.progettoing.repository;

import org.elis.progettoing.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for the {@link Cart} entity.
 */
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(long id);
}
