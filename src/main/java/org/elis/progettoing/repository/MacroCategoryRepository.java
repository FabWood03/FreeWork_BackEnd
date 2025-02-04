package org.elis.progettoing.repository;

import org.elis.progettoing.models.category.MacroCategory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the {@link MacroCategory} entity.
 */
public interface MacroCategoryRepository extends JpaRepository<MacroCategory, Long> {
}
