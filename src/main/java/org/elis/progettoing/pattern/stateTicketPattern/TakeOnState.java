package org.elis.progettoing.pattern.stateTicketPattern;

import org.elis.progettoing.models.Ticket;
import org.elis.progettoing.service.definition.EmailService;

/**
 * This class represents the "Take On" state for a Ticket in the StateTicketPattern.
 * When a Ticket transitions to this state, it indicates that the ticket is being actively worked on.
 * In this state, a "Take On" email is sent to the user who submitted the ticket.
 */
public class TakeOnState implements StateTicket {
    private final EmailService emailService;

    /**
     * Constructs a TakeOnState object with the given EmailService dependency.
     *
     * @param emailService The service for sending emails.
     */
    public TakeOnState(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Handles the actions to be performed when a Ticket transitions to the Take On state.
     * This method sends a "Take On" email to the user who submitted the ticket.
     *
     * @param ticket The Ticket object to be handled.
     */
    @Override
    public void handle(Ticket ticket, String responseDescriptionEmail) {
        emailService.sendTakeOnEmail(ticket);
    }

    /**
     * Returns the status message for the Take On state.
     *
     * @return The status message, which is "In lavorazione" in this case.
     */
    @Override
    public String getStatusMessage() {
        return "In lavorazione";
    }
}