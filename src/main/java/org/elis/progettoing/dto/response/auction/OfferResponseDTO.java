package org.elis.progettoing.dto.response.auction;

import lombok.Data;
import org.elis.progettoing.dto.response.user.UserResponseDTO;

/**
 * Data Transfer Object (DTO) representing an offer made for an auction.
 * This class contains details about the offer, including the ID, the auction it is associated with,
 * the proposed delivery time, the price, and the seller who made the offer.
 *
 * <p>This DTO is used to return information about an offer, typically used when retrieving or displaying
 * the details of an offer made on an auction platform.</p>
 */
@Data
public class OfferResponseDTO {

    private long id;

    private long auctionId;

    private long deliveryTimeProposed;

    private double price;

    private UserResponseDTO seller;
}
