package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.request.product.ProductRequestDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;
import org.elis.progettoing.dto.response.product.ProductSummaryDTO;
import org.elis.progettoing.dto.response.product.TagResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface for the ProductService class. Provides methods for creating, retrieving, and deleting products.
 */
public interface ProductService {
    ProductDetailsDTO createProduct(ProductRequestDTO productRequestDTO, List<MultipartFile> images);

    List<ProductDetailsDTO> findAll();

    ProductDetailsDTO findWithDetails(long id);

    List<ProductSummaryDTO> findAllSummaryByUserId(long id);

    boolean removeProduct(long id);

    List<TagResponseDTO> getTags(String nameFilter);

    List<ProductSummaryDTO> getProductSummary();
}
