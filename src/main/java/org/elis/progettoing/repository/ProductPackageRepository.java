package org.elis.progettoing.repository;

import org.elis.progettoing.models.product.ProductPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for the {@link ProductPackage} entity.
 */
public interface ProductPackageRepository extends JpaRepository<ProductPackage, Long> {
    Optional<ProductPackage> findByProductIdAndId(long productId, long id);
}
