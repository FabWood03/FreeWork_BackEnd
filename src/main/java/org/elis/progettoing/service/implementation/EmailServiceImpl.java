package org.elis.progettoing.service.implementation;

import org.elis.progettoing.models.Email;
import org.elis.progettoing.models.OrderProduct;
import org.elis.progettoing.models.Ticket;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.service.definition.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Implementation of the email service that manages the sending of various types of email notifications to users.
 */
@Service
public class EmailServiceImpl implements EmailService {
    private final MailSenderServiceImpl emailService;

    public EmailServiceImpl(MailSenderServiceImpl emailService) {
        this.emailService = emailService;
    }


    /**
     * Sends a notification to the user that their ticket has been received.
     *
     * @param ticket the ticket that has been received.
     */
    @Override
    @Async
    public void sendSellerRequestDemand(Ticket ticket) {
        User user = ticket.getTicketRequester();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("Conferma della richiesta venditore");
        email.setBody(
                "Gentile " + user.getName() + " " + user.getSurname() + ",\n\n" +
                        "Ti informiamo che la tua richiesta per diventare venditore è stata ricevuta con successo.\n\n" +
                        "Di seguito trovi i dettagli della tua richiesta:\n" +
                        "- Numero della richiesta: " + ticket.getId() + "\n" +
                        "- Data di invio: " + ticket.getCreationDate().format(dateFormatter) + "\n" +
                        "- Descrizione: " + ticket.getDescription() + "\n\n" +
                        "Il nostro team ti contatterà il prima possibile per ulteriori informazioni.\n\n" +
                        "Grazie per la tua fiducia.\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork"
        );

        emailService.sendEmail(email);
    }

    /**
     * Sends a notification to the user that their ticket has been received.
     *
     * @param ticket the ticket that has been received.
     */
    @Override
    @Async
    public void sendReportConfirmation(Ticket ticket) {
        User user = ticket.getTicketRequester();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("Conferma della segnalazione ricevuta");
        email.setBody(
                "Gentile " + user.getName() + " " + user.getSurname() + ",\n\n" +
                        "Ti informiamo che la tua segnalazione è stata ricevuta con successo.\n\n" +
                        "Di seguito trovi i dettagli della tua segnalazione:\n" +
                        "- Numero della segnalazione: " + ticket.getId() + "\n" +
                        "- Data di invio: " + ticket.getCreationDate().format(dateFormatter) + "\n" +
                        "- Descrizione: " + ticket.getDescription() + "\n\n" +
                        "Grazie per il tuo prezioso contributo. Apprezziamo il tempo che hai dedicato per segnalarci questo problema.\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork"
        );

        emailService.sendEmail(email);
    }

    /**
     * Sends a notification to the user that their ticket has been received.
     *
     * @param ticket the ticket that has been received.
     */
    @Override
    @Async
    public void sendTicketDecisionEmail(Ticket ticket, boolean isAccepted, String responseDescriptionEmail) {
        User user = ticket.getTicketRequester();
        String decision = isAccepted ? "approvata" : "respinta";

        String ticketTypeMessage = switch (ticket.getType()) {
            case SELLER_REQUEST -> isAccepted
                    ? "Siamo lieti di comunicarti che la tua richiesta per diventare venditore è stata accolta. Benvenuto nella nostra rete di venditori!"
                    : "Purtroppo, la tua richiesta per diventare venditore non è stata accettata.";
            case REPORT_REVIEWS -> isAccepted
                    ? "La tua segnalazione riguardo a una recensione è stata verificata e accolta."
                    : "Dopo una verifica approfondita, la tua segnalazione riguardo a una recensione non è stata accettata.";
            case REPORT_USER -> isAccepted
                    ? "La tua segnalazione di un utente è stata verificata e confermata."
                    : "La tua segnalazione di un utente è stata respinta.";
            case REPORT_PRODUCT -> isAccepted
                    ? "Abbiamo accettato la tua segnalazione relativa a un prodotto."
                    : "La tua segnalazione relativa a un prodotto non è stata accolta.";
        };

        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("Aggiornamento sulla tua richiesta: " + decision);
        email.setBody(
                "Gentile " + user.getName() + " " + user.getSurname() + ",\n\n" +
                        "Ti informiamo che la tua richiesta (ID: " + ticket.getId() + ") è stata " + decision + ".\n\n" +
                        ticketTypeMessage + "\n\n" +
                        (responseDescriptionEmail != null ? "Motivazione: " + responseDescriptionEmail + "\n\n" : "") +
                        "Grazie per il tuo contributo.\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork"
        );

        emailService.sendEmail(email);
    }

