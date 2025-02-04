package org.elis.progettoing.service.implementation;

import org.elis.progettoing.enumeration.PriorityFlag;
import org.elis.progettoing.enumeration.TicketType;
import org.elis.progettoing.models.Review;
import org.elis.progettoing.models.Ticket;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.elis.progettoing.repository.PurchasedProductRepository;
import org.elis.progettoing.repository.ReviewRepository;
import org.elis.progettoing.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service class that assigns a priority to a given ticket based on various criteria.
 * This includes ticket type, severity, age, state, and other factors like user reputation,
 * product popularity, and frequency of complaints.
 */
@Service
public class TicketPriorityAssigner {
    private final ReviewRepository reviewRepository;
    private final TicketRepository ticketRepository;
    private final PurchasedProductRepository purchasedProductRepository;

    private static final int LOW_REPUTATION_SCORE = 10;
    private static final int MEDIUM_REPUTATION_SCORE = 5;
    private static final int HIGH_REPUTATION_SCORE = 2;

    private static final int PRODUCT_POPULARITY_HIGH = 8;
    private static final int PRODUCT_POPULARITY_MEDIUM = 4;
    private static final int PRODUCT_POPULARITY_LOW = 2;

    private static final int RECENT_TICKET_SCORE = 20;
    private static final int MODERATE_TICKET_SCORE = 10;
    private static final int OLD_TICKET_SCORE = 5;

    private static final int OPEN_TICKETS_THRESHOLD = 3;
    private static final int OPEN_TICKETS_SCORE = 8;

    private static final int STATE_PENDING_SCORE = 5;
    private static final int URGENT_SELLER_REQUEST_SCORE = 10;

    private static final int HIGH_TRANSACTION_FREQUENCY_SCORE = 10;
    private static final int HIGH_COMPLAINT_FREQUENCY_SCORE = 12;

    private static final double LOW_RATING_THRESHOLD = 2.5;
    private static final int NEGATIVE_REVIEW_SCORE = 20;

