package org.elis.progettoing.models;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.enumeration.PriorityFlag;
import org.elis.progettoing.enumeration.TicketType;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.pattern.stateTicketPattern.StateTicket;

import java.time.LocalDateTime;

/**
 * Represents a ticket.
 * <p>
 * A ticket is a request made by a user to report a problem or ask for help.
 * </p>
 */
@Data
@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "ticket_requester_id")
    private User ticketRequester;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "description", length = 750)
    private String description;

    @Enumerated(EnumType.STRING)
    private PriorityFlag priorityFlag;

    @Column(name = "date")
    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    private TicketType type;

    @Column(name = "state", length = 50)
    private String state;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review reportedReview;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product reportedProduct;

    @ManyToOne
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;

    @Transient
    private StateTicket stateTicket;

    public Ticket() {
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", creationDate=" + creationDate +
                ", type=" + type +
                ", state='" + state + '\'' +
                ", stateTicket=" + stateTicket +
                '}';
    }
}
