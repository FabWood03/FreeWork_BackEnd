package org.elis.progettoing.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for user login requests.
 * This DTO is used to collect the login credentials of a user, including their email address
 * and password. The fields are validated to ensure they meet specific constraints before
 * being processed.
 *
 * The email is validated to ensure it is in a correct email format, and the password is
 * required to have a minimum length of 8 characters.
 */
@Data
public class UserLoginRequest {

    @NotBlank(message = "Email non può essere vuota")
    @Email(message = "Email non valida")
    @Size(min = 1, max = 50, message = "Email deve essere compreso tra 1 e 50 caratteri")
    private String email;

    @NotBlank(message = "Password non può essere vuota")
    @Size(min = 8, message = "Password errata")
    private String password;
}
