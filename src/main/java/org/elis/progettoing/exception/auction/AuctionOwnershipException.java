package org.elis.progettoing.exception.auction;

/**
 * Exception thrown when an impermissible operation is attempted on an auction by a non-owner user.
 */
public class AuctionOwnershipException extends AuctionException {

    public AuctionOwnershipException(long userId, long auctionId, String message) {
        super("L'utente con ID " + userId + " non è il proprietario dell'asta con ID " + auctionId + " e non può eseguire questa operazione: " + message);
    }

    public AuctionOwnershipException(String userEmail, long auctionId, String message) {
        super("L'utente con ID " + userEmail + " è il proprietario dell'asta con ID " + auctionId + " e non può eseguire questa operazione: " + message);
    }
}
