package org.elis.progettoing.dto.request.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for editing user information.
 * This DTO is used when a user submits a request to edit their profile,
 * including personal details such as name, surname, nickname, birthdate, skills, and education.
 * The request also includes a biography and languages spoken by the user.
 * All fields are validated to ensure proper submission of user information.
 */
@Data
public class UserEditRequest {

    @NotEmpty(message = "Il nome non può essere vuoto.")
    @Size(max = 30, message = "Il nome non può superare i 30 caratteri.")
    private String name;

    @NotEmpty(message = "Il cognome non può essere vuoto.")
    @Size(max = 30, message = "Il cognome non può superare i 30 caratteri.")
    private String surname;

    @NotEmpty(message = "Il nickname non può essere vuoto.")
    @Size(max = 20, message = "Il nickname non può superare i 20 caratteri.")
    private String nickname;

    @Past(message = "La data di nascita deve essere nel passato.")
    private LocalDate birthDate;

    @Size(max = 750, message = "L'educazione non può superare i 750 caratteri.")
    private String education;

    @Size(max = 1000, message = "La biografia non può superare i 1000 caratteri.")
    private String bio;

    private List<@Size(max = 50, message = "La singola competenza non può superare i 50 caratteri.") String> languages = new ArrayList<>();

    private List<@Size(max = 50, message = "La singola competenza non può superare i 50 caratteri.") String> skills = new ArrayList<>();
}
