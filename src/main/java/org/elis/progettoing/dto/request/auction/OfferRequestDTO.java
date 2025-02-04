package org.elis.progettoing.dto.request.auction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a request to place an offer in an auction.
 * This DTO contains the necessary details for the offer, including the auction ID,
 * proposed delivery time, and offer price.
 * The fields are validated using constraints to ensure that the data is valid before processing.
 */
@Data
public class OfferRequestDTO {

    @NotNull(message = "The auction ID cannot be null.")
    private long auctionId;

    @NotNull(message = "The proposed delivery date cannot be empty.")
    @Min(value = 1, message = "The proposed delivery date must be at least 1 day.")
    private long deliveryTimeProposed;

    @Positive(message = "The price must be a positive value.")
    @DecimalMin(value = "0.01", message = "The price must be at least 0.01.")
    private double price;
}
