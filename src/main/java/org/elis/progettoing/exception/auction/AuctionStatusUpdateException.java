package org.elis.progettoing.exception.auction;

/**
 * Exception thrown during an error in updating the status of an auction.
 */
public class AuctionStatusUpdateException extends AuctionException {

  public AuctionStatusUpdateException(String message, Throwable cause) {
    super("Errore nell'aggiornamento dello stato dell'asta: " + message, cause);
  }
}
