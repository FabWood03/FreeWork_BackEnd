package org.elis.progettoing.dto.response.auction;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.elis.progettoing.dto.response.category.MacroCategoryResponseDTO;
import org.elis.progettoing.dto.response.category.SubCategoryResponseDTO;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing the details of an auction.
 * This class extends {@link AuctionSummaryDTO} and includes additional auction-specific information
 * such as category details, auction start and end dates, and the winner of the auction.
 *
 * <p>It is used to transfer the complete details of an auction, including information about
 * its categories, delivery date, and timing of the auction.</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AuctionDetailsDTO extends AuctionSummaryDTO {

    private MacroCategoryResponseDTO macroCategory;

    private SubCategoryResponseDTO subCategory;

    private long deliveryDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAuctionDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAuctionDate;

    private long winnerId;
}
