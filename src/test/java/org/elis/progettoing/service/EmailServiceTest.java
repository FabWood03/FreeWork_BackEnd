package org.elis.progettoing.service;

import org.elis.progettoing.enumeration.TicketType;
import org.elis.progettoing.models.Email;
import org.elis.progettoing.models.Review;
import org.elis.progettoing.models.Ticket;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.service.implementation.EmailServiceImpl;
import org.elis.progettoing.service.implementation.MailSenderServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class EmailServiceTest {
    @InjectMocks
    private EmailServiceImpl emailServiceImpl;

    @Mock
    private MailSenderServiceImpl mailSenderService;
    @Mock
    private Ticket ticket;

    private User user;
    private Auction auction;
    @Mock
    private Product reportedProduct;

    @Mock
    private User seller;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setName("Giulia");
        user.setEmail("giulia@example.com");

        auction = new Auction();
        auction.setTitle("Asta speciale");

        seller = new User();
        seller.setName("Mario");
        seller.setSurname("Rossi");
        seller.setEmail("mario.rossi@example.com");

        reportedProduct = new Product();
        reportedProduct.setTitle("Prodotto difettoso");
        reportedProduct.setUser(seller);

        ticket = new Ticket();
        ticket.setReportedProduct(reportedProduct);
    }


    @Test
    void testSendSellerRequestDemand() {
        // Preparazione dati
        User user = new User();
        user.setName("Mario");
        user.setSurname("Rossi");
        user.setEmail("mario.rossi@example.com");

        Ticket ticket = new Ticket();
        ticket.setId(123L);
        ticket.setTicketRequester(user);
        ticket.setCreationDate(LocalDateTime.now());
        ticket.setDescription("Richiesta di diventare venditore");

        // Esecuzione del metodo
        emailServiceImpl.sendSellerRequestDemand(ticket);

        // Verifica chiamata
        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService, times(1)).sendEmail(emailCaptor.capture());

        Email email = emailCaptor.getValue();
        assertEquals("mario.rossi@example.com", email.getRecipient());
        assertEquals("Conferma della richiesta venditore", email.getSubject());
        assertTrue(email.getBody().contains("Mario Rossi"));
        assertTrue(email.getBody().contains("Richiesta di diventare venditore"));
    }

    @Test
    void testSendReportConfirmation() {
        // Preparazione dati
        User user = new User();
        user.setName("Anna");
        user.setSurname("Bianchi");
        user.setEmail("anna.bianchi@example.com");

        Ticket ticket = new Ticket();
        ticket.setId(456L);
        ticket.setTicketRequester(user);
        ticket.setCreationDate(LocalDateTime.now());
        ticket.setDescription("Segnalazione su un prodotto");

        // Esecuzione del metodo
        emailServiceImpl.sendReportConfirmation(ticket);

        // Verifica chiamata
        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService, times(1)).sendEmail(emailCaptor.capture());

        Email email = emailCaptor.getValue();
        assertEquals("anna.bianchi@example.com", email.getRecipient());
        assertEquals("Conferma della segnalazione ricevuta", email.getSubject());
        assertTrue(email.getBody().contains("Anna Bianchi"));
        assertTrue(email.getBody().contains("Segnalazione su un prodotto"));
    }

    @Test
    void sendAuctionNotWinnerEmail_shouldSendEmailWithCorrectContent() {
        emailServiceImpl.sendAuctionNotWinnerEmail(auction, user);

        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService).sendEmail(emailCaptor.capture());
        Email sentEmail = emailCaptor.getValue();

        assertEquals("giulia@example.com", sentEmail.getRecipient());
        assertEquals("L'asta \"Asta speciale\" è stata chiusa!", sentEmail.getSubject());
    }

    @Test
    void sendAuctionWinnerEmail_shouldSendEmailWithCorrectContent() {
        emailServiceImpl.sendAuctionWinnerEmail(auction, user);

        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService).sendEmail(emailCaptor.capture());
        Email sentEmail = emailCaptor.getValue();

        assertEquals("giulia@example.com", sentEmail.getRecipient());
        assertEquals("Congratulazioni! Hai vinto l'asta \"Asta speciale\"!", sentEmail.getSubject());
    }

    @Test
    void testSendAuctionOpenedEmail() {
        emailServiceImpl.sendAuctionOpenedEmail(auction, user);

        // Verifica chiamata
        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService, times(1)).sendEmail(emailCaptor.capture());

        Email email = emailCaptor.getValue();
        assertEquals("giulia@example.com", email.getRecipient());
        assertEquals("L'asta \"Asta speciale\" è ora aperta!", email.getSubject());
        assertTrue(email.getBody().contains("Asta speciale"));
    }


    @Test
    void testSendUserBlockedEmail() {
        // Preparazione dati
        User user = new User();
        user.setName("Luca");
        user.setSurname("Verdi");
        user.setEmail("luca.verdi@example.com");

        // Esecuzione del metodo
        emailServiceImpl.sendUserBlockedEmail(user);

        // Verifica chiamata
        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService, times(1)).sendEmail(emailCaptor.capture());

        Email email = emailCaptor.getValue();
        assertEquals("luca.verdi@example.com", email.getRecipient());
        assertEquals("Il tuo account è stato bloccato", email.getSubject());
        assertTrue(email.getBody().contains("Luca Verdi"));
        assertTrue(email.getBody().contains("bloccato"));
    }

    @Test
    void testSendTicketDecisionEmailAccepted() {
        // Arrange
        Ticket ticket = createMockTicket(TicketType.SELLER_REQUEST, 1L, "Richiesta per diventare venditore");
        String responseDescriptionEmail = "Congratulazioni! Sei stato approvato.";
        boolean isAccepted = true;

        // Act
        emailServiceImpl.sendTicketDecisionEmail(ticket, isAccepted, responseDescriptionEmail);

        // Assert
        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService, times(1)).sendEmail(emailCaptor.capture());

        Email sentEmail = emailCaptor.getValue();
        assertEquals(ticket.getTicketRequester().getEmail(), sentEmail.getRecipient());
        assertEquals("Aggiornamento sulla tua richiesta: approvata", sentEmail.getSubject());
        assertEquals(
                """
                        Gentile John Doe,
                        
                        Ti informiamo che la tua richiesta (ID: 1) è stata approvata.
                        
                        Siamo lieti di comunicarti che la tua richiesta per diventare venditore è stata accolta. Benvenuto nella nostra rete di venditori!
                        
                        Motivazione: Congratulazioni! Sei stato approvato.
                        
                        Grazie per il tuo contributo.
                        
                        Cordiali saluti,
                        Il team di FreeWork""",
                sentEmail.getBody()
        );
    }

    @Test
    void testSendTicketDecisionEmailWithoutResponseDescription() {
        // Arrange
        Ticket ticket = createMockTicket(TicketType.REPORT_PRODUCT, 3L, "Segnalazione di prodotto non conforme");
        boolean isAccepted = true;

        // Act
        emailServiceImpl.sendTicketDecisionEmail(ticket, isAccepted, null);

        // Assert
        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService, times(1)).sendEmail(emailCaptor.capture());

        Email sentEmail = emailCaptor.getValue();
        assertEquals(ticket.getTicketRequester().getEmail(), sentEmail.getRecipient());
        assertEquals("Aggiornamento sulla tua richiesta: approvata", sentEmail.getSubject());
        assertEquals(
                """
                        Gentile John Doe,
                        
                        Ti informiamo che la tua richiesta (ID: 3) è stata approvata.
                        
                        Abbiamo accettato la tua segnalazione relativa a un prodotto.
                        
                        Grazie per il tuo contributo.
                        
                        Cordiali saluti,
                        Il team di FreeWork""",
                sentEmail.getBody()
        );
    }

    // Helper method to create a mock ticket
    private Ticket createMockTicket(TicketType type, Long id, String description) {
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("johndoe@example.com");

        ticket.setId(id);
        ticket.setType(type);
        ticket.setTicketRequester(user);
        ticket.setDescription(description);

        return ticket;
    }

    private Ticket createMockTicket(TicketType type, Long id, String description, User user) {
        ticket.setType(type);
        ticket.setId(id);
        ticket.setDescription(description);
        ticket.setTicketRequester(user);
        return ticket;
    }

    private User createMockUser(String name, String surname, String email) {
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        return user;
    }

    private void verifyEmailContent(String expectedRecipient, String expectedSubject, String expectedBody) {
        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService, times(1)).sendEmail(emailCaptor.capture());

        Email sentEmail = emailCaptor.getValue();
        assertEquals(expectedRecipient, sentEmail.getRecipient());
        assertEquals(expectedSubject, sentEmail.getSubject());
        assertEquals(expectedBody, sentEmail.getBody());
    }

    @Test
    void testSendTicketDecisionEmail() {
        User mockUser = createMockUser("John", "Doe", "johndoe@example.com");

        TicketType[] ticketTypes = TicketType.values();
        for (TicketType type : ticketTypes) {
            // Test both accepted and refused cases
            for (boolean isAccepted : new boolean[]{true, false}) {
                String responseDescriptionEmail = isAccepted
                        ? "Grazie per il tuo contributo. La richiesta è stata accolta."
                        : "Dopo una verifica approfondita, la tua richiesta non è stata accettata.";
                Ticket ticket = createMockTicket(type, 123L, "Test description", mockUser);

                emailServiceImpl.sendTicketDecisionEmail(ticket, isAccepted, responseDescriptionEmail);

                String decision = isAccepted ? "approvata" : "respinta";
                String ticketTypeMessage;
                switch (type) {
                    case SELLER_REQUEST -> ticketTypeMessage = isAccepted
                            ? "Siamo lieti di comunicarti che la tua richiesta per diventare venditore è stata accolta. Benvenuto nella nostra rete di venditori!"
                            : "Purtroppo, la tua richiesta per diventare venditore non è stata accettata.";
                    case REPORT_REVIEWS -> ticketTypeMessage = isAccepted
                            ? "La tua segnalazione riguardo a una recensione è stata verificata e accolta."
                            : "Dopo una verifica approfondita, la tua segnalazione riguardo a una recensione non è stata accettata.";
                    case REPORT_USER -> ticketTypeMessage = isAccepted
                            ? "La tua segnalazione di un utente è stata verificata e confermata."
                            : "La tua segnalazione di un utente è stata respinta.";
                    case REPORT_PRODUCT -> ticketTypeMessage = isAccepted
                            ? "Abbiamo accettato la tua segnalazione relativa a un prodotto."
                            : "La tua segnalazione relativa a un prodotto non è stata accolta.";
                    default -> throw new IllegalArgumentException("Unexpected ticket type: " + type);
                }

                String expectedBody =
                        "Gentile " + mockUser.getName() + " " + mockUser.getSurname() + ",\n\n" +
                                "Ti informiamo che la tua richiesta (ID: " + ticket.getId() + ") è stata " + decision + ".\n\n" +
                                ticketTypeMessage + "\n\n" +
                                (responseDescriptionEmail != null ? "Motivazione: " + responseDescriptionEmail + "\n\n" : "") +
                                "Grazie per il tuo contributo.\n\n" +
                                "Cordiali saluti,\n" +
                                "Il team di FreeWork";

                verifyEmailContent(mockUser.getEmail(), "Aggiornamento sulla tua richiesta: " + decision, expectedBody);

                // Reset the mock to ensure no interference with subsequent cases
                reset(mailSenderService);
            }
        }
    }

    @Test
    void testSendTakeOnEmail() {
        // Arrange
        User mockUser = createMockUser("John", "Doe", "johndoe@example.com");
        Ticket mockTicket = new Ticket();
        mockTicket.setId(123L);
        mockTicket.setTicketRequester(mockUser);


        // Act
        emailServiceImpl.sendTakeOnEmail(mockTicket);

        // Assert
        String expectedBody =
                "Gentile " + mockUser.getName() + " " + mockUser.getSurname() + ",\n\n" +
                        "Ti informiamo che la tua richiesta (ID: " + mockTicket.getId() + ") è stata presa in carico.\n\n" +
                        "Ti aggiorneremo non appena avremo nuove informazioni.\n\n" +
                        "Grazie per la tua pazienza.\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork";

        verifyEmailContent(mockUser.getEmail(), "Richiesta presa in carico", expectedBody);
    }

    @Test
    void testSendAuctionEndingSoonEmail() {
        // Call the method to test
        emailServiceImpl.sendAuctionEndingSoonEmail(auction, user);

        // Capture the sent email
        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService, times(1)).sendEmail(emailCaptor.capture());

        // Get the captured email
        Email sentEmail = emailCaptor.getValue();

        // Assertions
        assertEquals("giulia@example.com", sentEmail.getRecipient());
        assertEquals("L'asta \"Asta speciale\" sta per chiudere!", sentEmail.getSubject());
        assertEquals(
                "Gentile Giulia,\n\n" +
                        "Questa è un'ultima occasione per fare la tua offerta nell'asta \"Asta speciale\".\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork",
                sentEmail.getBody()
        );
    }

    @Test
    void testSendAuctionClosedEmail() {
        user = new User();
        user.setName("Giulia");
        user.setSurname("Rossi");
        user.setEmail("giulia.rossi@example.com");

        auction = new Auction();
        auction.setTitle("Asta speciale");

        // Call the method to test
        emailServiceImpl.sendAuctionClosedEmail(auction, user);

        // Capture the sent email
        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService, times(1)).sendEmail(emailCaptor.capture());

        // Get the captured email
        Email sentEmail = emailCaptor.getValue();

        // Assertions
        assertEquals("giulia.rossi@example.com", sentEmail.getRecipient());
        assertEquals("L'asta \"Asta speciale\" è stata chiusa!", sentEmail.getSubject());
        assertEquals(
                "Gentile Giulia Rossi,\n\n" +
                        "ti informiamo che l'asta \"Asta speciale\" è stata chiusa! \n" +
                        "Corri a vedere se hai vinto!.\n\n" +
                        "Ti ringraziamo per il tuo interesse e ti invitiamo a partecipare alle prossime occasioni!\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork",
                sentEmail.getBody()
        );
    }

    @Test
    void testSendSellerAlertEmail() {
        // Call the method to test
        emailServiceImpl.sendSellerAlertEmail(ticket);

        // Capture the sent email
        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService, times(1)).sendEmail(emailCaptor.capture());

        // Get the captured email
        Email sentEmail = emailCaptor.getValue();

        // Assertions
        assertEquals("mario.rossi@example.com", sentEmail.getRecipient());
        assertEquals("Attenzione: Prodotto non conforme eliminato", sentEmail.getSubject());
        assertEquals(
                "Gentile Mario Rossi,\n\n" +
                        "Ti informiamo che il prodotto con titolo \"Prodotto difettoso\" è stato eliminato.\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork",
                sentEmail.getBody()
        );
    }

    @Test
    void testSendUserReviewAlertEmail() {
        Review reportedReview = new Review();
        reportedReview.setComment("Recensione negativa");
        reportedReview.setDateCreation(LocalDateTime.now());
        reportedReview.setUser(user);
        reportedReview.setProduct(reportedProduct);

        Ticket ticket = new Ticket();
        ticket.setId(123L);
        ticket.setTicketRequester(user);
        ticket.setReportedReview(reportedReview);
        ticket.setCreationDate(LocalDateTime.now());
        ticket.setDescription("Segnalazione di recensione");

        // Call the method to test
        emailServiceImpl.sendUserReviewAlertEmail(ticket);

        // Capture the sent email
        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(mailSenderService, times(1)).sendEmail(emailCaptor.capture());

        // Get the captured email
        Email sentEmail = emailCaptor.getValue();

        // Assertions
        assertEquals(ticket.getTicketRequester().getEmail(), sentEmail.getRecipient());
        assertEquals("Attenzione: Prodotto non conforme eliminato", sentEmail.getSubject());
    }
}
