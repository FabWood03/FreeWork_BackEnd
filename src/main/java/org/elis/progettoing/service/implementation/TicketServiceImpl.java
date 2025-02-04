package org.elis.progettoing.service.implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.elis.progettoing.dto.request.ticket.DemandSellerRequestDTO;
import org.elis.progettoing.dto.request.ticket.TicketFilterRequest;
import org.elis.progettoing.dto.request.ticket.TicketRequestDTO;
import org.elis.progettoing.dto.response.ticket.FilteredTicketsResponse;
import org.elis.progettoing.dto.response.ticket.TicketResponseDTO;
import org.elis.progettoing.enumeration.PriorityFlag;
import org.elis.progettoing.enumeration.Role;
import org.elis.progettoing.enumeration.TicketType;
import org.elis.progettoing.exception.EmailSendingException;
import org.elis.progettoing.exception.TicketActionException;
import org.elis.progettoing.exception.entity.EntityEditException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.exception.entity.InvalidEntityDataException;
import org.elis.progettoing.mapper.definition.TicketMapper;
import org.elis.progettoing.models.Review;
import org.elis.progettoing.models.Ticket;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.pattern.stateTicketPattern.AcceptedState;
import org.elis.progettoing.pattern.stateTicketPattern.PendingState;
import org.elis.progettoing.pattern.stateTicketPattern.RefuseState;
import org.elis.progettoing.pattern.stateTicketPattern.TakeOnState;
import org.elis.progettoing.repository.ProductRepository;
import org.elis.progettoing.repository.ReviewRepository;
import org.elis.progettoing.repository.TicketRepository;
import org.elis.progettoing.repository.UserRepository;
import org.elis.progettoing.service.definition.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione del servizio per la gestione dei ticket.
 */
@Service
public class TicketServiceImpl implements TicketService {
    private final TicketPriorityAssigner ticketPriorityAssigner;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final EmailService emailService;
    private final UserService userService;
    private final ProductService productService;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final TicketMapper ticketMapper;
    private final ReviewService reviewService;

    private static final String TICKET = "ticket";
    private final LocalStorageService localStorageService;

    private final EntityManager entityManager;

    /**
     * Costruttore del servizio TicketServiceImpl.
     *
     * @param ticketPriorityAssigner il servizio per l'assegnazione della priorità ai ticket.
     * @param userRepository         il repository degli utenti.
     * @param ticketRepository       il repository dei ticket.
     * @param emailService           il servizio per l'invio di email.
     * @param userService            il servizio per la gestione degli utenti.
     * @param productService         il servizio per la gestione dei prodotti.
     * @param reviewRepository       il repository delle recensioni.
     * @param productRepository      il repository dei prodotti.
     * @param ticketMapper           il mapper per la conversione dei ticket.
     * @param reviewService          il servizio per la gestione delle recensioni.
     */
    public TicketServiceImpl(TicketPriorityAssigner ticketPriorityAssigner, UserRepository userRepository, TicketRepository ticketRepository, EmailServiceImpl emailService,
                             UserServiceImpl userService, ProductServiceImpl productService,
                             ReviewRepository reviewRepository, ProductRepository productRepository, TicketMapper ticketMapper, ReviewService reviewService, LocalStorageService localStorageService, EntityManager entityManager) {
        this.ticketPriorityAssigner = ticketPriorityAssigner;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.emailService = emailService;
        this.userService = userService;
        this.productService = productService;
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.ticketMapper = ticketMapper;
        this.reviewService = reviewService;
        this.localStorageService = localStorageService;
        this.entityManager = entityManager;
    }

