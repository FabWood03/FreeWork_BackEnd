package org.elis.progettoing.dto.response.auction;

import lombok.Data;
import org.elis.progettoing.dto.response.user.UserResponseDTO;

/**
 * Data Transfer Object (DTO) representing a summary of an auction.
 * This class contains essential details of an auction such as its ID, title, description,
 * and the user who created or is associated with the auction.
 *
 * <p>This DTO is used when fetching basic information about an auction, typically for listing auctions
 * or providing a high-level overview of an auction's details.</p>
 */
@Data
public class AuctionSummaryDTO {

    private long id;

    private String title;

    private String description;

    private String state;

    private UserResponseDTO user;

    /**
     * Default constructor for the AuctionSummaryDTO class.
     */
    public AuctionSummaryDTO() {
        // Default constructor
    }
}
