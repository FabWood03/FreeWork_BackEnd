package org.elis.progettoing.service.implementation;

import org.elis.progettoing.dto.request.auction.OfferRequestDTO;
import org.elis.progettoing.dto.response.auction.OfferResponseDTO;
import org.elis.progettoing.enumeration.AuctionStatus;
import org.elis.progettoing.exception.auction.AuctionException;
import org.elis.progettoing.exception.auction.AuctionOwnershipException;
import org.elis.progettoing.exception.entity.EntityAlreadyExistsException;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.OfferMapper;
import org.elis.progettoing.models.Offer;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.pattern.observerPattern.AuctionManager;
import org.elis.progettoing.repository.AuctionRepository;
import org.elis.progettoing.repository.OfferRepository;
import org.elis.progettoing.service.definition.OfferService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Service implementation for bid management in an auction system.
 */
@Service
public class OfferServiceImpl implements OfferService {
    private final OfferRepository offerRepository;
    private final AuctionRepository auctionRepository;
    private final OfferMapper offerMapper;
    private final AuctionManager auctionManager;

    /**
     * Constructor for OfferServiceImpl.
     *
     * @param offerRepository   the repository for accessing offers.
     * @param auctionRepository the repository for accessing auctions.
     * @param offerMapper       the mapper for conversion between entities and DTOs.
     * @param auctionManager    the manager for auction operations.
     */
    public OfferServiceImpl(OfferRepository offerRepository, AuctionRepository auctionRepository, OfferMapper offerMapper, AuctionManager auctionManager) {
        this.offerRepository = offerRepository;
        this.auctionRepository = auctionRepository;
        this.offerMapper = offerMapper;
        this.auctionManager = auctionManager;
    }

    /**
     * Create a new bid for an auction.
     *
     * @param offerRequestDTO DTO containing the offer data.
     * @return Response DTO with the details of the created offer.
     * @throws EntityCreationException      if an error occurs while creating the offer.
     * @throws AuctionException             if the auction is not open.
     * @throws EntityAlreadyExistsException if the user has already submitted an offer.
     * @throws EntityNotFoundException      if the auction does not exist.
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OfferResponseDTO createOffer(OfferRequestDTO offerRequestDTO) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Auction auction = auctionRepository.findById(offerRequestDTO.getAuctionId())
                .orElseThrow(() -> new EntityNotFoundException("asta", "ID", offerRequestDTO.getAuctionId()));

        Offer existingOffer = offerRepository.findBySellerIdAndAuctionId(user.getId(), auction.getId());

        // Verifica che l'asta sia aperta.
        if (auction.getStatus() == AuctionStatus.PENDING || auction.getStatus() == AuctionStatus.CLOSED) {
            throw new AuctionException(user.getId());
        }

        // Lancia un'eccezione se l'utente ha già presentato un'offerta.
        if (existingOffer != null) {
            throw new EntityAlreadyExistsException("offerta", "ID", user.getId());
        }

        if (user.getId() == auction.getOwner().getId()) {
            throw new AuctionOwnershipException(user.getEmail(), auction.getId(), "creazione offerta");
        }

        // Crea una nuova offerta utilizzando il mapper.
        Offer offer = offerMapper.offerRequestDTOToOffer(offerRequestDTO);
        offer.setSeller(user);
        offer.setOfferDate(LocalDateTime.now());

        auctionManager.subscribe(auction, user);

        // Salva l'offerta nel database e gestisce eventuali errori.
        try {
            offerRepository.save(offer);
        } catch (Exception e) {
            throw new EntityCreationException("offerta", "ID venditore", user.getId());
        }

        // Restituisce l'offerta creata come DTO.
        return offerMapper.offerToOfferResponseDTO(offer);
    }

    /**
     * Delete an authenticated user's bid for a particular auction.
     *
     * @param auctionId ID of the auction.
     * @return true if the deletion was successful.
     * @throws EntityDeletionException if an error occurs while deleting the offer.
     * @throws EntityNotFoundException if the auction does not exist.
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteOffer(long auctionId) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // Trova l'asta e l'offerta corrispondente.
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("asta", "ID", auctionId));

        Offer offer = offerRepository.findBySellerIdAndAuctionId(user.getId(), auctionId);

        auctionManager.unsubscribe(auction, user);

        // Elimina l'offerta dal database e gestisce eventuali errori.
        try {
            offerRepository.delete(offer);
        } catch (Exception e) {
            throw new EntityDeletionException("offerta", "ID utente", user.getId());
        }

        return true;
    }

    /**
     * Update an existing offer from an authenticated user.
     *
     * @param offerRequestDTO DTO containing the updated offer data.
     * @return Response DTO with updated offer details.
     * @throws EntityDeletionException if an error occurs while updating the offer.
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OfferResponseDTO updateOffer(OfferRequestDTO offerRequestDTO) {
        // Recupera l'utente autenticato.
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Offer offer = offerRepository.findBySellerIdAndAuctionId(user.getId(), offerRequestDTO.getAuctionId());

        // Aggiorna i dati dell'offerta.
        offer.setPrice(offerRequestDTO.getPrice());
        offer.setDeliveryTimeProposed(offerRequestDTO.getDeliveryTimeProposed());
        offer.setOfferDate(LocalDateTime.now());

        // Salva l'offerta aggiornata nel database.
        try {
            offerRepository.save(offer);
        } catch (Exception e) {
            throw new EntityDeletionException("offerta", "ID utente", user.getId());
        }

        return offerMapper.offerToOfferResponseDTO(offer);
    }

    /**
     * Retrieve details of an offer via its ID.
     *
     * @param offerId ID of the offer.
     * @return Response DTO with offer details.
     */
    @Transactional(readOnly = true)
    @Override
    public OfferResponseDTO getOfferById(long offerId) {
        Offer offer = offerRepository.findById(offerId).orElse(null);
        return offerMapper.offerToOfferResponseDTO(offer);
    }

