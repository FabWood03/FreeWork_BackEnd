package org.elis.progettoing.exception;

/**
 * Custom exception to report errors while editing an active user.
 */
public class ActiveUserEditException extends RuntimeException {

    public ActiveUserEditException(String action, String propertyName, long value) {
        super(String.format("Si è verificato un errore durante il tentativo di %s l'utente con %s = %d.", action, propertyName, value));
    }
}
