package org.elis.progettoing.exception;

/**
 * Custom exception for reporting errors in email sending.
 * Can be used to report errors in sending emails.
 */
public class EmailSendingException extends RuntimeException {

    public EmailSendingException(String message) {
        super(message);
    }
}
