package org.elis.progettoing.pattern.observerPattern;

import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;

/**
 * The AuctionListener interface represents an observer in the Observer pattern.
 * for auction-related events. Implementors of this interface
 * will receive notifications when certain events occur on auctions.
 */
public interface AuctionListener {

    /**
     * Method called to notify an auction-related event.
     *
     * @param auction The auction associated with the event.
     * @param user The user who will receive the event notification.
     * @param notifyType The type of event that triggered the notification.
     * Some examples of notifications may include:
     * - “auctionOpened”: notifies that the auction has been opened.
     * - “auctionClosed”: notifies the closing of the auction.
     * - “auctionEndingSoon”: notifies that the auction is about to close.
     */
    void update(Auction auction, User user, String notifyType);
}