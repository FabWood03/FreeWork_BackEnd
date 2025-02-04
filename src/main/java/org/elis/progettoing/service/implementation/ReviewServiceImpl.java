package org.elis.progettoing.service.implementation;

import org.elis.progettoing.dto.request.review.ReviewRequestDTO;
import org.elis.progettoing.dto.response.review.ReviewResponseDTO;
import org.elis.progettoing.dto.response.review.ReviewSummary;
import org.elis.progettoing.dto.response.review.ReviewSummaryResponse;
import org.elis.progettoing.exception.InvalidProductOwnerException;
import org.elis.progettoing.exception.entity.*;
import org.elis.progettoing.mapper.definition.ReviewMapper;
import org.elis.progettoing.models.Review;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.repository.ProductRepository;
import org.elis.progettoing.repository.ReviewRepository;
import org.elis.progettoing.repository.TicketRepository;
import org.elis.progettoing.service.definition.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the methods declared in the ReviewService interface
 */
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final LocalStorageService localStorageService;
    private final TicketRepository ticketRepository;

    /**
     * Constructor for ReviewServiceImpl
     *
     * @param reviewMapper        Mapper per la conversione tra entità e DTO delle recensioni
     * @param reviewRepository    Repository per l'accesso alle recensioni
     * @param productRepository   Repository per l'accesso ai prodotti
     * @param localStorageService Servizio per la gestione del salvataggio delle immagini
     */
    public ReviewServiceImpl(ReviewMapper reviewMapper, ReviewRepository reviewRepository, ProductRepository productRepository, LocalStorageService localStorageService, TicketRepository ticketRepository) {
        this.reviewMapper = reviewMapper;
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.localStorageService = localStorageService;
        this.ticketRepository = ticketRepository;
    }

    /**
     * Method to create a review
     *
     * @param reviewRequestDTO DTO of the review creation request
     * @return DTO of the review creation response
     * @throws EntityCreationException      if an error occurs while creating the review
     * @throws EntityAlreadyExistsException if a review already exists for the specified user and product
     * @throws InvalidProductOwnerException if the user tries to review one of their products
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReviewResponseDTO createReview(ReviewRequestDTO reviewRequestDTO, List<MultipartFile> images) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Review review = reviewMapper.reviewRequestDTOToReview(reviewRequestDTO);
        Product product = productRepository.findById(reviewRequestDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("prodotto", "ID", reviewRequestDTO.getProductId()));

        if (product.getUser().getId() == user.getId()) {
            throw new InvalidProductOwnerException("Non puoi recensire un tuo prodotto");
        }

        if (reviewRepository.existsByUserIdAndProductId(user.getId(), reviewRequestDTO.getProductId())) {
            throw new EntityAlreadyExistsException("Recensione", "ID utente", user.getId());
        }

        review.setDateCreation(LocalDateTime.now());
        review.setUser(user);
        review.setProduct(product);
        review.setTotalRating((review.getRatingQuality() + review.getRatingCommunication() + review.getRatingTimeliness() + review.getRatingCost()) / 4);
        product.getUser().setRanking(userRanking(product.getUser().getId(), review));

        List<String> imagesReview = localStorageService.saveReviewImages(
                Optional.ofNullable(images).orElse(Collections.emptyList()),
                reviewRequestDTO.getProductId(),
                user.getId()
        );

        review.setUrlReviewPhoto(imagesReview);

        try {
            reviewRepository.save(review);
        } catch (Exception e) {
            throw new EntityCreationException("recensione", "email utente", review.getUser().getEmail());
        }

        return reviewMapper.reviewToReviewResponseDTO(review);
    }

    /**
     * Method for updating a review
     *
     * @param reviewId ID of the review
     * @return DTO of the review update response
     * @throws EntityNotFoundException if the review with the specified ID does not exist
     * @throws EntityDeletionException if an error occurs while deleting the review
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReviewResponseDTO deleteReview(long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("recensione", "ID", reviewId));

        ticketRepository.unsetReview(reviewId);

        try {
            reviewRepository.delete(review);
        } catch (Exception e) {
            throw new EntityDeletionException("recensione", "ID", reviewId);
        }

        return reviewMapper.reviewToReviewResponseDTO(review);
    }

    /**
     * Method for updating a review
     *
     * @param reviewRequestDTO DTO of the review update request
     * @return DTO of the review update response
     * @throws EntityEditException     if an error occurs while updating the review
     * @throws EntityNotFoundException if the review with the specified ID does not exist
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReviewResponseDTO updateReview(ReviewRequestDTO reviewRequestDTO, List<MultipartFile> images) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Review review = reviewRepository.findByUserIdAndProductId(user.getId(), reviewRequestDTO.getProductId());

        Product product = productRepository.findById(reviewRequestDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("prodotto", "ID", reviewRequestDTO.getProductId()));

        if (review == null) {
            throw new EntityNotFoundException("recensione", "ID utente", user.getId());
        }

        reviewMapper.reviewRequestDTOToReview(reviewRequestDTO);
        review.setProduct(product);
        review.setTotalRating((review.getRatingQuality() + review.getRatingCommunication() + review.getRatingTimeliness() + review.getRatingCost()) / 4);

        user.setRanking(userRanking(user.getId(), review));

        List<String> imagesReview = localStorageService.saveReviewImages(images, reviewRequestDTO.getProductId(), user.getId());

        review.setUrlReviewPhoto(imagesReview);

        try {
            reviewRepository.save(review);
        } catch (Exception e) {
            throw new EntityEditException("recensione", "ID", review.getId());
        }

        return reviewMapper.reviewToReviewResponseDTO(review);
    }

    /**
     * Method for obtaining reviews for a product
     *
     * @param productId Product ID
     * @return List of product reviews
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Page<ReviewResponseDTO> getReviewsByProductId(long productId, Pageable pageable) {
        Page<Review> reviewsPage = reviewRepository.findByProductId(productId, pageable);

        return reviewsPage.map(reviewMapper::reviewToReviewResponseDTO);
    }

    /**
     * Method for obtaining reviews for a user
     *
     * @return List of user reviews
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ReviewResponseDTO> getReviewsReceivedByUserId(long userId) {
        List<Review> reviewsPage = reviewRepository.findByProductOwnerId(userId);

        return reviewsPage.stream()
                .map(reviewMapper::reviewToReviewResponseDTO)
                .toList();
    }

    /**
     * Method for obtaining reviews by user ID
     *
     * @return List of user reviews
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ReviewResponseDTO> getReviewsByUserId(long userId) {
        List<Review> reviewsPage = reviewRepository.findByUserId(userId);

        return reviewsPage.stream()
                .map(reviewMapper::reviewToReviewResponseDTO)
                .toList();
    }

    /**
     * Calculates the average ranking of a user based on the reviews they have left.
     * The average rating is calculated as the sum of all ratings divided by the number of reviews.
     *
     * @param userId the ID of the user you want to calculate the ranking for
     * @return the user's average rating
     * @throws EntityNotFoundException if the user with the specified ID does not exist
     * @throws EntityEditException     if an error occurs while saving the user's ranking
     */
    public double userRanking(long userId, Review newReview) {
        double totalRating = 0;
        List<Review> reviews = reviewRepository.findByProductOwnerId(userId);

        if (newReview != null) {
            reviews.add(newReview);
        }

        for (Review review : reviews) {
            totalRating += review.getTotalRating();
        }

        return totalRating / reviews.size();
    }

    /**
     * Returns a summary of reviews for a product, including review counts for each rating from 1 to 5 stars,
     * the total number of reviews and the weighted average rating.
     *
     * @param productId the ID of the product for which you want to get the review summary
     * @return a {@link ReviewSummaryResponse} object containing the review summary for the product
     */
    @Override
    public ReviewSummaryResponse getReviewSummaryByProductId(long productId) {
        List<Object[]> results = reviewRepository.countReviewsByProductId(productId);


        long[] ratingCounts = new long[5];
        long totalReviews = 0;
        double totalRatingSum = 0;

        for (Object[] result : results) {
            double rating = (Double) result[0];
            long count = (Long) result[1];
            int roundedRating = (int) Math.round(rating);

            if (roundedRating >= 1 && roundedRating <= 5) {
                ratingCounts[roundedRating - 1] += count;
                totalReviews += count;
                totalRatingSum += rating * count;
            }
        }

        List<ReviewSummary> ratingSummaries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ratingSummaries.add(new ReviewSummary(i + 1, ratingCounts[i]));
        }

        double averageRating = 0;
        if (totalReviews > 0) {
            averageRating = totalRatingSum / totalReviews;
        }

        return new ReviewSummaryResponse(ratingSummaries, totalReviews, averageRating);
    }

    /**
     * Returns a summary of reviews left by a specific user, including ratings from 1 to 5 stars,
     * the total number of reviews and the weighted average rating.
     *
     * @param userId the ID of the user for whom you want to get the review summary
     * @return a {@link ReviewSummaryResponse} object containing the review summary for the user
     */
    @Override
    public ReviewSummaryResponse getReviewSummaryByUserId(long userId) {
        List<Object[]> results = reviewRepository.countReviewsByProductUserId(userId);

        long[] ratingCounts = new long[5];
        long totalReviews = 0;
        double totalRatingSum = 0;

        for (Object[] result : results) {
            double rating = (Double) result[0];
            long count = (Long) result[1];

            int roundedRating = (int) Math.round(rating);

            if (roundedRating >= 1 && roundedRating <= 5) {
                ratingCounts[roundedRating - 1] += count;
                totalReviews += count;
                totalRatingSum += rating * count;
            }
        }

        List<ReviewSummary> ratingSummaries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ratingSummaries.add(new ReviewSummary(i + 1, ratingCounts[i]));
        }

        double averageRating = 0;
        if (totalReviews > 0) {
            averageRating = totalRatingSum / totalReviews;
        }

        return new ReviewSummaryResponse(ratingSummaries, totalReviews, averageRating);
    }
}
