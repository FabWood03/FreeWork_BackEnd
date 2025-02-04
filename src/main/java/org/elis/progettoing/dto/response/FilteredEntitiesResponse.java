package org.elis.progettoing.dto.response;

import lombok.Data;
import org.elis.progettoing.dto.response.auction.AuctionSummaryDTO;
import org.elis.progettoing.dto.response.product.ProductSummaryDTO;

import java.util.List;

@Data
public class FilteredEntitiesResponse {
    private List<AuctionSummaryDTO> filteredAuctions;
    private List<ProductSummaryDTO> filteredProducts;

    public FilteredEntitiesResponse(List<AuctionSummaryDTO> filteredAuctions, List<ProductSummaryDTO> filteredProducts) {
        this.filteredAuctions = filteredAuctions;
        this.filteredProducts = filteredProducts;
    }

    public FilteredEntitiesResponse() {

    }
}