    /**
     * Sends a notification to the user that their ticket has been taken on by a moderator.
     *
     * @param ticket the ticket that has been taken on.
     */
    @Override
    @Async
    public void sendTakeOnEmail(Ticket ticket) {
        User user = ticket.getTicketRequester();
        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("Richiesta presa in carico");
        email.setBody(
                "Gentile " + user.getName() + " " + user.getSurname() + ",\n\n" +
                        "Ti informiamo che la tua richiesta (ID: " + ticket.getId() + ") è stata presa in carico.\n\n" +
                        "Ti aggiorneremo non appena avremo nuove informazioni.\n\n" +
                        "Grazie per la tua pazienza.\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork"
        );

        emailService.sendEmail(email);
    }

    /**
     * Sends a notification to the user that the specified auction is open for bidding.
     *
     * @param auction the auction that is open for bidding.
     * @param user    the user to send the notification to.
     */
    @Override
    @Async
    public void sendAuctionOpenedEmail(Auction auction, User user) {
        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("L'asta \"" + auction.getTitle() + "\" è ora aperta!");
        email.setBody(
                "Gentile " + user.getName() + ",\n\n" +
                        "L'asta \"" + auction.getTitle() + "\" è ora aperta per le offerte!\n\n" +
                        "Ti aspettiamo!\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork"
        );

        emailService.sendEmail(email);
    }

    /**
     * Sends a notification to the user that the specified auction is ending soon.
     *
     * @param auction the auction that is ending soon.
     * @param user    the user to send the notification to.
     */
    @Override
    @Async
    public void sendAuctionEndingSoonEmail(Auction auction, User user) {
        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("L'asta \"" + auction.getTitle() + "\" sta per chiudere!");
        email.setBody(
                "Gentile " + user.getName() + ",\n\n" +
                        "Questa è un'ultima occasione per fare la tua offerta nell'asta \"" + auction.getTitle() + "\".\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork"
        );

        emailService.sendEmail(email);
    }

    @Override
    @Async
    public void sendAuctionWinnerEmail(Auction auction, User user) {
        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("Congratulazioni! Hai vinto l'asta \"" + auction.getTitle() + "\"!");
        email.setBody(
                "Gentile " + user.getName() + " " + user.getSurname() + ",\n\n" +
                        "Congratulazioni! Hai vinto l'asta \"" + auction.getTitle() + "\"!\n\n" +
                        "Per ulteriori informazioni, contatta il venditore.\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork"
        );

        emailService.sendEmail(email);
    }

    @Override
    @Async
    public void sendAuctionNotWinnerEmail(Auction auction, User user) {
        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("L'asta \"" + auction.getTitle() + "\" è stata chiusa!");
        email.setBody(
                "Gentile " + user.getName() + " " + user.getSurname() + ",\n\n" +
                        "Ti informiamo che l'asta \"" + auction.getTitle() + "\" è stata chiusa.\n\n" +
                        "Purtroppo, non hai vinto l'asta. Continua a partecipare per avere altre possibilità!\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork"
        );

        emailService.sendEmail(email);
    }

    @Override
    @Async
    public void sendDeliveryConfirmationEmail(User user, OrderProduct orderProduct, String response) {
        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("Conferma di consegna per il tuo ordine");
        email.setBody(
                "Gentile " + user.getName() + " " + user.getSurname() + ",\n\n" +
                        "La consegna del prodotto \"" + orderProduct.getProduct().getTitle() + "\" è stata confermata. Di seguito trovi i dettagli sulle modalità di consegna:\n\n" +
                        response + "\n\n" +
                        "Grazie per aver scelto FreeWork. Se hai domande o necessiti ulteriori informazioni, non esitare a contattarci.\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork"
        );

        emailService.sendEmail(email);
    }

