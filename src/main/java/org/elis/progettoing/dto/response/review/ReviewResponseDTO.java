package org.elis.progettoing.dto.response.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.elis.progettoing.dto.response.user.UserResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing the details of a review for a product or service.
 *
 * <p>The {@link ReviewResponseDTO} class includes the review's comment, ratings for various
 * attributes, and metadata such as the review's creation date and the reviewer (user) details.</p>
 */
@Data
public class ReviewResponseDTO {

    private long id;

    private String comment;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime dateCreation;

    private double ratingQuality;

    private double ratingCommunication;

    private double ratingTimeliness;

    private double ratingCost;

    private double totalRating;

    private UserResponseDTO user;

    private List<String> imagesPath;
}
