package org.elis.progettoing.repository;

import org.elis.progettoing.models.product.PackageAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the {@link PackageAttribute} entity.
 */
public interface PackageAttributeRepository extends JpaRepository<PackageAttribute, Long> {
}
