package org.elis.progettoing.pattern.observerPattern;

import org.elis.progettoing.enumeration.AuctionStatus;
import org.elis.progettoing.exception.auction.AuctionException;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.models.auction.AuctionSubscription;
import org.elis.progettoing.repository.AuctionSubscriptionRepository;
import org.elis.progettoing.service.definition.EmailService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The {@code AuctionManager} class handles operations related to auction ad registrations.
 * and sends notifications to users based on events occurring in the auction.
 * Implements the logic to subscribe and unsubscribe users, as well as notify users of opening events,
 * closing, and impending end of an auction.
 */
@Component
public class AuctionManager {
    private final AuctionSubscriptionRepository auctionSubscriptionRepository;
    private final EmailService emailService;

    /**
     * Builder that initializes the components needed to handle auction registrations and notifications.
     *
     * @param auctionSubscriptionRepository the repository for managing auction subscriptions.
     * @param emailService                  the service for sending emails.
     */
    public AuctionManager(AuctionSubscriptionRepository auctionSubscriptionRepository, EmailService emailService) {
        this.auctionSubscriptionRepository = auctionSubscriptionRepository;
        this.emailService = emailService;
    }

    /**
     * Allows a user to register for an auction. If the user is already enrolled, an exception is raised.
     *
     * @param auction the auction the user wishes to subscribe to.
     * @param user    the user signing up for the auction.
     * @throws AuctionException if the user is already enrolled in the auction.
     */
    public void subscribe(Auction auction, User user) {
        try {
            boolean alreadySubscribed = auctionSubscriptionRepository.existsByAuctionAndUser(auction, user);

            if (alreadySubscribed) {
                throw new AuctionException(user.getId());
            }

            AuctionSubscription subscription = new AuctionSubscription();
            subscription.setAuction(auction);
            subscription.setUser(user);
            auctionSubscriptionRepository.save(subscription);
        } catch (Exception e) {
            throw new AuctionException(user.getId());
        }
    }

    /**
     * Allows a user to unsubscribe from an auction.
     *
     * @param auction the auction from which the user wishes to unsubscribe.
     * @param user    the user unsubscribing from the auction.
     * @throws AuctionException if an error occurs while unsubscribing.
     */
    public void unsubscribe(Auction auction, User user) {
        try {
            AuctionSubscription subscription = auctionSubscriptionRepository.findByAuctionAndUser(auction, user);
            auctionSubscriptionRepository.delete(subscription);
        } catch (Exception e) {
            throw new AuctionException(user.getId());
        }
    }

    /**
     * Send email notifications to registered users when an auction is opened.
     * Notification is sent only if the auction start date has passed and the auction status is “OPEN”.
     *
     * @param auction the auction being opened.
     */
    public void notifyAuctionOpening(Auction auction) {
        if (auction.getStartAuctionDate().isBefore(LocalDateTime.now()) && auction.getStatus() == AuctionStatus.OPEN) {
            List<AuctionSubscription> subscriptions = auctionSubscriptionRepository.findByAuction(auction);
            EmailNotificationListener listener = new EmailNotificationListener(emailService);

            subscriptions.forEach(subscription ->
                    listener.update(auction, subscription.getUser(), "auctionOpened")
            );
        }
    }

    /**
     * Send email notifications to registered users when an auction is about to close.
     * Notification is sent only if there is less than an hour until the end of the auction.
     *
     * @param auction the auction is about to close.
     */
    public void notifyAuctionEndingSoon(Auction auction) {
        LocalDateTime now = LocalDateTime.now();
        if (auction.getEndAuctionDate().minusHours(1).isBefore(now) && auction.getEndAuctionDate().isAfter(now)) {
            List<AuctionSubscription> subscriptions = auctionSubscriptionRepository.findByAuction(auction);
            EmailNotificationListener listener = new EmailNotificationListener(emailService);

            subscriptions.forEach(subscription ->
                    listener.update(auction, subscription.getUser(), "auctionEndingSoon")
            );
        }
    }

    /**
     * Send email notifications to registered users when an auction is closed.
     * Notification is sent only if the auction end date has passed.
     *
     * @param auction the auction that has been closed.
     */
    public void notifyAuctionClosed(Auction auction) {
        if (auction.getEndAuctionDate().isBefore(LocalDateTime.now())) {
            List<AuctionSubscription> subscriptions = auctionSubscriptionRepository.findByAuction(auction);
            EmailNotificationListener listener = new EmailNotificationListener(emailService);

            subscriptions.forEach(subscription ->
                    listener.update(auction, subscription.getUser(), "auctionClosed")
            );
        }
    }

    /**
     * Send email notifications to registered users when an auction has a winner.
     * Notification is sent only if the auction has a winner.
     *
     * @param auction     the auction that has a winner.
     * @param userWinner the user who won the auction.
     */
    public void notifyAuctionWinner(Auction auction, User userWinner) {
        List<AuctionSubscription> subscriptions = auctionSubscriptionRepository.findByAuction(auction);
        EmailNotificationListener listener = new EmailNotificationListener(emailService);

        subscriptions.forEach(subscription -> {
            if (subscription.getUser().getId() == userWinner.getId()) {
                listener.update(auction, subscription.getUser(), "auctionWinner");
            } else {
                listener.update(auction, subscription.getUser(), "auctionNotWinner");
            }
        });
    }
}