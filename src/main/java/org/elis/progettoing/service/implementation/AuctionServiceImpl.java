package org.elis.progettoing.service.implementation;

import org.elis.progettoing.dto.request.auction.AuctionRequestDTO;
import org.elis.progettoing.dto.response.auction.AuctionDetailsDTO;
import org.elis.progettoing.dto.response.auction.AuctionSummaryDTO;
import org.elis.progettoing.enumeration.AuctionStatus;
import org.elis.progettoing.exception.auction.*;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityEditException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.AuctionMapper;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.models.auction.AuctionSubscription;
import org.elis.progettoing.pattern.observerPattern.AuctionManager;
import org.elis.progettoing.repository.AuctionRepository;
import org.elis.progettoing.repository.AuctionSubscriptionRepository;
import org.elis.progettoing.repository.UserRepository;
import org.elis.progettoing.service.definition.AuctionService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the AuctionService that manages the auction lifecycle,
 * including automatic creation and updating of status based on dates.
 */
@Service
@EnableScheduling
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final AuctionManager auctionManager;
    private final AuctionSubscriptionRepository auctionSubscriptionRepository;
    private final AuctionMapper auctionMapper;
    private final UserRepository userRepository;

    /**
     * Costruttore per AuctionServiceImpl.
     *
     * @param auctionRepository             Il repository per la gestione delle aste.
     * @param auctionManager                Il gestore per la gestione degli eventi del ciclo di vita delle aste.
     * @param auctionSubscriptionRepository Il repository per la gestione delle sottoscrizioni alle aste.
     * @param auctionMapper                 Il mapper per la conversione tra DTO ed entità.
     */
    public AuctionServiceImpl(AuctionRepository auctionRepository, AuctionManager auctionManager, AuctionSubscriptionRepository auctionSubscriptionRepository, AuctionMapper auctionMapper, UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.auctionManager = auctionManager;
        this.auctionSubscriptionRepository = auctionSubscriptionRepository;
        this.auctionMapper = auctionMapper;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new auction and sets its initial state.
     *
     * @param auctionRequestDTO Data of the auction to be created.
     * @return AuctionResponseDTO with details of the created auction.
     * @throws AuctionException if a generic error occurs while saving the auction.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuctionDetailsDTO createAuction(AuctionRequestDTO auctionRequestDTO) {
        // Retrieve the authenticated user
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User userAuthenticated = (User) authentication.getPrincipal();

        Auction auction = auctionMapper.auctionRequestDTOToAuction(auctionRequestDTO);
        auction.setOwner(userAuthenticated);
        auction.setStartAuctionDate(auctionRequestDTO.getStartAuctionDate());
        auction.setEndAuctionDate(auctionRequestDTO.getEndAuctionDate());
        auction.setStatus(auction.getStartAuctionDate().isAfter(LocalDateTime.now()) ? AuctionStatus.PENDING : AuctionStatus.OPEN);

        try {
            auctionRepository.save(auction);
        } catch (Exception e) {
            throw new AuctionException("Errore durante il salvataggio dell'asta.", e);
        }

        return auctionMapper.auctionToAuctionResponseDTO(auction);
    }

    /**
     * Deletes an existing auction.
     *
     * @param auctionId The ID of the auction to be deleted.
     * @return true if the auction is successfully deleted.
     * @throws EntityNotFoundException   if the auction with the specified ID does not exist in the system.
     * @throws AuctionOwnershipException if the user is not the owner of the auction.
     * @throws AuctionException          if the auction is not in a pending state.
     * @throws EntityDeletionException   if an error occurs while deleting the auction.
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteAuction(long auctionId) {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User userAuthenticated = (User) authentication.getPrincipal();

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("asta", "ID", auctionId));

        if (auction.getOwner().getId() != userAuthenticated.getId()) {
            throw new AuctionOwnershipException(userAuthenticated.getId(), auctionId, "eliminazione");
        }

        if (auction.getStatus() != AuctionStatus.PENDING) {
            throw new AuctionException("Non è possibile eliminare l'asta.");
        }

        try {
            auctionRepository.delete(auction);
        } catch (Exception e) {
            throw new EntityDeletionException("asta", "ID", auctionId);
        }

        return true;
    }

    /**
     * Aggiorna un'asta esistente con nuovi dettagli.
     *
     * @param auctionRequestDTO I dettagli aggiornati dell'asta.
     * @return AuctionResponseDTO con i dettagli aggiornati dell'asta.
     * @throws EntityNotFoundException      se l'asta con l'ID specificato non esiste nel sistema.
     * @throws AuctionOwnershipException    se l'utente non è il proprietario dell'asta.
     * @throws InvalidAuctionStateException se l'asta non è in stato pendente.
     * @throws EntityEditException          se si verifica un errore durante l'aggiornamento dell'asta.
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuctionDetailsDTO updateAuction(AuctionRequestDTO auctionRequestDTO) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Auction auction = auctionRepository.findById(auctionRequestDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("asta", "ID", auctionRequestDTO.getId()));

        if (auction.getStatus() != AuctionStatus.PENDING) {
            throw new InvalidAuctionStateException(auction.getId(), auction.getStatus(), AuctionStatus.PENDING);
        }

        if (auction.getOwner().getId() != user.getId()) {
            throw new AuctionOwnershipException(user.getId(), auction.getId(), "modifica");
        }

        auction = auctionMapper.auctionRequestDTOToAuction(auctionRequestDTO);
        auction.setOwner(user);
        auction.setStatus(AuctionStatus.PENDING);

        try {
            auctionRepository.save(auction);
        } catch (Exception e) {
                throw new EntityEditException("asta", "ID", auctionRequestDTO.getId());
        }

        return auctionMapper.auctionToAuctionResponseDTO(auction);
    }

    /**
     * Retrieves a summary of all open auctions.
     *
     * @return List of DTOs representing open auctions.
     */
    @Override
    public List<AuctionSummaryDTO> getActiveAuctionSummary() {
        List<Auction> auctions = auctionRepository.findByStatus(AuctionStatus.OPEN);

        return auctions.stream()
                .map(auctionMapper::auctionToAuctionSummaryDTO)
                .toList();
    }

    /**
     * Retrieves a summary of auctions by the specified user.
     *
     * @param userId The ID of the user to get the auctions for.
     * @return List of DTOs representing auctions owned by the user.
     */
    @Override
    public List<AuctionSummaryDTO> getAuctionSummaryByUserId(long userId) {
        List<Auction> auctions = auctionRepository.findByOwnerIdAndStatus(userId);

        return auctions.stream()
                .map(auctionMapper::auctionToAuctionSummaryDTO)
                .toList();
    }

    /**
     * Retrieves a summary of all pending auctions.
     *
     * @return List of DTOs representing pending auctions.
     */
    @Override
    public List<AuctionSummaryDTO> getPendingAuctionSummary() {
        List<Auction> auctions = auctionRepository.findByStatus(AuctionStatus.PENDING);

        return auctions.stream()
                .map(auctionMapper::auctionToAuctionSummaryDTO)
                .toList();
    }

    /**
     * Assigns a winner to the specified auction.
     *
     * @param auctionId The ID of the auction to assign a winner to.
     * @param winnerId  The ID of the user to assign as the winner.
     * @return true if the winner is successfully assigned.
     * @throws EntityNotFoundException      if the auction with the specified ID does not exist in the system.
     * @throws AuctionOwnershipException    if the user is not the owner of the auction.
     * @throws AuctionException             if the auction is not closed.
     * @throws UserSubscriptionException    if an error occurs during user registration.
     */
    @Override
    public Boolean assignWinner(long auctionId, long winnerId) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User userAuthenticated = (User) authentication.getPrincipal();

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("asta", "ID", auctionId));

        User userWinner = userRepository.findById(winnerId)
                .orElseThrow(() -> new EntityNotFoundException("utente", "ID", winnerId));

        if (auction.getOwner().getId() != userAuthenticated.getId()) {
            throw new AuctionOwnershipException(userAuthenticated.getId(), auctionId, "assegnazione vincitore");
        }

        if (auction.getStatus() != AuctionStatus.CLOSED) {
            throw new AuctionException("L'asta con ID " + auctionId + " non è chiusa.");
        }

        auction.setWinner(userWinner);

        try {
            auctionRepository.save(auction);
        } catch (Exception e) {
            throw new AuctionException("Errore durante l'assegnazione del vincitore all'asta con ID: " + auctionId);
        }

        auctionManager.notifyAuctionWinner(auction, userWinner);

        return true;
    }

    /**
     * Subscribes a user to receive notifications about the specified auction.
     *
     * @param auctionId The ID of the auction to subscribe to.
     * @return true if the subscription was successful.
     * @throws EntityNotFoundException if the auction with the specified ID does not exist in the system.
     * @throws AuctionOwnershipException if the user is the owner of the auction.
     * @throws AuctionException if the auction is not in pending status.
     * @throws UserSubscriptionException if an error occurs during user registration.
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean subscribeUserNotification(long auctionId) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("asta", "ID", auctionId));

        if (auction.getOwner().getId() == user.getId()) {
            throw new AuctionOwnershipException(user.getEmail(), auctionId, "iscrizione alle notifiche");
        }

        if (auction.getStatus() != AuctionStatus.PENDING) {
            throw new AuctionException("L'asta con ID " + auctionId + " è chiusa o in attesa e non è possibile iscriversi alle notifiche.");
        }

        try {
            auctionManager.subscribe(auction, user);
            return true;
        } catch (Exception e) {
            throw new UserSubscriptionException("Errore durante l'iscrizione dell'utente alle notifiche per l'asta con ID: " + auctionId);
        }
    }

    /**
     * Retrieves complete details of an auction, including the owner, bids,
     * the start and end dates, and the current status of the auction.
     *
     * @param auctionId The ID of the auction to get the details of.
     * @return AuctionResponseDTO containing the detailed auction information.
     * @throws EntityNotFoundException if the auction with the specified ID does not exist in the system.
     */
    @Transactional(readOnly = true)
    @Override
    public AuctionDetailsDTO getAuctionDetails(long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Asta", "ID", auctionId));

        return auctionMapper.auctionToAuctionResponseDTO(auction);
    }

    /**
     * Retrieves all active and open auctions.
     *
     * @return List of DTOs representing active auctions.
     * @throws EntityNotFoundException If there are no active auctions.
     */
    @Transactional(readOnly = true)
    @Override
    public List<AuctionDetailsDTO> listActiveAuctions() {
        List<Auction> activeAuctions = auctionRepository.findByStatus(AuctionStatus.OPEN);

        if (activeAuctions.isEmpty()) {
            throw new EntityNotFoundException("asta", "stato", AuctionStatus.OPEN.name());
        }

        return activeAuctions.stream()
                .map(auctionMapper::auctionToAuctionResponseDTO)
                .toList();
    }

    /**
     * Retrieves all closed auctions.
     *
     * @return List of DTOs representing closed auctions.
     * @throws EntityNotFoundException If there are no closed auctions.
     */
    @Transactional(readOnly = true)
    @Override
    public List<AuctionDetailsDTO> listClosedAuctions() {
        List<Auction> closedAuctions = auctionRepository.findByStatus(AuctionStatus.CLOSED);

        if (closedAuctions.isEmpty()) {
            throw new EntityNotFoundException("asta", "stato", AuctionStatus.CLOSED.name());
        }

        return closedAuctions.stream()
                .map(auctionMapper::auctionToAuctionResponseDTO)
                .toList();
    }

    /**
     * Retrieves all pending auctions.
     *
     * @return List of DTOs representing pending auctions.
     * @throws EntityNotFoundException If there are no pending auctions.
     */
    @Transactional(readOnly = true)
    @Override
    public List<AuctionDetailsDTO> listPendingAuctions() {
        List<Auction> pendingAuctions = auctionRepository.findByStatus(AuctionStatus.PENDING);

        if (pendingAuctions.isEmpty()) {
            throw new EntityNotFoundException("asta", "stato", AuctionStatus.PENDING.name());
        }

        return pendingAuctions.stream()
                .map(auctionMapper::auctionToAuctionResponseDTO)
                .toList();
    }

    /**
     * Retrieves all auctions for which the user has chosen to receive notifications.
     *
     * @return List of DTOs representing the auctions the user is subscribed to.
     */
    @Transactional(readOnly = true)
    @Override
    public List<AuctionDetailsDTO> listSubscribedAuctions() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<AuctionSubscription> subscribedAuctions = auctionSubscriptionRepository.findByUser(user);

        return subscribedAuctions.stream()
                .map(subscription -> auctionMapper.auctionToAuctionResponseDTO(subscription.getAuction()))
                .toList();
    }

    /**
     * Retrieves a summary of all closed auctions without a winner.
     *
     * @return List of DTOs representing closed auctions without a winner.
     */
    @Override
    public List<AuctionSummaryDTO> getClosedAndWithoutWinnerAuctionSummary() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<Auction> auctions = auctionRepository.findByStatusAndWinnerIsNullAndOwner(AuctionStatus.CLOSED, user);

        return auctions.stream()
                .map(auctionMapper::auctionToAuctionSummaryDTO)
                .toList();
    }

    /**
     * Retrieves a summary of all pending auctions without a winner.
     *
     * @return List of DTOs representing pending auctions without a winner.
     */
    @Override
    public List<AuctionSummaryDTO> getPendingAndWithoutWinnerAuctionSummary() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<Auction> auctions = auctionRepository.findByStatusAndWinnerIsNullAndOwner(AuctionStatus.PENDING, user);

        return auctions.stream()
                .map(auctionMapper::auctionToAuctionSummaryDTO)
                .toList();
    }

    /**
     * Retrieves a summary of all open auctions without a winner.
     *
     * @return List of DTOs representing open auctions without a winner.
     */
    @Override
    public List<AuctionSummaryDTO> getOpenAndWithoutWinnerAuctionSummary() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<Auction> auctions = auctionRepository.findByStatusAndWinnerIsNullAndOwner(AuctionStatus.OPEN, user);

        return auctions.stream()
                .map(auctionMapper::auctionToAuctionSummaryDTO)
                .toList();
    }

    /**
     * Retrieves a summary of all auctions the user is subscribed to.
     *
     * @return List of DTOs representing the auctions the user is subscribed to.
     */
    @Override
    public Boolean getAuctionSubscriptionByAuctionId(long auctionId) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("asta", "ID", auctionId));

        return auctionSubscriptionRepository.existsByAuctionAndUser(auction, user);
    }

    /**
     * Updates auction status periodically.
     * @throws AuctionStatusUpdateException if an error occurs while updating the auction status.
     */
    @Scheduled(fixedRate = 30000)
    public void updateAuctionStatus() {
        try {
            List<Auction> auctionsToUpdate = auctionRepository.findAllActiveAndPendingAuctions(LocalDateTime.now());
            auctionsToUpdate.parallelStream().forEach(this::updateSingleAuctionStatus);
        } catch (Exception e) {
            throw new AuctionStatusUpdateException("Errore globale durante l'aggiornamento dello stato delle aste.", e);
        }
    }

    /**
     * Updates the status of a single auction based on the current date.
     *
     * @param auction The auction to update.
     * @throws AuctionException if an error occurs while updating the auction status.
     */
    public void updateSingleAuctionStatus(Auction auction) {
        try {
            boolean updated = false;

            if (auction.getStatus() == AuctionStatus.PENDING && auction.getStartAuctionDate().isBefore(LocalDateTime.now())) {
                auction.setStatus(AuctionStatus.OPEN);
                auctionRepository.save(auction);
                updated = true;

                // Invia notifica di apertura
                auctionManager.notifyAuctionOpening(auction);
            }

            if (auction.getStatus() == AuctionStatus.OPEN && auction.getEndAuctionDate().isBefore(LocalDateTime.now())) {
                auction.setStatus(AuctionStatus.CLOSED);
                auctionRepository.save(auction);
                updated = true;

                // Invia notifica di chiusura
                auctionManager.notifyAuctionClosed(auction);
            }

            if (!updated && auction.getStatus() == AuctionStatus.OPEN
                    && auction.getEndAuctionDate().minusHours(1).isBefore(LocalDateTime.now())) {
                // Invia notifica che l'asta sta per terminare
                auctionManager.notifyAuctionEndingSoon(auction);
            }
        } catch (Exception e) {
            throw new AuctionException("Errore durante l'aggiornamento dell'asta con ID: " + auction.getId(), e);
        }
    }
}
