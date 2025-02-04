package org.elis.progettoing.exception.storage;

/**
 * Custom exception to report errors when saving files.
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }
}
