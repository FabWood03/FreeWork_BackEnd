package org.elis.progettoing.dto.response;

import lombok.Data;
import org.elis.progettoing.dto.response.auction.AuctionSummaryDTO;
import org.elis.progettoing.dto.response.product.ProductSummaryDTO;

import java.util.List;

/**
 * This class represents the response DTO for filtered entities.
 * It contains the data that will be sent to the client.
 */
@Data
public class FilteredEntitiesResponse {
    private List<AuctionSummaryDTO> filteredAuctions;
    private List<ProductSummaryDTO> filteredProducts;

    /**
     * Constructs a new FilteredEntitiesResponse with the specified filtered auctions and products.
     *
     * @param filteredAuctions the list of AuctionSummaryDTO containing the filtered auctions
     * @param filteredProducts the list of ProductSummaryDTO containing the filtered products
     */
    public FilteredEntitiesResponse(List<AuctionSummaryDTO> filteredAuctions, List<ProductSummaryDTO> filteredProducts) {
        this.filteredAuctions = filteredAuctions;
        this.filteredProducts = filteredProducts;
    }

    /**
     * Default constructor for FilteredEntitiesResponse.
     */
    public FilteredEntitiesResponse() {

    }
}
