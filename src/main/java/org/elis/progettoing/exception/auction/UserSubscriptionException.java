package org.elis.progettoing.exception.auction;

/**
 * Exception thrown during an auction notification entry error.
 */
public class UserSubscriptionException extends AuctionException {

  public UserSubscriptionException(String message) {
    super("Errore durante l'iscrizione alle notifiche: " + message);
  }
}
