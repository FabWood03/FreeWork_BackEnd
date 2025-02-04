package org.elis.progettoing.repository;

import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for the {@link PurchasedProduct} entity.
 */
public interface PurchasedProductRepository extends JpaRepository<PurchasedProduct, Long> {
    List<PurchasedProduct> findByCartId(long cartId);

    @Query("SELECT pp FROM PurchasedProduct pp WHERE pp.buyer.cart.id = :cartId")
    List<PurchasedProduct> findAllByCartId(Long cartId);

    List<PurchasedProduct> findByProduct(Product product);

    long countByBuyer(User buyer);

    @Modifying
    @Query("DELETE FROM PurchasedProduct p WHERE p.buyer.id = :buyerId")
    void deletePurchasedProductsByBuyerId(@Param("buyerId") Long buyerId);
}
