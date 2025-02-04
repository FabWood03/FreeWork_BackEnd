package org.elis.progettoing.dto.request.auction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.elis.progettoing.utils.customAnnotation.ValidAuctionDates;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing the request for creating or updating an auction.
 * It contains the necessary details for the auction, such as title, description, categories, and dates.
 * This DTO is validated using constraints, including custom validation for auction dates.
 */
@Data
@ValidAuctionDates
public class AuctionRequestDTO {

    private long id;

    @NotNull(message = "The auction title cannot be empty")
    @Size(min = 1, max = 100, message = "The auction title must be between 1 and 100 characters")
    private String title;

    @NotNull(message = "The product description cannot be empty")
    @Size(min = 1, max = 1000, message = "The product description must be between 1 and 1000 characters")
    private String descriptionProduct;

    @NotNull(message = "The macro category cannot be null")
    private long macroCategoryId;

    @NotNull(message = "The subcategory cannot be null")
    private long subCategoryId;

    @NotNull(message = "The delivery date cannot be null")
    @Min(value = 1, message = "The delivery date must be at least 1 day in the future")
    private long deliveryDate;

    @NotNull(message = "The auction end date cannot be null")
    private LocalDateTime endAuctionDate;

    @NotNull(message = "The auction start date cannot be null")
    private LocalDateTime startAuctionDate;

    /**
     * Default constructor for AuctionRequestDTO.
     */
    public AuctionRequestDTO() {
        // Default constructor
    }
}
