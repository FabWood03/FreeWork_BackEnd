package org.elis.progettoing.exception.auction;

import org.elis.progettoing.enumeration.AuctionStatus;

/**
 * Exception thrown for an attempted change or action on an auction in an incompatible state.
 */
public class InvalidAuctionStateException extends AuctionException {

    public InvalidAuctionStateException(long auctionId, AuctionStatus currentState, AuctionStatus requiredState) {
        super("L'asta con ID " + auctionId + " è in stato " + currentState + ", ma è richiesta in stato " + requiredState + ".");
    }
}
