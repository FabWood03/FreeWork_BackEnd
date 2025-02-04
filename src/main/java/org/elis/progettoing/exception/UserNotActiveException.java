package org.elis.progettoing.exception;

/**
 * Custom exception for reporting errors due to inactive users.
 * Can be used to report errors from inactive users.
 */
public class UserNotActiveException extends RuntimeException {

    public UserNotActiveException(String email) {
        super(String.format("Utente con email %s non è attivo", email));
    }
}