    /**
     * Retrieves the offer of an authenticated user.
     *
     * @return Response DTO with offer details.
     */
    @Transactional(readOnly = true)
    @Override
    public OfferResponseDTO getOfferByUser(long auctionId) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Offer offers = offerRepository.findBySellerIdAndAuctionId(user.getId(), auctionId);

        return offerMapper.offerToOfferResponseDTO(offers);
    }

    /**
     * Retrieve all bids relating to a specific auction.
     *
     * @param auctionId ID of the auction.
     * @return List of DTO offers sorted by score and creation date.
     * @throws EntityNotFoundException if the auction does not exist.
     */
    @Transactional(readOnly = true)
    @Override
    public List<OfferResponseDTO> getAllOffersByAuction(long auctionId) {
        List<Offer> offers = offerRepository.findAllByAuctionId(auctionId);

        // Calcola il prezzo medio delle offerte.
        double averagePrice = offers.stream()
                .mapToDouble(Offer::getPrice)
                .average()
                .orElse(0.0);

        // Trova l'asta corrispondente.
        Auction auction = auctionRepository.findById(auctionId)
                .orElse(null);

        if (auction == null) {
            throw new EntityNotFoundException("asta", "ID", auctionId);
        }

        // Calcola e assegna il punteggio a ciascuna offerta.
        offers.forEach(offer -> {
            double score = calculateTotalScore(offer, offer.getSeller(), averagePrice, auction.getDeliveryDate());
            offer.setScore(score);
        });


        // Ordina le offerte per punteggio e data.
        return offers.stream()
                .sorted(Comparator.comparingDouble(Offer::getScore).reversed()
                        .thenComparing(Offer::getOfferDate))
                .map(offerMapper::offerToOfferResponseDTO)
                .toList();
    }

    /**
     * Calculate the total score of an offer based on price, delivery time and user ranking.
     *
     * @param offer               Offer to be evaluated.
     * @param seller              Seller user of the offer.
     * @param averagePrice        Average price of offers.
     * @param auctionDeliveryTime Auction delivery period.
     * @return Total score assigned to the offer in order to draw up the ranking.
     */
    private double calculateTotalScore(Offer offer, User seller, double averagePrice, long auctionDeliveryTime) {
        double priceScore = calculatePriceScore(offer.getPrice(), averagePrice);

        double deliveryTimeScore = calculateDeliveryTimeScore(offer, auctionDeliveryTime);

        double userRankingScore = seller.getRanking() / 5.0;

        return (priceScore * 0.4) + (deliveryTimeScore * 0.4) + (userRankingScore * 0.2);
    }

    /**
     * Calculate an offer's price score based on the average price of offers.
     *
     * @param offerPrice   Offer price.
     * @param averagePrice Average price of offers.
     * @return Score assigned to the offer price.
     */
    private double calculatePriceScore(double offerPrice, double averagePrice) {
        if (offerPrice <= 0) return 0; // Evita divisioni per 0 o prezzi invalidi

        double priceRatio = offerPrice / averagePrice;

        if (priceRatio < 1) {
            // Premi maggiormente per prezzi inferiori al medio (differenza più marcata)
            return 1 - Math.pow(priceRatio, 2); // Penalizzazione meno severa per offerte più basse
        } else {
            // Penalizza per prezzi superiori al medio, con una penalità meno esponenziale
            return 1 / (1 + Math.pow(priceRatio - 1, 2)); // Penalità graduale per prezzi più alti
        }
    }

    /**
     * Calculates the proposed lead time score of an offer based on the requested lead time of the auction.
     *
     * @param offer               Offer to evaluate.
     * @param auctionDeliveryDays Days required for auction delivery.
     * @return Score assigned to the proposed delivery time of the offer.
     */
    private double calculateDeliveryTimeScore(Offer offer, long auctionDeliveryDays) {
        long offerDeliveryDays = offer.getDeliveryTimeProposed();

        long daysToDeliver = Math.abs(auctionDeliveryDays - offerDeliveryDays);

        if (auctionDeliveryDays <= 0 || offerDeliveryDays <= 0) {
            return 0;
        }

        double c = 15.0;
        double minimumScore = 0.1;

        return 1 / (1 + Math.pow(daysToDeliver / c, 2)) + minimumScore;
    }
}
