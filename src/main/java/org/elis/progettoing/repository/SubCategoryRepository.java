package org.elis.progettoing.repository;

import org.elis.progettoing.models.category.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the {@link SubCategory} entity.
 */
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
}
