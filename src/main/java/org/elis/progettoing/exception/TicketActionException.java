package org.elis.progettoing.exception;

/**
 * Custom exception to report errors in the execution of actions on a ticket.
 * Can be used to report errors in execution of invalid actions on a ticket.
 */
public class TicketActionException extends RuntimeException {

    public TicketActionException(long ticketId, String action, String currentState) {
        super(String.format("Errore nell'esecuzione dell'azione '%s' per il ticket con ID: %d. Stato attuale: '%s'.", action, ticketId, currentState));
    }
}
