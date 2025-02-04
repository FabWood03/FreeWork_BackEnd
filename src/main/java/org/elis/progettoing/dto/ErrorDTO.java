package org.elis.progettoing.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for representing error responses.
 * This DTO is used to structure the details of an error response returned from the server.
 * It includes information about the timestamp of the error, HTTP status code, the error message,
 * additional details about the message, and the request path that triggered the error.
 */
@Data
public class ErrorDTO {

    private LocalDateTime timestamp;

    private int status;

    String error;

    Object message;

    String path;

    /**
     * Constructor for creating an ErrorDTO instance.
     *
     * @param now the timestamp of the error occurrence.
     * @param value the HTTP status code for the error response.
     * @param reasonPhrase a brief description of the error.
     * @param message additional details or message about the error.
     * @param path the path of the request that caused the error.
     */
    public ErrorDTO(LocalDateTime now, int value, String reasonPhrase, Object message, String path) {
        this.timestamp = now;
        this.status = value;
        this.error = reasonPhrase;
        this.message = message;
        this.path = path;
    }
}
