package org.elis.progettoing.pattern.observerPattern;

import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.service.definition.EmailService;

/**
 * EmailNotificationListener implements the AuctionListener interface and handles.
 * Email notifications for auction-related events.
 * Uses an email delivery service to notify registered users.
 */
public class EmailNotificationListener implements AuctionListener {
    private final EmailService emailService;

    /**
     * Constructor for EmailNotificationListener.
     *
     * @param emailService The email service used to notify users.
     */
    public EmailNotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Send an email notification to the user specified for the auction-related event.
     *
     * @param auction The auction for which the notification is being sent.
     * @param user The user to whom the notification is sent.
     * @param notifyType The type of notification, which determines the content of the email sent.
     * Accepted values are:
     * - “auctionOpened”: sends an email to notify that the auction is open.
     * - “auctionClosed”: sends an email to notify the closing of the auction.
     * - “auctionEndingSoon”: sends an email to notify that the auction is closing.
     * @throws IllegalArgumentException if the notification type is invalid.
     */
    @Override
    public void update(Auction auction, User user, String notifyType) {
        if (auction != null && user != null) {
            switch (notifyType) {
                case "auctionOpened" -> emailService.sendAuctionOpenedEmail(auction, user);
                case "auctionClosed" -> emailService.sendAuctionClosedEmail(auction, user);
                case "auctionEndingSoon" -> emailService.sendAuctionEndingSoonEmail(auction, user);
                case "auctionWinner" -> emailService.sendAuctionWinnerEmail(auction, user);
                case "auctionNotWinner" -> emailService.sendAuctionNotWinnerEmail(auction, user);
                default -> throw new IllegalArgumentException("Tipo di notifica non valido");
            }
        }
    }
}
