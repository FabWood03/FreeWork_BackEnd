package org.elis.progettoing.exception;

/**
 * Custom exception for reporting errors when a product is not owned by the user.
 */
public class InvalidProductOwnerException extends RuntimeException {

    public InvalidProductOwnerException(String message) {
        super(message);
    }
}
