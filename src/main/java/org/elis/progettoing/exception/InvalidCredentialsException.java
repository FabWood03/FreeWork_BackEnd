package org.elis.progettoing.exception;

/**
 * Exception to indicate invalid authentication credentials.
 * Can be used to report login errors or failed authentication.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
