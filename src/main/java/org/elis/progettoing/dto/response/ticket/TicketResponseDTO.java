package org.elis.progettoing.dto.response.ticket;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing the response for a ticket.
 * This class provides details about a specific ticket, including its title,
 * description, state, type, and other relevant information.
 *
 * <p>The {@link TicketResponseDTO} is used to structure the information returned
 * when querying a ticket, including metadata such as creation date, priority flag,
 * and the ID of the user who requested the ticket.</p>
 */
@Data
public class TicketResponseDTO {

    private long id;

    private String title;

    private String description;

    private String state;

    private String type;

    private long userId;

    private String userName;

    private String userSurname;

    private String userPhoto;

    @JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private String creationDate;

    private String priorityFlag;
}
