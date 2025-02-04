package org.elis.progettoing.exception.storage;

/**
 * Custom exception to report errors during storage system initialization.
 */
public class StorageInitializationException extends RuntimeException {

    public StorageInitializationException(String message) {
        super(message);
    }

    public StorageInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
