package org.elis.progettoing.models;

import lombok.Data;

/**
 * Represents an email message.
 * <p>
 * An email message is a message that can be sent to a recipient.
 * </p>
 */
@Data
public class Email {
    private String recipient;
    private String subject;
    private String body;

    /**
     * Default constructor.
     */
    public Email() {}

    /**
     * Constructs a new email message with the specified recipient, subject, and body.
     *
     * @param recipient the recipient of the email
     * @param subject the subject of the email
     * @param body the body of the email
     */
    public Email(String recipient, String subject, String body) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }
}