    /**
     * Constructs a TicketPriorityAssigner service to assign priorities to tickets.
     *
     * @param ticketRepository           the repository for tickets
     * @param purchasedProductRepository the repository for purchased products
     */
    public TicketPriorityAssigner(TicketRepository ticketRepository, PurchasedProductRepository purchasedProductRepository, ReviewRepository reviewRepository) {
        this.ticketRepository = ticketRepository;
        this.purchasedProductRepository = purchasedProductRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Assigns a priority flag to the given ticket based on various factors like ticket type,
     * severity, age, state, and additional scores calculated based on user behavior, product popularity, etc.
     *
     * @param ticket the ticket to assign a priority to
     * @return the priority flag (HIGH, MEDIUM, LOW)
     */
    public PriorityFlag assignPriorityToTicket(Ticket ticket) {
        double priorityScore = 0.0;

        priorityScore += calculateBasePriorityScore(ticket);
        priorityScore += calculateSeverityScore(ticket);
        priorityScore += calculateAgeScore(ticket);
        priorityScore += calculateStateScore(ticket);
        priorityScore += calculateCombinationScore(ticket);

        // Penalizzazione specifica per i "SELLER_REQUEST"
        if (ticket.getType() == TicketType.SELLER_REQUEST) {
            priorityScore -= 10; // Penalizzazione extra
        }

        return determinePriorityLevel(priorityScore);
    }


    /**
     * Calculates the base priority score based on the ticket type.
     *
     * @param ticket the ticket to calculate the base priority score for
     * @return the base priority score based on ticket type
     */
    private int calculateBasePriorityScore(Ticket ticket) {
        return switch (ticket.getType()) {
            case SELLER_REQUEST -> 20;
            case REPORT_REVIEWS -> 25;
            case REPORT_USER -> 30;
            case REPORT_PRODUCT -> 35;
        };
    }

    /**
     * Calculates the severity score based on the reported entities (user, product, review).
     * Includes reputation, popularity, and frequency of reports or complaints.
     *
     * @param ticket the ticket for which the severity score is calculated
     * @return the severity score for the ticket
     */
    private double calculateSeverityScore(Ticket ticket) {
        double severityScore = 0.0;

        if (ticket.getReportedProduct() != null && ticket.getType() == TicketType.REPORT_PRODUCT) {
            severityScore += calculateUserReputationScore(ticket.getReportedProduct().getUser());
            severityScore += calculateProductSeverityScore(ticket.getReportedProduct());
            severityScore += calculateOpenTicketsForProduct(ticket.getReportedProduct());
        }

        if (ticket.getReportedUser() != null && ticket.getType() == TicketType.REPORT_USER) {
            severityScore += calculateUserReputationScore(ticket.getReportedUser());
            severityScore += calculateOpenTicketsForUser(ticket.getReportedUser());
            severityScore += calculateTransactionFrequencyScore(ticket.getReportedUser());
            severityScore += calculateComplaintFrequencyScore(ticket.getReportedUser());
        }

        if (ticket.getReportedReview() != null && ticket.getType() == TicketType.REPORT_REVIEWS) {
            severityScore += calculateUserReputationScore(ticket.getReportedReview().getUser());
            severityScore += calculateOpenTicketsForReview(ticket.getReportedReview());
            severityScore += calculateNegativeReviewScore(ticket.getReportedReview().getUser().getId());
        }

        return severityScore;
    }


    /**
     * Calculates the product severity score based on the product's popularity and report frequency.
     *
     * @param product the product to calculate the severity score for
     * @return the product's severity score
     */
    private double calculateProductSeverityScore(Product product) {
        int score = 0;

        List<PurchasedProduct> purchasedProducts = purchasedProductRepository.findByProduct(product);
        int productOrderCount = purchasedProducts.size();

        if (productOrderCount > 100) {
            score += PRODUCT_POPULARITY_HIGH;
        } else if (productOrderCount > 50) {  // Modifica qui
            score += PRODUCT_POPULARITY_MEDIUM;
        } else if (productOrderCount > 0) {
            score += PRODUCT_POPULARITY_LOW;
        }

        return score;
    }

    /**
     * Calculates the user reputation score based on the user's ranking.
     *
     * @param user the user to calculate the reputation score for
     * @return the user reputation score
     */
    private double calculateUserReputationScore(User user) {
        if (user.getRanking() < 1) {
            return LOW_REPUTATION_SCORE;
        } else if (user.getRanking() < 3) {
            return MEDIUM_REPUTATION_SCORE;
        } else {
            return HIGH_REPUTATION_SCORE;
        }
    }

    /**
     * Calculates the number of open tickets for a given user and returns a score if the threshold is exceeded.
     *
     * @param user the user to calculate open tickets for
     * @return the open tickets score for the user
     */
    private int calculateOpenTicketsForUser(User user) {
        long openTicketsCount = ticketRepository.countByReportedUser(user);
        return openTicketsCount > OPEN_TICKETS_THRESHOLD ? OPEN_TICKETS_SCORE : 0;
    }

    /**
     * Calculates the number of open tickets for a given review and returns a score if the threshold is exceeded.
     *
     * @param review the review to calculate open tickets for
     * @return the open tickets score for the review
     */
    private int calculateOpenTicketsForReview(Review review) {
        long openTicketsCount = ticketRepository.countByReportedReview(review);
        return openTicketsCount > OPEN_TICKETS_THRESHOLD ? OPEN_TICKETS_SCORE : 0;
    }

    /**
     * Calculates the number of open tickets for a given product and returns a score if the threshold is exceeded.
     *
     * @param product the product to calculate open tickets for
     * @return the open tickets score for the product
     */
    private int calculateOpenTicketsForProduct(Product product) {
        long openTicketsCount = ticketRepository.countByReportedProduct(product);
        return openTicketsCount > OPEN_TICKETS_THRESHOLD ? OPEN_TICKETS_SCORE : 0;
    }

    /**
     * Calculates the age score based on the ticket creation date.
     *
     * @param ticket the ticket to calculate the age score for
     * @return the age score for the ticket
     */
    private int calculateAgeScore(Ticket ticket) {
        long daysOld = ChronoUnit.DAYS.between(ticket.getCreationDate(), LocalDateTime.now());

        if (daysOld <= 2) {
            return RECENT_TICKET_SCORE;
        } else if (daysOld <= 5) {
            return MODERATE_TICKET_SCORE;
        } else {
            return OLD_TICKET_SCORE;
        }
    }

    /**
     * Calculates the score based on the current state of the ticket.
     *
     * @param ticket the ticket to calculate the state score for
     * @return the state score for the ticket
     */
    private int calculateStateScore(Ticket ticket) {
        return "In attesa".equals(ticket.getState()) || "In lavorazione".equals(ticket.getState()) ? STATE_PENDING_SCORE : 0;
    }

    /**
     * Calculates an additional score for a seller request ticket if it is recent (created within the last 24 hours).
     *
     * @param ticket the ticket to calculate the combination score for
     * @return the combination score for the ticket
     */
    private int calculateCombinationScore(Ticket ticket) {
        if (ticket.getType() == TicketType.SELLER_REQUEST) {
            return 0;
        }
        return ticket.getCreationDate().isAfter(LocalDateTime.now().minusDays(1)) ? URGENT_SELLER_REQUEST_SCORE : 0;
    }

    /**
     * Calculates the frequency of transactions for a user and returns a score if it exceeds the threshold.
     *
     * @param user the user to calculate the transaction frequency score for
     * @return the transaction frequency score for the user
     */
    private int calculateTransactionFrequencyScore(User user) {
        long transactionCount = purchasedProductRepository.countByBuyer(user);
        return transactionCount > 50 ? HIGH_TRANSACTION_FREQUENCY_SCORE : 0;
    }

    /**
     * Calculates the frequency of complaints for a user and returns a score if it exceeds the threshold.
     *
     * @param user the user to calculate the complaint frequency score for
     * @return the complaint frequency score for the user
     */
    private int calculateComplaintFrequencyScore(User user) {
        long complaintCount = ticketRepository.countByReportedUser(user);
        return complaintCount > 10 ? HIGH_COMPLAINT_FREQUENCY_SCORE : 0;
    }

    /**
     * Calculates the negative review score for a user based on the average rating of the user's reviews.
     *
     * @param userId the ID of the user to calculate the negative review score for
     * @return the negative review score for the user
     */
    private int calculateNegativeReviewScore(long userId) {
        double avgRating = reviewRepository.getAverageRatingForSeller(userId, LocalDateTime.now().minusDays(30));
        return avgRating < LOW_RATING_THRESHOLD ? NEGATIVE_REVIEW_SCORE : 0;
    }

    /**
     * Determines the priority level based on the calculated priority score.
     *
     * @param priorityScore the total priority score
     * @return the determined priority level (HIGH, MEDIUM, LOW)
     */
    private PriorityFlag determinePriorityLevel(double priorityScore) {
        if (priorityScore >= 80) {
            return PriorityFlag.HIGH;
        } else if (priorityScore >= 50) {
            return PriorityFlag.MEDIUM;
        } else {
            return PriorityFlag.LOW;
        }
    }
}
