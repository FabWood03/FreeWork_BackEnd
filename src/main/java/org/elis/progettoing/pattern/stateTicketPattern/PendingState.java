package org.elis.progettoing.pattern.stateTicketPattern;

import org.elis.progettoing.models.Ticket;
import org.elis.progettoing.service.definition.EmailService;

/**
 * This class represents the "Pending" state for a Ticket in the StateTicketPattern.
 * When a Ticket transitions to this state, the appropriate notification emails are sent based on the ticket type.
 */
public class PendingState implements StateTicket {
    private final EmailService emailService;

    /**
     * Constructs a PendingState object with the given EmailService dependency.
     *
     * @param emailService The service for sending emails.
     */
    public PendingState(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Handles the actions to be performed when a Ticket transitions to the Pending state.
     * This method sends appropriate notification emails based on the ticket type:
     *
     * <ul>
     *     <li>REPORT_USER, REPORT_REVIEWS, REPORT_PRODUCT: Sends a "Report Confirmation" email to the user who submitted the ticket.</li>
     *     <li>SELLER_REQUEST: Sends a "Seller Request Demand" email to the user who submitted the ticket.</li>
     * </ul>
     *
     * @param ticket The Ticket object to be handled.
     * @throws IllegalArgumentException if the ticket type is null.
     */
    @Override
    public void handle(Ticket ticket, String responseDescriptionEmail) {

        switch(ticket.getType()){
            case REPORT_USER, REPORT_REVIEWS, REPORT_PRODUCT -> emailService.sendReportConfirmation(ticket);
            case SELLER_REQUEST -> emailService.sendSellerRequestDemand(ticket);
        }
    }

    /**
     * Returns the status message for the Pending state.
     *
     * @return The status message, which is "In attesa" in this case.
     */
    @Override
    public String getStatusMessage() {
        return "In attesa";
    }
}