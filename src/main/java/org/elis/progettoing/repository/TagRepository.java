package org.elis.progettoing.repository;

import org.elis.progettoing.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the {@link Tag} entity.
 */
public interface TagRepository extends JpaRepository<Tag, Long> {
}
