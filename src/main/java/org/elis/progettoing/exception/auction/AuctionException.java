package org.elis.progettoing.exception.auction;

/**
 * General exception for errors related to auction transactions.
 */
public class AuctionException extends RuntimeException {

    public AuctionException(String message) {
        super(message);
    }

    public AuctionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuctionException(long userId) {
        super("Errore durante l'operazione sull'asta per l'utente con ID: " + userId);
    }
}