package org.elis.progettoing.pattern.stateTicketPattern;

import org.elis.progettoing.models.Ticket;
import org.elis.progettoing.service.definition.EmailService;

/**
 * This class represents the "Refused" state for a Ticket in the StateTicketPattern.
 * When a Ticket transitions to this state, a "Ticket Decision" email is sent to the user who submitted the ticket
 * to inform them that the ticket has been refused.
 */
public class RefuseState implements StateTicket {
    private final EmailService emailService;

    /**
     * Constructs a RefuseState object with the given EmailService dependency.
     *
     * @param emailService The service for sending emails.
     */
    public RefuseState(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Handles the actions to be performed when a Ticket transitions to the Refused state.
     * This method sends a "Ticket Decision" email to the user who submitted the ticket,
     * regardless of the ticket type.
     *
     * @param ticket The Ticket object to be handled.
     */
    @Override
    public void handle(Ticket ticket, String responseDescriptionEmail) {
        switch (ticket.getType()) {
            case REPORT_USER, REPORT_PRODUCT, SELLER_REQUEST, REPORT_REVIEWS ->
                    emailService.sendTicketDecisionEmail(ticket, false, responseDescriptionEmail);

        }
    }

    /**
     * Returns the status message for the Refused state.
     *
     * @return The status message, which is "Rifiutato" in this case.
     */
    @Override
    public String getStatusMessage() {
        return "Rifiutato";
    }
}