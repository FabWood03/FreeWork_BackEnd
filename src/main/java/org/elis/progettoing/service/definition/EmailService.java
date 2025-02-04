package org.elis.progettoing.service.definition;

import org.elis.progettoing.models.OrderProduct;
import org.elis.progettoing.models.Ticket;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;

public interface EmailService {
    void sendSellerRequestDemand(Ticket ticket);

    void sendReportConfirmation(Ticket ticket);

    void sendTicketDecisionEmail(Ticket ticket, boolean accepted, String responseDescriptionEmail);

    void sendTakeOnEmail(Ticket ticket);

    void sendUserBlockedEmail(User reportedUser);

    void sendSellerAlertEmail(Ticket ticket);

    void sendUserReviewAlertEmail(Ticket ticket);

    void sendAuctionOpenedEmail(Auction auction, User user);

    void sendAuctionClosedEmail(Auction auction, User user);

    void sendAuctionEndingSoonEmail(Auction auction, User user);

    void sendAuctionWinnerEmail(Auction auction, User user);

    void sendAuctionNotWinnerEmail(Auction auction, User user);

    void sendDeliveryConfirmationEmail(User user, OrderProduct orderProduct, String response);
}