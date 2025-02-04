package org.elis.progettoing.exception.storage;

/**
 * Custom exception to report errors when a file is not found.
 */
public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(String message) {
        super(message);
    }
}
