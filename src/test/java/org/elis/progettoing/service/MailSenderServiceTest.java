package org.elis.progettoing.service;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.elis.progettoing.exception.EmailSendingException;
import org.elis.progettoing.models.Email;
import org.elis.progettoing.service.implementation.MailSenderServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MailSenderServiceTest {
    private MailSenderServiceImpl mailSenderServiceImpl;
    private JavaMailSender mailSender;
    private String sender;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        sender = "sender@example.com";
        mailSenderServiceImpl = new MailSenderServiceImpl(mailSender);
        mailSenderServiceImpl.setSender(sender);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @CsvSource({
            "recipient@example.com, Test Subject, Test Body",
            "null, Test Subject, Test Body",
            "recipient@example.com, null, Test Body",
            "recipient@example.com, Test Subject, null"
    })
    void sendEmail_shouldHandleVariousInputs(String recipient, String subject, String body) throws Exception {
        Email email = new Email(recipient, subject, body);

        if (recipient == null) {
            assertThrows(EmailSendingException.class, () -> mailSenderServiceImpl.sendEmail(email));
        } else {
            mailSenderServiceImpl.sendEmail(email);

            ArgumentCaptor<MimeMessagePreparator> messageCaptor = ArgumentCaptor.forClass(MimeMessagePreparator.class);
            verify(mailSender).send(messageCaptor.capture());
            MimeMessagePreparator preparator = messageCaptor.getValue();

            MimeMessage mimeMessage = mock(MimeMessage.class);
            preparator.prepare(mimeMessage);

            verify(mimeMessage).setFrom(new InternetAddress(sender));
            verify(mimeMessage).setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            verify(mimeMessage).setSubject(subject, "UTF-8");
            verify(mimeMessage).setContent(any(MimeMultipart.class));
        }
    }

    @Test
    void sendEmail_shouldThrowEmailSendingException_whenMailSenderFails() {
        Email email = new Email("recipient@example.com", "Test Subject", "Test Body");

        doThrow(new RuntimeException("Mail sending failed")).when(mailSender).send(any(MimeMessagePreparator.class));

        assertThrows(EmailSendingException.class, () -> mailSenderServiceImpl.sendEmail(email));
    }
}