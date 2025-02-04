package org.elis.progettoing.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an authentication token.
 * This DTO is used to encapsulate a JWT (JSON Web Token) for authentication purposes in the application.
 */
@Data
public class TokenDTO {

    private String token;

    /**
     * Constructor for creating a TokenDTO instance.
     *
     * @param token the authentication token to be encapsulated in the DTO.
     */
    public TokenDTO(String token) {
        this.token = token;
    }
}