    /**
     * Crea un nuovo ticket di richiesta per diventare venditore.
     *
     * @param demandSellerRequestDTO i dati del ticket da creare.
     * @param userPhoto              la foto del profilo dell'utente.
     * @param portfolio              la lista delle immagini del portfolio dell'utente.
     * @return la risposta contenente i dettagli del ticket creato.
     * @throws EntityEditException se si verifica un errore durante la modifica dell'utente.
     */
    @Override
    public TicketResponseDTO requestToBeSeller(DemandSellerRequestDTO demandSellerRequestDTO, MultipartFile userPhoto, List<MultipartFile> portfolio) {
        // Retrieve the authenticated user
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = ((User) authentication.getPrincipal());

        user.setFiscalCode(demandSellerRequestDTO.getFiscalCode());
        user.setEducation(demandSellerRequestDTO.getEducation());
        user.setSkills(demandSellerRequestDTO.getSkills());
        user.setBirthDate(demandSellerRequestDTO.getBirthDate());
        user.setBasedIn(demandSellerRequestDTO.getBasedIn());
        user.setLanguages(demandSellerRequestDTO.getLanguages());

        List<String> portfolioImagesList = localStorageService.savePortfolioImages(portfolio, user.getId());
        String userImage = localStorageService.saveUserProfilePhoto(userPhoto);

        user.setUrlUserPhoto(userImage);
        user.setPortfolio(portfolioImagesList);

        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            throw new EntityEditException("utente", "id", user.getId());
        }

        Ticket ticket = ticketMapper.sellerRequestDTOToTicket(demandSellerRequestDTO);
        ticket.setType(TicketType.SELLER_REQUEST);
        ticket.setCreationDate(LocalDateTime.now());
        ticket.setTicketRequester(user);

        PendingState pendingState = new PendingState(emailService);
        ticket.setState(pendingState.getStatusMessage());

        PriorityFlag priority = ticketPriorityAssigner.assignPriorityToTicket(ticket);
        ticket.setPriorityFlag(priority);

        try {
            ticketRepository.save(ticket);
            pendingState.handle(ticket, null);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidEntityDataException(TICKET, "user id", ticket.getTicketRequester().getId(), "L'utente ha già una richiesta di venditore in sospeso.");
        }

