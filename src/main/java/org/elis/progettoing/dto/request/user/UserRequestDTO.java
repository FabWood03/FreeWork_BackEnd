package org.elis.progettoing.dto.request.user;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for user registration requests.
 * This DTO is used to collect and validate the user information during the registration process.
 * The fields are subject to various validation constraints to ensure the data meets the expected format.
 * The required fields include the user's name, surname, nickname, email, and password. Optional fields
 * such as birthdate, biography, and fiscal code are included as well.
 * This class provides all the necessary data for user registration while ensuring data integrity
 * and enforcing business rules.
 */
@Data
public class UserRequestDTO {

    @NotEmpty(message = "Il nome non può essere vuoto.")
    @Size(max = 30, message = "Il nome non può superare i 30 caratteri.")
    private String name;

    @NotEmpty(message = "Il cognome non può essere vuoto.")
    @Size(max = 30, message = "Il cognome non può superare i 30 caratteri.")
    private String surname;

    @NotEmpty(message = "Il nickname non può essere vuoto.")
    @Size(max = 20, message = "Il nickname non può superare i 20 caratteri.")
    private String nickname;

    @NotEmpty(message = "L'email non può essere vuota.")
    @Email(message = "L'email deve essere valida.")
    @Size(max = 30, message = "L'email non può superare i 30 caratteri.")
    private String email;

    @NotEmpty(message = "La password non può essere vuota.")
    @Size(min = 8, message = "La password deve contenere almeno 8 caratteri.")
    private String password;

    @Past(message = "La data di nascita deve essere nel passato.")
    private LocalDate birthDate;

    @Size(max = 750, message = "L'educazione non può superare i 750 caratteri.")
    private String education;

    private String role;

    private boolean active = true;

    private double ranking = 0.0;

    private long cartId;

    @Pattern(regexp = "^[A-Z0-9]{16}$", message = "Il codice fiscale deve essere valido.")
    private String fiscalCode;

    /**
     * Default constructor.
     */
    public UserRequestDTO() {
        // Default constructor
    }
}
