package org.elis.progettoing.exception;

/**
 * Custom exception to report errors during JWT token generation.
 */
public class TokenGenerationException extends RuntimeException {

    public TokenGenerationException(String message) {
        super(message);
    }
}

