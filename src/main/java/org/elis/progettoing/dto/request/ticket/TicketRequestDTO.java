package org.elis.progettoing.dto.request.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for creating a new ticket request.
 * This DTO is used to collect the details of a new ticket, including its title, description,
 * state, type, and references to reported entities such as reviews, products, and users.
 *
 * The fields in this class are validated to ensure that they meet the necessary constraints
 * before being processed.
 */
@Data
public class TicketRequestDTO {

    @NotBlank(message = "Il titolo non può essere vuoto")
    @Size(max = 255, message = "Il titolo non può superare i 255 caratteri")
    private String title;

    @NotBlank(message = "La descrizione non può essere vuota")
    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    private String description;

    private long reportedReviewId;

    private long reportedProductId;

    private long reportedUserId;
}
