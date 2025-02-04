package org.elis.progettoing.service.implementation;

import lombok.Setter;
import org.elis.progettoing.exception.EmailSendingException;
import org.elis.progettoing.models.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementation of the MailSenderService interface. Provides methods for sending emails.
 */
@Service
public class MailSenderServiceImpl {
    private final JavaMailSender mailSender;

    @Setter
    @Value("${spring.mail.username}")
    private String sender;

    /**
     * Constructor for {@code MailSenderServiceImpl}.
     *
     * @param mailSender the component to send email.
     */
    public MailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send an email using the provided {@link Email} object.
     *
     * @param email the email object containing recipient, subject, and body.
     * @throws EmailSendingException if an error occurs while sending the email.
     */
    @Async
    public void sendEmail(Email email) {
        try {
            MimeMessagePreparator mailMessage = mimeMessage -> {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(sender);
                message.setTo(email.getRecipient());
                message.setSubject(email.getSubject());
                message.setText(email.getBody());
            };

            mailSender.send(mailMessage);
        } catch (Exception e) {
            throw new EmailSendingException("Errore nell'invio dell'email");
        }
    }

}
