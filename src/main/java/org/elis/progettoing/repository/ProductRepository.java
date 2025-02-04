package org.elis.progettoing.repository;

import org.elis.progettoing.models.category.SubCategory;
import org.elis.progettoing.models.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for the {@link Product} entity.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySubCategory(SubCategory subCategory);

    List<Product> findAllByUserId(long userId);

    @Modifying
    @Query("UPDATE Product p SET p.user.id = NULL WHERE p.user.id = :userId")
    void unsetUser(@Param("userId") long userId);
}
