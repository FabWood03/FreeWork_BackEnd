package org.elis.progettoing.service.implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.elis.progettoing.dto.request.FilterRequest;
import org.elis.progettoing.dto.response.FilteredEntitiesResponse;
import org.elis.progettoing.dto.response.auction.AuctionSummaryDTO;
import org.elis.progettoing.dto.response.product.ProductSummaryDTO;
import org.elis.progettoing.mapper.definition.AuctionMapper;
import org.elis.progettoing.mapper.definition.ProductMapper;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.models.category.SubCategory;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.service.definition.FilterService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilterServiceImpl implements FilterService {
    private final EntityManager entityManager;
    private final AuctionMapper auctionMapper;
    private final ProductMapper productMapper;

    public FilterServiceImpl(EntityManager entityManager, AuctionMapper auctionMapper, ProductMapper productMapper) {
        this.entityManager = entityManager;
        this.auctionMapper = auctionMapper;
        this.productMapper = productMapper;
    }

    @Override
    public FilteredEntitiesResponse getFilteredEntities(FilterRequest filterRequest) {
        List<AuctionSummaryDTO> filteredAuctions = getFilteredAuctions(filterRequest).stream()
                .map(auctionMapper::auctionToAuctionSummaryDTO)
                .toList();

        List<ProductSummaryDTO> filteredProducts = getFilteredProducts(filterRequest).stream()
                .map(productMapper::productToSummaryDTO)
                .toList();

        return new FilteredEntitiesResponse(filteredAuctions, filteredProducts);
    }

    private List<Auction> getFilteredAuctions(FilterRequest filterRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Auction> query = cb.createQuery(Auction.class);
        Root<Auction> root = query.from(Auction.class);

        List<Predicate> predicates = new ArrayList<>();

        if (filterRequest.getSubCategory() != null) {
            SubCategory subCategory = entityManager.find(SubCategory.class, filterRequest.getSubCategory());
            if (subCategory != null) {
                predicates.add(cb.equal(root.get("subCategory"), subCategory));
            }
        }

        if (filterRequest.getSearchText() != null) {
            String searchPattern = "%" + filterRequest.getSearchText().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("owner").get("name")), searchPattern),
                    cb.like(cb.lower(root.get("owner").get("surname")), searchPattern),
                    cb.like(cb.lower(root.get("owner").get("nickname")), searchPattern),
                    cb.like(cb.lower(root.get("title")), searchPattern)
            ));
        }

        if (filterRequest.getDeliveryTime() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("deliveryDate"), filterRequest.getDeliveryTime()));
        }

        if (predicates.isEmpty()) {
            // Se nessun filtro è applicato, restituisci tutte le aste
            return entityManager.createQuery(query).getResultList();
        }

        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

        query.distinct(true);

        return entityManager.createQuery(query).getResultList();
    }

    private List<Product> getFilteredProducts(FilterRequest filterRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        if (filterRequest.getSubCategory() != null) {
            SubCategory subCategory = entityManager.find(SubCategory.class, filterRequest.getSubCategory());
            if (subCategory != null) {
                predicates.add(cb.equal(root.get("subCategory"), subCategory));
            }
        }

        if (filterRequest.getSearchText() != null) {
            String searchPattern = "%" + filterRequest.getSearchText().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("user").get("name")), searchPattern),
                    cb.like(cb.lower(root.get("user").get("surname")), searchPattern),
                    cb.like(cb.lower(root.get("user").get("nickname")), searchPattern),
                    cb.like(cb.lower(root.get("title")), searchPattern)
            ));
        }

        // Gestisci il filtro sul prezzo
        if (filterRequest.getMinBudget() != null || filterRequest.getMaxBudget() != null) {
            // Filtra i pacchetti per il prezzo
            Join<Product, Package> packageJoin = root.join("packages", JoinType.INNER);

            List<Predicate> pricePredicates = new ArrayList<>();
            if (filterRequest.getMinBudget() != null) {
                pricePredicates.add(cb.greaterThanOrEqualTo(packageJoin.get("price"), filterRequest.getMinBudget()));
            }
            if (filterRequest.getMaxBudget() != null) {
                pricePredicates.add(cb.lessThanOrEqualTo(packageJoin.get("price"), filterRequest.getMaxBudget()));
            }

            if (!pricePredicates.isEmpty()) {
                predicates.add(cb.and(pricePredicates.toArray(new Predicate[0])));
            }
        }

        if (filterRequest.getDeliveryTime() != null) {
            Join<Product, Package> packageJoin = root.join("packages", JoinType.INNER);
            predicates.add(cb.lessThanOrEqualTo(packageJoin.get("deliveryTime"), filterRequest.getDeliveryTime()));
        }

        // Esegui la query con i predicati
        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

        // Rimuovi i duplicati dai risultati (un prodotto sarà restituito solo una volta)
        query.distinct(true);

        return entityManager.createQuery(query).getResultList();
    }
}