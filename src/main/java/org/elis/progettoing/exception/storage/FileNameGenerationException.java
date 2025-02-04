package org.elis.progettoing.exception.storage;

/**
 * Custom exception to report errors when generating a unique filename.
 */
public class FileNameGenerationException extends RuntimeException {

    public FileNameGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
