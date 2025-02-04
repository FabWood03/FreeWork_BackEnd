package org.elis.progettoing.pattern.stateTicketPattern;

import org.elis.progettoing.models.Ticket;
import org.elis.progettoing.service.definition.EmailService;
import org.elis.progettoing.service.definition.ProductService;
import org.elis.progettoing.service.definition.ReviewService;
import org.elis.progettoing.service.definition.UserService;
import org.springframework.stereotype.Component;

/**
 * This class represents the "Accepted" state for a Ticket in the StateTicketPattern.
 * When a Ticket transitions to this state, the appropriate actions are taken based on the ticket type.
 */
@Component
public class AcceptedState implements StateTicket {

    private final EmailService emailService;
    private final UserService userService;
    private final ProductService productService;
    private final ReviewService reviewService;

    /**
     * Constructs an AcceptedState object with dependencies on EmailService, UserService, ProductService, and ReviewService.
     *
     * @param emailService   The service for sending emails.
     * @param userService    The service for managing user data.
     * @param productService The service for managing product data.
     * @param reviewService  The service for managing review data.
     */
    public AcceptedState(EmailService emailService, UserService userService, ProductService productService, ReviewService reviewService) {
        this.emailService = emailService;
        this.userService = userService;
        this.productService = productService;
        this.reviewService = reviewService;
    }

    /**
     * Handles the actions to be performed when a Ticket transitions to the Accepted state.
     *
     * <p>This method performs different actions based on the ticket type:</p>
     * <ul>
     *     <li>REPORT_USER:
     *         <ul>
     *             <li>Disables the reported user.</li>
     *             <li>Sends a "User Blocked" email to the reported user.</li>
     *             <li>Sends a "Ticket Decision" email to the user who submitted the ticket.</li>
     *         </ul>
     *     </li>
     *     <li>SELLER_REQUEST:
     *         <ul>
     *             <li>Sends a "Ticket Decision" email to the user who submitted the ticket.</li>
     *         </ul>
     *     </li>
     *     <li>REPORT_PRODUCT:
     *         <ul>
     *             <li>Removes the reported product.</li>
     *             <li>Sends a "Seller Alert" email to the seller of the product.</li>
     *             <li>Sends a "Ticket Decision" email to the user who submitted the ticket.</li>
     *         </ul>
     *     </li>
     *     <li>REPORT_REVIEWS:
     *         <ul>
     *             <li>Deletes the reported review.</li>
     *             <li>Sends a "Ticket Decision" email to the user who submitted the ticket.</li>
     *             <li>Sends a "User Review Alert" email to the user who wrote the review.</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param ticket The Ticket object to be handled.
     */
    @Override
    public void handle(Ticket ticket, String responseDescriptionEmail) {

        switch (ticket.getType()) {
            case REPORT_USER -> {
                userService.disableById(ticket.getReportedUser().getId());
                emailService.sendUserBlockedEmail(ticket.getReportedUser());
                emailService.sendTicketDecisionEmail(ticket, true, responseDescriptionEmail);
            }
            case SELLER_REQUEST -> emailService.sendTicketDecisionEmail(ticket, true, responseDescriptionEmail);
            case REPORT_PRODUCT -> {
                productService.removeProduct(ticket.getReportedProduct().getId());
                emailService.sendSellerAlertEmail(ticket);
                emailService.sendTicketDecisionEmail(ticket, true, responseDescriptionEmail);
            }
            case REPORT_REVIEWS -> {
                reviewService.deleteReview(ticket.getReportedReview().getId());
                emailService.sendTicketDecisionEmail(ticket, true, responseDescriptionEmail);
                emailService.sendUserReviewAlertEmail(ticket);
            }
        }
    }

    /**
     * Returns the status message for the Accepted state.
     *
     * @return The status message, which is "Accettato" in this case.
     */
    @Override
    public String getStatusMessage() {
        return "Accettato";
    }
}