        return ticketMapper.ticketToTicketDTO(ticket);
    }

    /**
     * Crea un nuovo ticket di segnalazione per una recensione.
     *
     * @param ticketRequestDTO i dati del ticket da creare.
     * @return la risposta contenente i dettagli del ticket creato.
     * @throws EntityNotFoundException    se la recensione segnalata non esiste.
     * @throws InvalidEntityDataException se l'utente ha già una segnalazione in sospeso per la recensione segnalata.
     */
    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = EmailSendingException.class)
    public TicketResponseDTO reportReviews(TicketRequestDTO ticketRequestDTO) {
        return createReport(ticketRequestDTO, TicketType.REPORT_REVIEWS);
    }

    /**
     * Crea un nuovo ticket di segnalazione per un utente.
     *
     * @param ticketRequestDTO i dati del ticket da creare.
     * @return la risposta contenente i dettagli del ticket creato.
     * @throws EntityNotFoundException    se l'utente segnalato non esiste.
     * @throws InvalidEntityDataException se l'utente ha già una segnalazione in sospeso contro l'utente.
     */
    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = EmailSendingException.class)
    public TicketResponseDTO reportUser(TicketRequestDTO ticketRequestDTO) {
        return createReport(ticketRequestDTO, TicketType.REPORT_USER);
    }

    /**
     * Crea un nuovo ticket di segnalazione per un prodotto.
     *
     * @param ticketRequestDTO i dati del ticket da creare.
     * @return la risposta contenente i dettagli del ticket creato.
     * @throws EntityNotFoundException    se il prodotto segnalato non esiste.
     * @throws InvalidEntityDataException se l'utente ha già una segnalazione in sospeso contro il prodotto.
     */
    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = EmailSendingException.class)
    public TicketResponseDTO reportProduct(TicketRequestDTO ticketRequestDTO) {
        return createReport(ticketRequestDTO, TicketType.REPORT_PRODUCT);
    }

    /**
     * Crea un nuovo ticket di segnalazione.
     *
     * @param ticketRequestDTO i dati del ticket da creare.
     * @param ticketType       il tipo di ticket da creare.
     * @return la risposta contenente i dettagli del ticket creato.
     * @throws EntityNotFoundException    se l'entità segnalata non esiste.
     * @throws InvalidEntityDataException se l'utente ha già una segnalazione in sospeso per l'entità segnalata.
     */ 
    private TicketResponseDTO createReport(TicketRequestDTO ticketRequestDTO, TicketType ticketType) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = ((User) authentication.getPrincipal());

        Ticket ticket = ticketMapper.ticketRequestDTOToTicket(ticketRequestDTO);
        ticket.setTicketRequester(user);
        ticket.setCreationDate(LocalDateTime.now());
        ticket.setType(TicketType.valueOf(ticketType.name()));

        switch (ticketType) {
            case REPORT_REVIEWS -> {
                Review reviewReported = reviewRepository.findById(ticketRequestDTO.getReportedReviewId())
                        .orElseThrow(() -> new EntityNotFoundException("recensione", "ID", ticketRequestDTO.getReportedReviewId()));

                setReportedReview(ticket, reviewReported);
                if (ticketRepository.findByTicketRequesterAndReportedReviewId(user, reviewReported.getId()).isPresent()) {
                    throw new InvalidEntityDataException(TICKET, "ID recensione segnalata", reviewReported.getId(), "L'utente ha già una segnalazione in sospeso per questa recensione.");
                }
            }
            case REPORT_USER -> {
                User userReported = userRepository.findById(ticketRequestDTO.getReportedUserId())
                        .orElseThrow(() -> new EntityNotFoundException("utente", "ID", ticketRequestDTO.getReportedUserId()));

                setReportedUser(ticket, userReported);
                if (ticketRepository.findByTicketRequesterAndReportedUserId(user, userReported.getId()).isPresent()) {
                    throw new InvalidEntityDataException(TICKET, "ID utente segnalato", userReported.getId(), "L'utente ha già una segnalazione in sospeso contro questo utente.");
                }
            }
            case REPORT_PRODUCT -> {
                Product productReported = productRepository.findById(ticketRequestDTO.getReportedProductId())
                        .orElseThrow(() -> new EntityNotFoundException("prodotto", "ID", ticketRequestDTO.getReportedProductId()));

                setReportedProduct(ticket, productReported);
                if (ticketRepository.findByTicketRequesterAndReportedProductId(user, productReported.getId()).isPresent()) {
                    throw new InvalidEntityDataException(TICKET, "ID prodotto segnalato", productReported.getId(), "L'utente ha già una segnalazione in sospeso contro questo prodotto.");
                }
            }
        }

        PendingState pendingState = new PendingState(emailService);
        ticket.setState(pendingState.getStatusMessage());

        PriorityFlag priority = ticketPriorityAssigner.assignPriorityToTicket(ticket);
        ticket.setPriorityFlag(priority);

        try {
            ticket = ticketRepository.save(ticket);
            pendingState.handle(ticket, null);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidEntityDataException(TICKET, "ID utente", ticket.getTicketRequester().getId(), "L'utente ha già una segnalazione in sospeso.");
        }

        return ticketMapper.ticketToTicketDTO(ticket);
    }

    /**
     * Imposta la recensione segnalata nel ticket.
     *
     * @param ticket il ticket in cui impostare la recensione segnalata.
     * @param review la recensione segnalata.
     */
    private void setReportedReview(Ticket ticket, Review review) {
        ticket.setReportedReview(review);
    }

    /**
     * Imposta l'utente segnalato nel ticket.
     *
     * @param ticket il ticket in cui impostare l'utente segnalato.
     * @param user   l'utente segnalato.
     */
    private void setReportedUser(Ticket ticket, User user) {
        ticket.setReportedUser(user);
    }

    /**
     * Imposta il prodotto segnalato nel ticket.
     *
     * @param ticket  il ticket in cui impostare il prodotto segnalato.
     * @param product il prodotto segnalato.
     */
    private void setReportedProduct(Ticket ticket, Product product) {
        ticket.setReportedProduct(product);
    }

    /**
     * Restituisce un ticket per ID.
     *
     * @param id l'ID del ticket da cercare.
     * @return la risposta contenente i dettagli del ticket trovato.
     * @throws EntityNotFoundException se il ticket con l'ID fornito non esiste.
     */
    @Override
    @Transactional(readOnly = true)
    public TicketResponseDTO getTicketById(long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TICKET, "ID", id));

        return ticketMapper.ticketToTicketDTO(ticket);
    }

    /**
     * Restituisce tutti i ticket.
     *
     * @return la lista di tutti i ticket.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TicketResponseDTO> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return ticketMapper.ticketsToTicketDTOs(tickets);
    }

    /**
     * Accetta un ticket.
     *
     * @param id l'ID del ticket da accettare.
     * @return la risposta contenente i dettagli del ticket accettato.
     * @throws EntityNotFoundException se il ticket con l'ID fornito non esiste.
     * @throws TicketActionException   se il ticket non può essere accettato.
     * @throws EmailSendingException   se si verifica un errore durante l'invio dell'email di notifica.
     * @throws EntityEditException     se si verifica un errore durante la modifica del ticket.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TicketResponseDTO acceptTicket(long id, String responseDescriptionEmail) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TICKET, "ID", id));

        if (ticket.getStateTicket() instanceof AcceptedState) {
            throw new TicketActionException(id, "accettare", ticket.getState());
        }

        if (!"In lavorazione".equals(ticket.getState())) {
            throw new TicketActionException(id, "accettare", ticket.getState());
        }

        AcceptedState acceptedState = new AcceptedState(emailService, userService, productService, reviewService);
        ticket.setState(acceptedState.getStatusMessage());

        PriorityFlag priority = ticketPriorityAssigner.assignPriorityToTicket(ticket);
        ticket.setPriorityFlag(priority);

        if (ticket.getType() == TicketType.SELLER_REQUEST) {
            User user = ticket.getTicketRequester();

            if (user.getRole() != Role.SELLER) {
                user.setRole(Role.SELLER);

                try {
                    userRepository.save(user);
                } catch (Exception e) {
                    throw new EntityEditException("utente", "ID", user.getId());
                }
            }
        }

        try {
            ticketRepository.save(ticket);
        } catch (Exception e) {
            throw new EntityEditException(TICKET, "id", id);
        }

        // Tenta di inviare l'email, ignorando qualsiasi eccezione
        try {
            acceptedState.handle(ticket, responseDescriptionEmail);
        } catch (Exception e) {
            // Ignora l'eccezione senza log o gestione ulteriore
        }

        // Ritorna la risposta immediatamente senza aspettare l'invio delle email
        return ticketMapper.ticketToTicketDTO(ticket);
    }


    /**
     * Rifiuta un ticket.
     *
     * @param id l'ID del ticket da rifiutare.
     * @return la risposta contenente i dettagli del ticket rifiutato.
     * @throws EntityNotFoundException se il ticket con l'ID fornito non esiste.
     * @throws TicketActionException   se il ticket non può essere rifiutato.
     * @throws EmailSendingException   se si verifica un errore durante l'invio dell'email di notifica.
     * @throws EntityEditException     se si verifica un errore durante la modifica del ticket.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TicketResponseDTO refuseTicket(long id, String responseDescriptionEmail) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TICKET, "id", id));

        if (ticket.getStateTicket() instanceof RefuseState) {
            throw new TicketActionException(id, "rifiutare", ticket.getState());
        }

        if (!"In lavorazione".equals(ticket.getState())) {
            throw new TicketActionException(id, "rifiutare", ticket.getState());
        }

        // Imposta il nuovo stato come rifiutato
        RefuseState refuseState = new RefuseState(emailService);
        ticket.setState(refuseState.getStatusMessage());

        // Assegna una priorità
        PriorityFlag priority = ticketPriorityAssigner.assignPriorityToTicket(ticket);
        ticket.setPriorityFlag(priority);

        try {
            // Salva il ticket aggiornato
            ticket = ticketRepository.save(ticket);
        } catch (Exception e) {
            throw new EntityEditException(TICKET, "id", id);
        }

        // Tenta di inviare l'email, ignorando qualsiasi eccezione
        try {
            refuseState.handle(ticket, responseDescriptionEmail);
        } catch (Exception e) {
            // Ignora l'eccezione senza log o gestione ulteriore
        }

        // Restituisci i dati aggiornati
        return ticketMapper.ticketToTicketDTO(ticket);
    }

    /**
     * Prende in carico un ticket.
     *
     * @param id l'ID del ticket da prendere in carico.
     * @return la risposta contenente i dettagli del ticket preso in carico.
     * @throws EntityNotFoundException se il ticket con l'ID fornito non esiste.
     * @throws TicketActionException   se il ticket non può essere preso in carico.
     * @throws EmailSendingException   se si verifica un errore durante l'invio dell'email di notifica.
     * @throws EntityEditException     se si verifica un errore durante la modifica del ticket.
     */
    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = EmailSendingException.class)
    public TicketResponseDTO takeOnTicket(long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TICKET, "id", id));

        if (ticket.getStateTicket() instanceof TakeOnState) {
            throw new TicketActionException(id, "prendere in carico", ticket.getState());
        }

        if (!"In attesa".equals(ticket.getState())) {
            throw new TicketActionException(id, "prendere in carico", ticket.getState());
        }

        TakeOnState takeOnState = new TakeOnState(emailService);
        ticket.setState(takeOnState.getStatusMessage());

        PriorityFlag priority = ticketPriorityAssigner.assignPriorityToTicket(ticket);
        ticket.setPriorityFlag(priority);


        try {
            ticket = ticketRepository.save(ticket);
        } catch (Exception e) {
            throw new EntityEditException(TICKET, "id", id);
        }

        // Tenta di inviare l'email, ignorando qualsiasi eccezione
        try {
            takeOnState.handle(ticket, null);
        } catch (Exception e) {
            // Ignora l'eccezione senza log o gestione ulteriore
        }

        return ticketMapper.ticketToTicketDTO(ticket);
    }

    @Override
    public List<TicketResponseDTO> getResolvedTickets() {
        List<Ticket> resolvedTickets = ticketRepository.findByStateAcceptedOrRefused();
        return ticketMapper.ticketsToTicketDTOs(resolvedTickets);
    }

    @Override
    public List<TicketResponseDTO> getTakenOnTickets() {
        List<Ticket> takenOnTickets = ticketRepository.findByStateTakeOn();
        return ticketMapper.ticketsToTicketDTOs(takenOnTickets);
    }

    @Override
    public List<TicketResponseDTO> getPendingTickets() {
        List<Ticket> pendingTickets = ticketRepository.findByStatePending();
        return ticketMapper.ticketsToTicketDTOs(pendingTickets);
    }

    /**
     * Restituisce i ticket filtrati.
     *
     * @param ticketFilterRequest i filtri da applicare ai ticket.
     * @return la lista di ticket filtrati.
     */
    public FilteredTicketsResponse getTicketFiltered(TicketFilterRequest ticketFilterRequest) {
        // Inizializza l'EntityManager e CriteriaBuilder per costruire la query dinamica
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ticket> criteriaQuery = criteriaBuilder.createQuery(Ticket.class);
        Root<Ticket> root = criteriaQuery.from(Ticket.class);

        // Costruisce le condizioni di filtro basate sul filtro ricevuto
        List<Predicate> predicates = buildPredicates(ticketFilterRequest, criteriaBuilder, root);

        // Applica i predicati se ce ne sono
        if (!predicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        }

        // Aggiungi la logica di ordinamento (se presente)
        applySorting(ticketFilterRequest, criteriaBuilder, criteriaQuery, root);

        // Esegui la query per ottenere i ticket filtrati
        TypedQuery<Ticket> query = entityManager.createQuery(criteriaQuery);
        List<Ticket> tickets = query.getResultList();

        // Mappa i ticket da entità a DTO
        List<TicketResponseDTO> ticketDTOs = ticketMapper.ticketsToTicketDTOs(tickets);

        // Dividi i ticket in base allo stato
        List<TicketResponseDTO> pendingTickets = ticketDTOs.stream()
                .filter(ticket -> "In attesa".equals(ticket.getState()))
                .toList();
        List<TicketResponseDTO> resolvedTickets = ticketDTOs.stream()
                .filter(ticket -> "Accettato".equals(ticket.getState()) || "Rifiutato".equals(ticket.getState()))
                .toList();
        List<TicketResponseDTO> takeOnTickets = ticketDTOs.stream()
                .filter(ticket -> "In lavorazione".equals(ticket.getState()))
                .toList();

        // Crea la risposta finale con tutti i gruppi di ticket
        FilteredTicketsResponse response = new FilteredTicketsResponse();
        response.setAllTickets(ticketDTOs);  // Tutti i ticket filtrati
        response.setPendingTickets(pendingTickets);
        response.setResolvedTickets(resolvedTickets);
        response.setTakeOnTickets(takeOnTickets);

        return response;
    }

    private List<Predicate> buildPredicates(TicketFilterRequest ticketFilterRequest, CriteriaBuilder criteriaBuilder, Root<Ticket> root) {
        List<Predicate> predicates = new ArrayList<>();

        // Aggiungi il filtro sul tipo di ticket (TicketType)
        if (ticketFilterRequest.getTicketTypes() != null && !ticketFilterRequest.getTicketTypes().isEmpty()) {
            // Filtro per più tipi di ticket usando IN
            CriteriaBuilder.In<TicketType> inClause = criteriaBuilder.in(root.get("type"));
            for (TicketType ticketType : ticketFilterRequest.getTicketTypes()) {
                inClause.value(ticketType);  // Aggiungi ogni tipo di ticket all'IN clause
            }
            predicates.add(inClause);  // Aggiungi la condizione IN alla lista dei predicati
        }

        // Aggiungi il filtro sulla priorità
        if (ticketFilterRequest.getPriority() != null) {
            predicates.add(criteriaBuilder.equal(root.get("priorityFlag"), ticketFilterRequest.getPriority()));
        }

        // Aggiungi il filtro sul testo di ricerca
        addSearchTextPredicate(ticketFilterRequest, criteriaBuilder, root, predicates);

        // Aggiungi il filtro per l'intervallo di date
        addDateRangePredicate(ticketFilterRequest, criteriaBuilder, root, predicates);

        return predicates;
    }

    private void addSearchTextPredicate(TicketFilterRequest request, CriteriaBuilder criteriaBuilder, Root<Ticket> root, List<Predicate> predicates) {
        if (request.getSearchText() != null && !request.getSearchText().isEmpty()) {
            String searchPattern = "%" + request.getSearchText().toLowerCase() + "%";
            Predicate titleLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern);
            Predicate descriptionLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern);
            predicates.add(criteriaBuilder.or(titleLike, descriptionLike));
        }
    }

    public void addDateRangePredicate(TicketFilterRequest request, CriteriaBuilder criteriaBuilder, Root<Ticket> root, List<Predicate> predicates) {
        if (request.getDateRangeType() != null) {
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            LocalDate now = LocalDate.now();

            switch (request.getDateRangeType()) {
                case "THIS_WEEK":
                    startDateTime = now.with(DayOfWeek.MONDAY).atStartOfDay();
                    endDateTime = now.with(DayOfWeek.SUNDAY).atTime(23, 59, 59);
                    break;
                case "THIS_MONTH":
                    startDateTime = now.withDayOfMonth(1).atStartOfDay();
                    endDateTime = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);
                    break;
                case "TODAY":
                    startDateTime = now.atStartOfDay();
                    endDateTime = now.atTime(23, 59, 59);
                    break;
                case "ALWAYS":
                default:
                    break;
            }

            if (startDateTime != null) {
                predicates.add(criteriaBuilder.between(root.get("creationDate"), startDateTime, endDateTime));
            }
        }
    }

    private void applySorting(TicketFilterRequest request, CriteriaBuilder criteriaBuilder, CriteriaQuery<Ticket> criteriaQuery, Root<Ticket> root) {
        if (request.getSortByCreationDate() != null) {
            if ("ASC".equalsIgnoreCase(request.getSortByCreationDate())) {
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("creationDate")));
            } else if ("DESC".equalsIgnoreCase(request.getSortByCreationDate())) {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("creationDate")));
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updatePriorities() {
        List<Ticket> tickets = ticketRepository.findAllOpenTickets(); // Trova solo i ticket aperti
        for (Ticket ticket : tickets) {
            PriorityFlag newPriority = ticketPriorityAssigner.assignPriorityToTicket(ticket);
            if (!ticket.getPriorityFlag().equals(newPriority)) { // Aggiorna solo se cambia la priorità
                ticket.setPriorityFlag(newPriority);
                ticketRepository.save(ticket);
            }
        }
    }
}