    /**
     * Sends a notification to the user that the specified auction has been closed.
     *
     * @param auction the auction that has been closed.
     * @param user    the user to send the notification to.
     */
    @Override
    @Async
    public void sendAuctionClosedEmail(Auction auction, User user) {
        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("L'asta \"" + auction.getTitle() + "\" è stata chiusa!");
        email.setBody(
                "Gentile " + user.getName() + " " + user.getSurname() + ",\n\n" +
                        "ti informiamo che l'asta \"" + auction.getTitle() + "\" è stata chiusa! \n" +
                        "Corri a vedere se hai vinto!.\n\n" +
                        "Ti ringraziamo per il tuo interesse e ti invitiamo a partecipare alle prossime occasioni!\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork"
        );

        emailService.sendEmail(email);
    }

    /**
     * Sends a notification to the user that their account has been locked.
     *
     * @param user the user whose account has been locked.
     */
    @Override
    @Async
    public void sendUserBlockedEmail(User user) {
        Email email = new Email();
        email.setRecipient(user.getEmail());
        email.setSubject("Il tuo account è stato bloccato");
        email.setBody(
                "Gentile " +
                        user.getName() + " " +
                        user.getSurname() + ",\n\n" +
                        "Ti informiamo che il tuo account è stato bloccato.\n\n" +
                        "Se ritieni che si tratti di un errore, ti preghiamo di contattarci.\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork"
        );

        emailService.sendEmail(email);
    }

    /**
     * Sends a notification to the reported user that their non-compliant product has been removed.
     *
     * @param ticket the ticket that contains the details of the report.
     */
    @Override
    @Async
    public void sendSellerAlertEmail(Ticket ticket) {
        Email email = new Email();
        email.setRecipient(ticket.getReportedProduct().getUser().getEmail());
        email.setSubject("Attenzione: Prodotto non conforme eliminato");
        email.setBody(
                "Gentile " + ticket.getReportedProduct().getUser().getName() + " " + ticket.getReportedProduct().getUser().getSurname() + ",\n\n" +
                        "Ti informiamo che il prodotto con titolo \"" + ticket.getReportedProduct().getTitle() + "\" è stato eliminato.\n\n" +
                        "Cordiali saluti,\n" +
                        "Il team di FreeWork"
        );

        emailService.sendEmail(email);
    }

    /**
     * Sends a notification to the user that their review for a product has been removed due to a possible policy violation.
     *
     * @param ticket the ticket containing the details of the review report.
     */
    @Override
    @Async
    public void sendUserReviewAlertEmail(Ticket ticket) {
        Email email = new Email();
        email.setRecipient(ticket.getReportedReview().getUser().getEmail());
        email.setSubject("Attenzione: Prodotto non conforme eliminato");
        email.setBody(
                "Gentile " + ticket.getTicketRequester().getName() + " " + ticket.getTicketRequester().getSurname() + ",\n\n"
                        + "La tua recensione per il prodotto \"" + ticket.getReportedReview().getProduct().getTitle() + "\" è stata segnalata e abbiamo deciso di eliminarla per una possibile violazione delle nostre politiche.\n"
                        + "Dettagli della recensione eliminata:\n"
                        + " - Commento: \"" + ticket.getReportedReview().getComment() + "\"\n"
                        + " - Data: " + ticket.getReportedReview().getDateCreation() + "\n\n"
                        + "Al momento, non saranno presi ulteriori provvedimenti nei tuoi confronti. Tuttavia, ti invitiamo a prestare maggiore attenzione in futuro. Ulteriori violazioni potrebbero comportare conseguenze più severe.\n\n"
                        + "Numero del ticket: " + ticket.getId() + "\n\n"
                        + "Grazie per la tua attenzione e comprensione.\n\n"
                        + "Cordiali saluti,\n"
                        + "Il team di moderazione"
        );

        emailService.sendEmail(email);
    }
}