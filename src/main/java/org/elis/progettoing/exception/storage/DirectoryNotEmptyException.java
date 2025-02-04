package org.elis.progettoing.exception.storage;

/**
 * Custom exception to report errors when deleting a directory that is not empty.
 */
public class DirectoryNotEmptyException extends RuntimeException {

    public DirectoryNotEmptyException(String message) {
        super(message);
    }
}
