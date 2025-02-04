package org.elis.progettoing.pattern.stateTicketPattern;

import org.elis.progettoing.models.Ticket;

/**
 * This interface defines the contract for all state objects in the StateTicketPattern.
 *
 * <p>StateTicket objects represent different states that a Ticket can be in (e.g., Pending, Accepted, Refused).</p>
 *
 * <p>This interface provides methods for:</p>
 * <ul>
 *     <li>handle(Ticket ticket): This method defines the actions to be performed when a Ticket transitions to this state.</li>
 *     <li>getStatusMessage(): This method returns a human-readable string representing the status of the Ticket in this state.</li>
 * </ul>
 */
public interface StateTicket {

    /**
     * Handles the actions to be performed when a Ticket transitions to this state.
     *
     * @param ticket The Ticket object to be handled.
     */
    void handle(Ticket ticket, String responseDescriptionEmail);

    /**
     * Returns a human-readable string representing the status of the Ticket in this state.
     *
     * @return The status message for the current state.
     */
    String getStatusMessage();
}