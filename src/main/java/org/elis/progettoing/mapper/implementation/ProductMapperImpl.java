package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.product.ProductRequestDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;
import org.elis.progettoing.dto.response.product.ProductSummaryDTO;
import org.elis.progettoing.mapper.definition.ProductMapper;
import org.elis.progettoing.models.category.SubCategory;
import org.elis.progettoing.models.product.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the ProductMapper interface. Provides methods for mapping between
 * product-related request and response DTOs, as well as entity models.
 */
@Component
public class ProductMapperImpl implements ProductMapper {

    private final UserMapperImpl userMapperImpl;
    private final ProductPackageMapperImpl productPackageMapperImpl;
    private final TagMapperImpl tagMapperImpl;
    private final SubCategoryMapperImpl subCategoryMapperImpl;

    /**
     * Constructs a new ProductMapperImpl with the specified UserMapper, ProductPackageMapper, TagMapper, and SubCategoryMapper.
     *
     * @param userMapperImpl           the UserMapper to be used for mapping
     * @param productPackageMapperImpl the ProductPackageMapper to be used for mapping
     * @param tagMapperImpl            the TagMapper to be used for mapping
     * @param subCategoryMapperImpl    the SubCategoryMapper to be used for mapping
     */
    public ProductMapperImpl(UserMapperImpl userMapperImpl, ProductPackageMapperImpl productPackageMapperImpl, TagMapperImpl tagMapperImpl, SubCategoryMapperImpl subCategoryMapperImpl) {
        this.userMapperImpl = userMapperImpl;
        this.productPackageMapperImpl = productPackageMapperImpl;
        this.tagMapperImpl = tagMapperImpl;
        this.subCategoryMapperImpl = subCategoryMapperImpl;
    }

    /**
     * Converts a ProductRequestDTO to a Product entity.
     *
     * @param requestDTO the ProductRequestDTO to be converted
     * @return a Product entity populated with data from the ProductRequestDTO, or null if the requestDTO is null
     */
    @Override
    public Product productRequestDTOToProduct(ProductRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Product product = new Product();

        product.setSubCategory(productRequestDTOToSubCategory(requestDTO));
        product.setPackages(productPackageMapperImpl.productPackageRequestDTOListToProductPackageList(requestDTO.getPackages()));
        product.setTitle(requestDTO.getTitle());
        product.setDescription(requestDTO.getDescription());
        product.setTags(tagMapperImpl.tagDTOListToTagList(requestDTO.getTags()));

        return product;
    }

    /**
     * Converts a Product entity to a ProductDetailsDTO.
     *
     * @param product the Product entity to be converted
     * @return a ProductDetailsDTO populated with data from the Product entity, or null if the product is null
     */
    @Override
    public ProductDetailsDTO productToResponseDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDetailsDTO productDetailsDTO = new ProductDetailsDTO();

        // Maps sub-category to SubCategoryResponseDTO
        productDetailsDTO.setSubCategory(subCategoryMapperImpl.subCategoryToResponseDTO(product.getSubCategory()));

        // Maps user
        productDetailsDTO.setUser(userMapperImpl.userToUserResponseDTO(product.getUser()));

        // Maps other product details
        productDetailsDTO.setId(product.getId());
        productDetailsDTO.setTitle(product.getTitle());
        productDetailsDTO.setDescription(product.getDescription());
        productDetailsDTO.setPackages(productPackageMapperImpl.productPackageListToProductPackageResponseDTOList(product.getPackages()));
        productDetailsDTO.setUrlProductPhoto(product.getUrlProductPhotos());
        productDetailsDTO.setTags(tagMapperImpl.tagToTagListResponseDTO(product.getTags()));

        return productDetailsDTO;
    }

    /**
     * Converts a list of Product entities to a list of ProductDetailsDTOs.
     *
     * @param products the list of Product entities to be converted
     * @return a list of ProductDetailsDTOs populated with data from the list of Product entities
     */
    @Override
    public List<ProductDetailsDTO> productsToResponseDTOs(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }

        List<ProductDetailsDTO> list = new ArrayList<>(products.size());
        for (Product product : products) {
            list.add(productToResponseDTO(product));
        }

        return list;
    }

    /**
     * Converts a list of Product entities to a list of ProductSummaryDTOs.
     *
     * @param products the list of Product entities to be converted
     * @return a list of ProductSummaryDTOs populated with data from the list of Product entities
     */
    @Override
    public List<ProductSummaryDTO> productsToSummaryResponseDTOs(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }

        List<ProductSummaryDTO> list = new ArrayList<>(products.size());
        for (Product product : products) {
            list.add(productToSummaryDTO(product));
        }

        return list;
    }

    /**
     * Converts a Product entity to a ProductSummaryDTO.
     *
     * @param product the Product entity to be converted
     * @return a ProductSummaryDTO populated with data from the Product entity, or null if the product is null
     */
    public ProductSummaryDTO productToSummaryDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductSummaryDTO productSummaryDTO = new ProductSummaryDTO();

        productSummaryDTO.setId(product.getId());
        productSummaryDTO.setTitle(product.getTitle());
        productSummaryDTO.setDescription(product.getDescription());
        productSummaryDTO.setUser(userMapperImpl.userToUserResponseDTO(product.getUser()));
        productSummaryDTO.setUrlProductPhoto(product.getUrlProductPhotos());
        productSummaryDTO.setStartPrice(product.getPackages().getFirst().getPrice());

        return productSummaryDTO;
    }

    /**
     * Converts a ProductRequestDTO to a SubCategory entity.
     *
     * @param productRequestDTO the ProductRequestDTO containing the SubCategory ID
     * @return a SubCategory entity populated with data from the ProductRequestDTO, or null if the requestDTO is null
     */
    public SubCategory productRequestDTOToSubCategory(ProductRequestDTO productRequestDTO) {
        if (productRequestDTO == null) {
            return null;
        }

        SubCategory subCategory = new SubCategory();
        subCategory.setId(productRequestDTO.getSubCategoryId());

        return subCategory;
    }
}
