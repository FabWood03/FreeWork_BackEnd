package org.elis.progettoing.dto.request.ticket;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for submitting a seller demand request.
 * This DTO is used when a user submits a request to become a seller,
 * including personal details such as title, description, birthdate, skills, and education.
 * The request also includes a fiscal code for validation purposes.
 * All fields are validated to ensure proper submission of seller information.
 */
@Data
public class DemandSellerRequestDTO {

    @NotEmpty(message = "Il titolo non può essere vuoto.")
    @Size(max = 100, message = "Il titolo non può superare i 100 caratteri.")
    private String title;

    @NotEmpty(message = "La descrizione non può essere vuota.")
    @Size(max = 750, message = "La descrizione non può superare i 1000 caratteri.")
    private String description;

    @Past(message = "La data di nascita deve essere nel passato.")
    private LocalDate birthDate;

    @Size(max = 750, message = "L'educazione non può superare i 750 caratteri.")
    private String education;

    @Pattern(regexp = "^[A-Z0-9]{16}$", message = "Il codice fiscale deve essere valido.")
    private String fiscalCode;

    @NotEmpty(message = "La sede non può essere vuota.")
    private String basedIn;

    @NotEmpty(message = "Le lingue non possono essere vuote.")
    private List<String> languages = new ArrayList<>();

    @NotEmpty(message = "Le competenze non possono essere vuote.")
    private List<@Size(max = 50, message = "La singola competenza non può superare i 50 caratteri.") String> skills;
}
