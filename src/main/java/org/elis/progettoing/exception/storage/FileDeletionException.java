package org.elis.progettoing.exception.storage;

/**
 * Custom exception to report errors when deleting a file.
 */
public class FileDeletionException extends RuntimeException {

    public FileDeletionException(String message) {
        super(message);
    }
}
