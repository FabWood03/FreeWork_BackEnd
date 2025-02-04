package org.elis.progettoing.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for submitting a review request.
 * This DTO is used when a user submits a review for a product or service,
 * including ratings for various aspects like quality, communication, timeliness, and cost.
 * The review also includes a comment for additional feedback.
 * All fields are validated to ensure proper review submission.
 */
@Data
public class ReviewRequestDTO {

    private long id;

    @Size(max = 1000, message = "Il commento non può superare i 1000 caratteri")
    private String comment;

    @NotNull(message = "La valutazione della qualità è obbligatoria")
    @Min(value = 1, message = "La valutazione della qualità deve essere compresa tra 1 e 5")
    @Max(value = 5, message = "La valutazione della qualità deve essere compresa tra 1 e 5")
    private double ratingQuality;

    @NotNull(message = "La valutazione della comunicazione è obbligatoria")
    @Min(value = 1, message = "La valutazione della comunicazione deve essere compresa tra 1 e 5")
    @Max(value = 5, message = "La valutazione della comunicazione deve essere compresa tra 1 e 5")
    private double ratingCommunication;

    @NotNull(message = "La valutazione della puntualità è obbligatoria")
    @Min(value = 1, message = "La valutazione della puntualità deve essere compresa tra 1 e 5")
    @Max(value = 5, message = "La valutazione della puntualità deve essere compresa tra 1 e 5")
    private double ratingTimeliness;

    @NotNull(message = "La valutazione del costo è obbligatoria")
    @Min(value = 1, message = "La valutazione del costo deve essere compresa tra 1 e 5")
    @Max(value = 5, message = "La valutazione del costo deve essere compresa tra 1 e 5")
    private double ratingCost;

    @NotNull(message = "L'ID del servizio è obbligatorio")
    @Min(value = 1, message = "L'ID del servizio deve essere maggiore di 0")
    private long productId;
}
