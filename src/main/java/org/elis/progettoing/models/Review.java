package org.elis.progettoing.models;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.models.product.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a review.
 * <p>
 * A review is a comment made by a user about a product.
 * </p>
 */
@Data
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "date")
    private LocalDateTime dateCreation;

    @Column(name = "rating_quality")
    private double ratingQuality;

    @Column(name = "rating_communication")
    private double ratingCommunication;

    @Column(name = "rating_timeliness")
    private double ratingTimeliness;

    @Column(name = "rating_cost")
    private double ratingCost;

    @Column(name = "total_rating")
    private double totalRating;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "url_review_photo")
    @CollectionTable(name = "review_photo", joinColumns = @JoinColumn(name = "review_id"))
    @ElementCollection
    private List<String> urlReviewPhoto;

    @OneToMany(mappedBy = "reportedReview")
    private List<Ticket> ticketsReporting = new ArrayList<>();

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", dateCreation=" + dateCreation +
                ", ratingQuality=" + ratingQuality +
                ", ratingCommunication=" + ratingCommunication +
                ", ratingTimeliness=" + ratingTimeliness +
                ", ratingCost=" + ratingCost +
                ", totalRating=" + totalRating +
                '}';
    }
}
