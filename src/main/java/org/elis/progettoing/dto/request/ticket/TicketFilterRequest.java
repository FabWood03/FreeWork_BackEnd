package org.elis.progettoing.dto.request.ticket;

import lombok.Data;
import org.elis.progettoing.enumeration.TicketType;

import java.util.List;

/**
 * Data Transfer Object (DTO) used to filter ticket requests.
 * This DTO is used for specifying various filters when querying ticket data.
 * It includes options for filtering by ticket type, priority, search text, date range, and sorting options.
 * All fields are optional and can be used together to refine the ticket query.
 */
@Data
public class TicketFilterRequest {
    private List<TicketType> ticketTypes;

    private String priority;

    private String searchText;

    private String dateRangeType;

    private String sortByCreationDate;

}
