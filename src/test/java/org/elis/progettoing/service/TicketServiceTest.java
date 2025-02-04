package org.elis.progettoing.service;

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
import org.elis.progettoing.exception.TicketActionException;
import org.elis.progettoing.exception.entity.EntityEditException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.exception.entity.InvalidEntityDataException;
import org.elis.progettoing.mapper.implementation.TicketMapperImpl;
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
import org.elis.progettoing.service.implementation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {
    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TicketMapperImpl ticketMapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Mock
    private LocalStorageService localStorageService;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private TicketPriorityAssigner ticketPriorityAssigner;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ProductServiceImpl productService;

    @Mock
    private ReviewServiceImpl reviewService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UsernamePasswordAuthenticationToken authentication;

    @Mock
    private User user;

    @Mock
    private MultipartFile mockUserPhoto;

    @Mock
    private MultipartFile mockPortfolioFile;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Ticket> criteriaQuery;

    @Mock
    private Root<Ticket> root;

    @Mock
    private TypedQuery<Ticket> typedQuery;

    @Mock
    private Ticket ticket;

    @Mock
    private TicketResponseDTO ticketResponseDTO;

    @Mock
    private TicketFilterRequest ticketFilterRequest;
    private Ticket ticket1, ticket2;
    private TicketResponseDTO ticketDTO1, ticketDTO2;
    private String description;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticket.setState("In lavorazione");
        ticket.setType(TicketType.SELLER_REQUEST);

        user = new User();
        user.setRole(Role.BUYER);

        ticket1 = new Ticket();
        ticket1.setState("In attesa");
        ticket1.setTitle("Ticket 1");
        ticket1.setDescription("Description 1");

        ticket2 = new Ticket();
        ticket2.setState("Accettato");
        ticket2.setTitle("Ticket 2");
        ticket2.setDescription("Description 2");

        ticketDTO1 = new TicketResponseDTO();
        ticketDTO1.setState("In attesa");
        ticketDTO1.setTitle("Ticket 1");
        ticketDTO1.setDescription("Description 1");

        ticketDTO2 = new TicketResponseDTO();
        ticketDTO2.setState("Accettato");
        ticketDTO2.setTitle("Ticket 2");
        ticketDTO2.setDescription("Description 2");

        description = "Description";

        ticketFilterRequest = new TicketFilterRequest();

        ticketResponseDTO = new TicketResponseDTO();
    }

    @Test
    void testRequestToBeSeller_Success() {
        user.setId(1L);

        // Arrange
        DemandSellerRequestDTO demandSellerRequestDTO = new DemandSellerRequestDTO();
        demandSellerRequestDTO.setFiscalCode("ABC123XYZ");
        demandSellerRequestDTO.setEducation("Bachelor's Degree");
        demandSellerRequestDTO.setSkills(List.of("Java", "Spring Boot"));
        demandSellerRequestDTO.setBirthDate(LocalDate.of(1990, 1, 1));

        List<MultipartFile> portfolio = List.of(mockPortfolioFile);
        List<String> portfolioUrls = List.of("portfolio/image1.jpg");
        String userPhotoUrl = "user/photo.jpg";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);

        // Mock localStorageService calls
        when(localStorageService.savePortfolioImages(portfolio, 1L)).thenReturn(portfolioUrls);
        when(localStorageService.saveUserProfilePhoto(mockUserPhoto)).thenReturn(userPhotoUrl);

        // Mock userRepository save
        when(userRepository.save(user)).thenReturn(user);

        // Mock ticket creation
        ticket.setId(1L);
        when(ticketMapper.sellerRequestDTOToTicket(demandSellerRequestDTO)).thenReturn(ticket);

        // Mock ticket priority
        when(ticketPriorityAssigner.assignPriorityToTicket(ticket)).thenReturn(PriorityFlag.HIGH);

        // Mock ticketMapper conversion
        TicketResponseDTO responseDTO = new TicketResponseDTO();
        when(ticketMapper.ticketToTicketDTO(ticket)).thenReturn(responseDTO);

        // Act
        TicketResponseDTO result = ticketService.requestToBeSeller(demandSellerRequestDTO, mockUserPhoto, portfolio);

        // Assert
        assertNotNull(result);
        verify(localStorageService, times(1)).savePortfolioImages(portfolio, 1L);
        verify(localStorageService, times(1)).saveUserProfilePhoto(mockUserPhoto);
        verify(userRepository, times(1)).save(user);
        verify(ticketRepository, times(1)).save(ticket);
        verify(emailService, times(1)).sendSellerRequestDemand(any());
    }

    @Test
    void testRequestToBeSeller_WhenUserSaveFails_ThrowsEntityEditException() {
        user.setId(1L);
        user.setRole(Role.BUYER);

        // Arrange
        DemandSellerRequestDTO demandSellerRequestDTO = new DemandSellerRequestDTO();
        List<MultipartFile> portfolio = List.of(mockPortfolioFile);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);

        // Mock localStorageService
        when(localStorageService.savePortfolioImages(portfolio, 1L)).thenReturn(List.of("portfolio/image1.jpg"));
        when(localStorageService.saveUserProfilePhoto(mockUserPhoto)).thenReturn("user/photo.jpg");

        // Simulate user save failure
        when(userRepository.save(user)).thenThrow(new RuntimeException());

        // Act & Assert
        assertThrows(EntityEditException.class, () -> ticketService.requestToBeSeller(demandSellerRequestDTO, mockUserPhoto, portfolio));

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRequestToBeSeller_WhenDuplicateSellerRequest_ThrowsInvalidEntityDataException() {
        // Arrange
        DemandSellerRequestDTO demandSellerRequestDTO = new DemandSellerRequestDTO();
        ticket.setType(TicketType.SELLER_REQUEST);
        ticket.setTicketRequester(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);

        // Mock user save
        when(userRepository.save(user)).thenReturn(user);

        // Mock ticketMapper and repository behavior
        when(ticketMapper.sellerRequestDTOToTicket(demandSellerRequestDTO)).thenReturn(ticket);
        doThrow(new DataIntegrityViolationException("Duplicate seller request")).when(ticketRepository).save(ticket);

        List<MultipartFile> portfolio = List.of(mockPortfolioFile);

        // Act & Assert
        assertThrows(InvalidEntityDataException.class, () -> ticketService.requestToBeSeller(demandSellerRequestDTO, mockUserPhoto, portfolio));

        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void testRequestToBeSeller_WhenPortfolioSaveFails_ThrowsException() {
        user.setId(1L);
        user.setRole(Role.BUYER);

        // Arrange
        DemandSellerRequestDTO demandSellerRequestDTO = new DemandSellerRequestDTO();
        List<MultipartFile> portfolio = List.of(mockPortfolioFile);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);

        // Simulate failure in saving portfolio images
        when(localStorageService.savePortfolioImages(portfolio, 1L)).thenThrow(new RuntimeException("Portfolio save failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> ticketService.requestToBeSeller(demandSellerRequestDTO, mockUserPhoto, portfolio));

        verify(localStorageService, times(1)).savePortfolioImages(portfolio, 1L);
        verifyNoInteractions(ticketRepository);
    }

    @Test
    void testGetTicketById_WhenTicketExists() {
        // Arrange
        long ticketId = 1L;

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketMapper.ticketToTicketDTO(ticket)).thenReturn(ticketResponseDTO);

        // Act
        TicketResponseDTO result = ticketService.getTicketById(ticketId);

        // Assert
        assertNotNull(result);
        assertEquals(ticketResponseDTO, result);
        verify(ticketRepository, times(1)).findById(ticketId); // Verifica che il repository venga chiamato una sola volta
        verify(ticketMapper, times(1)).ticketToTicketDTO(ticket); // Verifica che il mapper venga chiamato
    }

    @Test
    void testGetTicketById_WhenTicketDoesNotExist() {
        // Arrange
        long ticketId = 1L;

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> ticketService.getTicketById(ticketId));
        assertEquals("Nessun ticket con ID = 1 è stato trovato.", exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId); // Verifica che il repository venga chiamato una sola volta
    }

    @Test
    void testGetAllTickets_WhenTicketsExist() {
        List<Ticket> ticketList = Arrays.asList(ticket1, ticket2); // Lista di ticket di test
        TicketResponseDTO ticketResponseDTO1 = new TicketResponseDTO(); // DTO per il primo ticket
        TicketResponseDTO ticketResponseDTO2 = new TicketResponseDTO(); // DTO per il secondo ticket
        List<TicketResponseDTO> ticketDTOList = Arrays.asList(ticketResponseDTO1, ticketResponseDTO2); // Lista di DTO di test

        when(ticketRepository.findAll()).thenReturn(ticketList);
        when(ticketMapper.ticketsToTicketDTOs(ticketList)).thenReturn(ticketDTOList);

        // Act
        List<TicketResponseDTO> result = ticketService.getAllTickets();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Verifica che la lista di ticket DTO abbia 2 elementi
        assertEquals(ticketDTOList, result); // Verifica che il risultato sia uguale alla lista di DTO attesi
        verify(ticketRepository, times(1)).findAll(); // Verifica che il repository venga chiamato una sola volta
        verify(ticketMapper, times(1)).ticketsToTicketDTOs(ticketList); // Verifica che il mapper venga chiamato una sola volta
    }

    @Test
    void testGetAllTickets_WhenNoTicketsExist() {
        // Arrange
        List<Ticket> ticketList = List.of(); // Lista vuota di ticket

        when(ticketRepository.findAll()).thenReturn(ticketList);

        // Act
        List<TicketResponseDTO> result = ticketService.getAllTickets();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty()); // La lista risultante deve essere vuota
        verify(ticketRepository, times(1)).findAll(); // Verifica che il repository venga chiamato una sola volta
    }

    @Test
    void testAcceptTicket_WhenTicketNotFound() {
        // Arrange
        long ticketId = 1L;
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> ticketService.acceptTicket(ticketId, description));
        assertEquals("Nessun ticket con ID = 1 è stato trovato.", exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @Test
    void testAcceptTicket_WhenTicketAlreadyAccepted() {
        // Arrange
        long ticketId = 1L;
        AcceptedState acceptedState = new AcceptedState(emailService, userService, productService, reviewService);
        ticket.setState(acceptedState.getStatusMessage()); // Stato già accettato
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act & Assert
        TicketActionException exception = assertThrows(TicketActionException.class, () -> ticketService.acceptTicket(ticketId, description));
        assertEquals("Errore nell'esecuzione dell'azione 'accettare' per il ticket con ID: 1. Stato attuale: 'Accettato'.", exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @Test
    void testAcceptTicket_WhenTicketStateIsNotInProgress() {
        // Arrange
        long ticketId = 1L;
        ticket.setState("Closed"); // Stato non valido
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act & Assert
        TicketActionException exception = assertThrows(TicketActionException.class, () -> ticketService.acceptTicket(ticketId, description));
        assertEquals("Errore nell'esecuzione dell'azione 'accettare' per il ticket con ID: 1. Stato attuale: 'Closed'.", exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @Test
    void testAcceptTicket_WhenSuccess() {
        ticketResponseDTO.setId(1L);
        user.setId(1L);
        user.setRole(Role.BUYER); // Imposta il ruolo dell'utente

        // Arrange
        long ticketId = 1L;
        ticket.setState("In lavorazione"); // Stato valido per l'accettazione
        ticket.setTicketRequester(user); // Associa l'utente al ticket
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketPriorityAssigner.assignPriorityToTicket(ticket)).thenReturn(PriorityFlag.HIGH);
        when(ticketMapper.ticketToTicketDTO(ticket)).thenReturn(ticketResponseDTO);

        // Act
        TicketResponseDTO result = ticketService.acceptTicket(ticketId, description);

        // Assert
        assertNotNull(result);
        assertEquals(ticketResponseDTO, result);
        assertEquals("Accettato", ticket.getState()); // Verifica che lo stato del ticket sia cambiato
        assertEquals(PriorityFlag.HIGH, ticket.getPriorityFlag()); // Verifica che la priorità sia stata assegnata
        verify(ticketRepository, times(1)).findById(ticketId); // Verifica che il metodo findById sia stato chiamato
        verify(ticketRepository, times(1)).save(ticket); // Verifica che il ticket venga salvato
        verify(ticketPriorityAssigner, times(1)).assignPriorityToTicket(ticket); // Verifica che la priorità venga assegnata
        verify(ticketMapper, times(1)).ticketToTicketDTO(ticket); // Verifica la mappatura
    }

    @Test
    void testAcceptTicket_WhenUserRoleNeedsUpdate() {
        // Arrange
        long ticketId = 1L;
        ticket.setState("In lavorazione");
        ticket.setTicketRequester(user);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketPriorityAssigner.assignPriorityToTicket(ticket)).thenReturn(PriorityFlag.HIGH);
        when(ticketMapper.ticketToTicketDTO(ticket)).thenReturn(ticketResponseDTO);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        TicketResponseDTO result = ticketService.acceptTicket(ticketId, description);

        // Assert
        assertNotNull(result);
        assertEquals(ticketResponseDTO, result);
        assertEquals(Role.SELLER, user.getRole()); // Verifica che il ruolo dell'utente sia stato aggiornato
        verify(userRepository, times(1)).save(user); // Verifica che il salvataggio dell'utente venga effettuato
    }

    @Test
    void testAcceptTicket_WhenTicketSaveFails() {
        // Arrange
        long ticketId = 1L;
        ticket.setState("In lavorazione");
        ticket.setTicketRequester(user); // Associate the user with the ticket

        // Simulate the ticket repository behavior
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        EntityEditException exception = assertThrows(EntityEditException.class, () -> ticketService.acceptTicket(ticketId, description));

        assertEquals("Si è verificato un errore nell'aggiornamento dell'entità id con ticket = 1.", exception.getMessage());

        // Verify interactions
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, times(1)).save(ticket); // Verify the save method is called
    }

    @Test
    void testAcceptTicket_WhenEmailSendingFails() {
        // Arrange
        long ticketId = 1L;
        ticket.setState("In lavorazione");

        // Crea un oggetto User simulato (mockato)
        user = new User();
        user.setRole(Role.BUYER);  // Imposta il ruolo dell'utente
        ticket.setTicketRequester(user);  // Associa l'utente al ticket

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketPriorityAssigner.assignPriorityToTicket(ticket)).thenReturn(PriorityFlag.HIGH);
        when(ticketMapper.ticketToTicketDTO(ticket)).thenReturn(ticketResponseDTO);

        // Act
        TicketResponseDTO result = ticketService.acceptTicket(ticketId, description);

        // Assert
        assertNotNull(result);
        assertEquals(ticketResponseDTO, result);
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, times(1)).save(ticket);
        verify(ticketPriorityAssigner, times(1)).assignPriorityToTicket(ticket);
        verify(ticketMapper, times(1)).ticketToTicketDTO(ticket);
    }

    @Test
    void testAcceptTicket_WhenEntityEditExceptionIsThrown() {
        // Arrange
        long ticketId = 1L;
        ticket.setId(ticketId);
        ticket.setState("In lavorazione");
        ticket.setType(TicketType.SELLER_REQUEST); // Simula che sia un ticket di tipo SELLER_REQUEST

        user.setId(1L);
        user.setRole(Role.BUYER); // Imposta il ruolo dell'utente a BUYER
        ticket.setTicketRequester(user); // Associa l'utente al ticket

        // Simula il comportamento del repository
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Simula il comportamento del repository dell'utente fallendo nel salvataggio
        doThrow(new RuntimeException("Database error")).when(userRepository).save(any(User.class));

        // Act & Assert
        EntityEditException exception = assertThrows(EntityEditException.class, () -> ticketService.acceptTicket(ticketId, description));

        // Verifica che l'eccezione sia stata lanciata con il messaggio corretto
        assertEquals("Si è verificato un errore nell'aggiornamento dell'entità ID con utente = 1.", exception.getMessage());

        // Verifica che il ticketRepository.findById e userRepository.save siano stati chiamati
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(userRepository, times(1)).save(user); // Verifica che sia stato tentato di salvare l'utente
    }

    @Test
    void testAcceptTicket_WhenTicketIsAlreadyAccepted() {
        // Arrange
        long ticketId = 1L;
        ticket.setId(ticketId);
        ticket.setState("Accepted");

        // Simula lo stato del ticket come AcceptedState
        AcceptedState acceptedState = mock(AcceptedState.class);
        ticket.setStateTicket(acceptedState);  // Imposta il ticket come già accettato

        // Simula il comportamento del repository per il recupero del ticket
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act & Assert
        TicketActionException exception = assertThrows(TicketActionException.class, () -> ticketService.acceptTicket(ticketId, description));

        // Verifica che l'eccezione sia stata lanciata con il messaggio corretto
        assertEquals("Errore nell'esecuzione dell'azione 'accettare' per il ticket con ID: 1. Stato attuale: 'Accepted'.", exception.getMessage());

        // Verifica che il repository sia stato chiamato per trovare il ticket
        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @Test
    void testRefuseTicket_WhenTicketNotFound() {
        // Arrange
        long ticketId = 1L;
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> ticketService.refuseTicket(ticketId, description));
        assertEquals("Nessun ticket con id = 1 è stato trovato.", exception.getMessage());

        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @Test
    void testRefuseTicket_WhenTicketAlreadyRefused() {
        // Arrange
        long ticketId = 1L;
        ticket.setId(ticketId);
        ticket.setState("Rifiutato");

        RefuseState refuseState = new RefuseState(emailService);
        ticket.setStateTicket(refuseState);  // Imposta lo stato a RefuseState (già rifiutato)

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act & Assert
        TicketActionException exception = assertThrows(TicketActionException.class, () -> ticketService.refuseTicket(ticketId, description));
        assertEquals("Errore nell'esecuzione dell'azione 'rifiutare' per il ticket con ID: 1. Stato attuale: 'Rifiutato'.", exception.getMessage());

        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @Test
    void testRefuseTicket_WhenTicketStateIsNotInLavorazione() {
        // Arrange
        long ticketId = 1L;
        ticket.setId(ticketId);
        ticket.setState("Closed"); // Stato non valido

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act & Assert
        TicketActionException exception = assertThrows(TicketActionException.class, () -> ticketService.refuseTicket(ticketId, description));
        assertEquals("Errore nell'esecuzione dell'azione 'rifiutare' per il ticket con ID: 1. Stato attuale: 'Closed'.", exception.getMessage());

        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @Test
    void testRefuseTicket_WhenTicketSaveFails() {
        // Arrange
        long ticketId = 1L;
        ticket.setId(ticketId);
        ticket.setState("In lavorazione");

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        doThrow(new RuntimeException("Database error")).when(ticketRepository).save(any(Ticket.class));

        // Act & Assert
        EntityEditException exception = assertThrows(EntityEditException.class, () -> ticketService.refuseTicket(ticketId, description));
        assertEquals("Si è verificato un errore nell'aggiornamento dell'entità id con ticket = 1.", exception.getMessage());

        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, times(1)).save(ticket); // Verifica che il tentativo di salvataggio fallisca
    }

    @Test
    void testRefuseTicket_WhenEmailSendingFails() {
        // Arrange
        long ticketId = 1L;

        user.setName("Mario");
        user.setSurname("Rossi");
        user.setEmail("example@mail.com");
        user.setRole(Role.BUYER);

        ticket.setId(ticketId);
        ticket.setState("In lavorazione");
        ticket.setTitle("Ticket 1");
        ticket.setDescription("Description 1");
        ticket.setType(TicketType.SELLER_REQUEST);
        ticket.setTicketRequester(user);

        // Configura il TicketResponseDTO atteso
        ticketResponseDTO.setId(ticketId);
        ticketResponseDTO.setUserName("Mario");
        ticketResponseDTO.setUserSurname("Rossi");
        ticketResponseDTO.setState("Rifiutato");
        ticketResponseDTO.setPriorityFlag(PriorityFlag.HIGH.name());

        // Stub del repository, priority assigner e mapper
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket); // Ritorna il ticket salvato
        when(ticketPriorityAssigner.assignPriorityToTicket(ticket)).thenReturn(PriorityFlag.HIGH);

        // Stub del mapper con il ticket aggiornato
        doReturn(ticketResponseDTO).when(ticketMapper).ticketToTicketDTO(ticket);

        // Act
        TicketResponseDTO result = ticketService.refuseTicket(ticketId, description);

        // Assert
        assertNotNull(result);
        assertEquals(ticketResponseDTO, result);
        assertEquals("Rifiutato", ticket.getState());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, times(1)).save(ticket);
        verify(ticketPriorityAssigner, times(1)).assignPriorityToTicket(ticket);
        verify(ticketMapper, times(1)).ticketToTicketDTO(ticket);
    }

    @Test
    void testRefuseTicket_WhenSuccess() {
        // Arrange
        long ticketId = 1L;

        user.setName("Mario");
        user.setSurname("Rossi");
        user.setEmail("example@mail.com");
        user.setRole(Role.BUYER);

        ticket.setId(ticketId);
        ticket.setState("In lavorazione");
        ticket.setTitle("Ticket 1");
        ticket.setDescription("Description 1");
        ticket.setType(TicketType.SELLER_REQUEST);
        ticket.setTicketRequester(user);

        // TicketResponseDTO
        ticketResponseDTO.setId(ticketId);
        ticketResponseDTO.setUserName("Mario");
        ticketResponseDTO.setUserSurname("Rossi");
        ticketResponseDTO.setState("Rifiutato");
        ticketResponseDTO.setPriorityFlag(PriorityFlag.HIGH.name());

        // Stub del repository, priority assinger e mapper
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket); // Importante restituire l'oggetto salvato
        when(ticketPriorityAssigner.assignPriorityToTicket(ticket)).thenReturn(PriorityFlag.HIGH);
        when(ticketMapper.ticketToTicketDTO(ticket)).thenReturn(ticketResponseDTO);

        // Act
        TicketResponseDTO result = ticketService.refuseTicket(ticketId, description);

        // Assert
        assertNotNull(result);
        assertEquals(ticketResponseDTO, result);
        assertEquals("Rifiutato", ticket.getState());  // Verifica che lo stato del ticket sia cambiato in "Rifiutato"
        assertEquals(PriorityFlag.HIGH, ticket.getPriorityFlag());  // Verifica che la priorità sia stata assegnata

        // Verifiche delle interazioni
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, times(1)).save(ticket);
        verify(ticketPriorityAssigner, times(1)).assignPriorityToTicket(ticket);
        verify(ticketMapper, times(1)).ticketToTicketDTO(ticket);
    }

    @Test
    void testGetResolvedTickets() {
        // Arrange
        List<Ticket> resolvedTickets = new ArrayList<>();
        ticket1.setState("Accepted");
        resolvedTickets.add(ticket1);

        ticket2.setState("Refused");
        resolvedTickets.add(ticket2);

        // TicketResponseDTO simulato per ogni ticket
        TicketResponseDTO ticketResponseDTO1 = new TicketResponseDTO();
        TicketResponseDTO ticketResponseDTO2 = new TicketResponseDTO();
        List<TicketResponseDTO> ticketResponseDTOList = Arrays.asList(ticketResponseDTO1, ticketResponseDTO2);

        // Simula il comportamento di ticketRepository
        when(ticketRepository.findByStateAcceptedOrRefused()).thenReturn(resolvedTickets);
        when(ticketMapper.ticketsToTicketDTOs(resolvedTickets)).thenReturn(ticketResponseDTOList);

        // Act
        List<TicketResponseDTO> result = ticketService.getResolvedTickets();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ticketResponseDTOList, result);  // Verifica che il risultato sia quello atteso
        verify(ticketRepository, times(1)).findByStateAcceptedOrRefused();
        verify(ticketMapper, times(1)).ticketsToTicketDTOs(resolvedTickets);
    }

    @Test
    void testGetTakenOnTickets() {
        // Arrange
        List<Ticket> takenOnTickets = new ArrayList<>();
        ticket1.setState("Taken On");
        takenOnTickets.add(ticket1);

        // TicketResponseDTO simulato per il ticket
        List<TicketResponseDTO> ticketResponseDTOList = Collections.singletonList(ticketResponseDTO);

        // Simula il comportamento di ticketRepository
        when(ticketRepository.findByStateTakeOn()).thenReturn(takenOnTickets);
        when(ticketMapper.ticketsToTicketDTOs(takenOnTickets)).thenReturn(ticketResponseDTOList);

        // Act
        List<TicketResponseDTO> result = ticketService.getTakenOnTickets();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ticketResponseDTOList, result);  // Verifica che il risultato sia quello atteso
        verify(ticketRepository, times(1)).findByStateTakeOn();
        verify(ticketMapper, times(1)).ticketsToTicketDTOs(takenOnTickets);
    }

    @Test
    void testGetPendingTickets() {
        // Arrange
        List<Ticket> pendingTickets = new ArrayList<>();
        ticket1.setState("Pending");
        pendingTickets.add(ticket1);

        ticket2.setState("Pending");
        pendingTickets.add(ticket2);

        // TicketResponseDTO simulato per ogni ticket
        TicketResponseDTO ticketResponseDTO1 = new TicketResponseDTO();
        TicketResponseDTO ticketResponseDTO2 = new TicketResponseDTO();
        List<TicketResponseDTO> ticketResponseDTOList = Arrays.asList(ticketResponseDTO1, ticketResponseDTO2);

        // Simula il comportamento di ticketRepository
        when(ticketRepository.findByStatePending()).thenReturn(pendingTickets);
        when(ticketMapper.ticketsToTicketDTOs(pendingTickets)).thenReturn(ticketResponseDTOList);

        // Act
        List<TicketResponseDTO> result = ticketService.getPendingTickets();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ticketResponseDTOList, result);  // Verifica che il risultato sia quello atteso
        verify(ticketRepository, times(1)).findByStatePending();
        verify(ticketMapper, times(1)).ticketsToTicketDTOs(pendingTickets);
    }

    @Test
    void testTakeOnTicket_WhenTicketIsInValidState() {
        // Arrange
        long ticketId = 1L;

        // Mock del ticket
        ticket.setState("In attesa");

        // Mock del repository e dei mapper
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket); // Corretto qui
        when(ticketPriorityAssigner.assignPriorityToTicket(ticket)).thenReturn(PriorityFlag.HIGH);
        when(ticketMapper.ticketToTicketDTO(ticket)).thenReturn(ticketResponseDTO);

        // Act
        TicketResponseDTO result = ticketService.takeOnTicket(ticketId);

        // Assert
        assertNotNull(result);
        assertEquals(ticketResponseDTO, result);
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, times(1)).save(ticket);
        verify(ticketPriorityAssigner, times(1)).assignPriorityToTicket(ticket);
        verify(ticketMapper, times(1)).ticketToTicketDTO(ticket);
    }

    @Test
    void testTakeOnTicket_WhenTicketAlreadyInTakeOnState() {
        // Arrange
        long ticketId = 1L;
        ticket.setState("Pronto per essere preso in carico");

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act & Assert
        TicketActionException exception = assertThrows(TicketActionException.class, () -> ticketService.takeOnTicket(ticketId));
        assertEquals("Errore nell'esecuzione dell'azione 'prendere in carico' per il ticket con ID: 1. Stato attuale: 'Pronto per essere preso in carico'.", exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, never()).save(ticket); // Verifica che il salvataggio non sia avvenuto
    }

    @Test
    void testTakeOnTicket_WhenTicketIsInInvalidState() {
        // Arrange
        long ticketId = 1L;
        ticket.setState("Risolto");

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act & Assert
        TicketActionException exception = assertThrows(TicketActionException.class, () -> ticketService.takeOnTicket(ticketId));
        assertEquals("Errore nell'esecuzione dell'azione 'prendere in carico' per il ticket con ID: 1. Stato attuale: 'Risolto'.", exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, never()).save(ticket); // Verifica che il salvataggio non sia avvenuto
    }

    @Test
    void testTakeOnTicket_WhenSaveFails() {
        // Arrange
        long ticketId = 1L;
        ticket.setState("In attesa");

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        EntityEditException exception = assertThrows(EntityEditException.class, () -> ticketService.takeOnTicket(ticketId));
        assertEquals("Si è verificato un errore nell'aggiornamento dell'entità id con ticket = 1.", exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void testTakeOnTicket_WhenTicketIsAlreadyInTakeOnState() {
        // Arrange
        long ticketId = 1L;
        TakeOnState takeOnState = new TakeOnState(emailService); // Ticket in stato "Take On"
        ticket.setStateTicket(takeOnState);  // Impostiamo lo stato del ticket come TakeOnState

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act & Assert
        TicketActionException exception = assertThrows(TicketActionException.class, () -> ticketService.takeOnTicket(ticketId));

        // Verifica che l'eccezione sia stata lanciata con il messaggio corretto
        assertEquals("Errore nell'esecuzione dell'azione 'prendere in carico' per il ticket con ID: 1. Stato attuale: 'In lavorazione'.", exception.getMessage());

        // Verifica che il ticket non sia stato salvato
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, never()).save(ticket);
    }

    @Test
    void testGetTicketFiltered_WithAllFilters() {
        // Arrange
        ticketFilterRequest.setPriority(String.valueOf(PriorityFlag.HIGH));
        ticketFilterRequest.setSearchText("ticket");
        ticketFilterRequest.setDateRangeType("THIS_WEEK");
        ticketFilterRequest.setSortByCreationDate("ASC");

        List<Ticket> tickets = Arrays.asList(ticket1, ticket2);
        List<TicketResponseDTO> ticketDTOs = Arrays.asList(ticketDTO1, ticketDTO2);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Ticket.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Ticket.class)).thenReturn(root);
        when(ticketMapper.ticketsToTicketDTOs(tickets)).thenReturn(ticketDTOs);

        // Simula il comportamento della query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(tickets);

        // Act
        FilteredTicketsResponse result = ticketService.getTicketFiltered(ticketFilterRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getAllTickets().size());
        assertEquals(1, result.getPendingTickets().size());
        assertEquals(1, result.getResolvedTickets().size());
        assertEquals(0, result.getTakeOnTickets().size());

        verify(criteriaBuilder, times(1)).createQuery(Ticket.class);
        verify(ticketMapper, times(1)).ticketsToTicketDTOs(tickets);
    }

    @Test
    void testGetTicketFiltered_WithNoFilters() {
        // Arrange
        ticketFilterRequest = new TicketFilterRequest(); // Nessun filtro applicato

        List<Ticket> tickets = Arrays.asList(ticket1, ticket2);
        List<TicketResponseDTO> ticketDTOs = Arrays.asList(ticketDTO1, ticketDTO2);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Ticket.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Ticket.class)).thenReturn(root);
        when(ticketMapper.ticketsToTicketDTOs(tickets)).thenReturn(ticketDTOs);

        // Simula il comportamento della query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(tickets);

        // Act
        FilteredTicketsResponse result = ticketService.getTicketFiltered(ticketFilterRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getAllTickets().size());
        assertEquals(1, result.getPendingTickets().size());  // "In attesa"
        assertEquals(1, result.getResolvedTickets().size()); // "Accettato"
        assertEquals(0, result.getTakeOnTickets().size());

        verify(criteriaBuilder, times(1)).createQuery(Ticket.class);
    }

    @Test
    void testGetTicketFiltered_WithSearchText() {
        // Arrange
        ticketFilterRequest.setSearchText("Ticket 1");

        List<Ticket> tickets = Collections.singletonList(ticket1);
        List<TicketResponseDTO> ticketDTOs = Collections.singletonList(ticketDTO1);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Ticket.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Ticket.class)).thenReturn(root);
        when(ticketMapper.ticketsToTicketDTOs(tickets)).thenReturn(ticketDTOs);

        // Simula il comportamento della query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(tickets);

        // Act
        FilteredTicketsResponse result = ticketService.getTicketFiltered(ticketFilterRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getAllTickets().size()); // Solo "Ticket 1"
        assertEquals(1, result.getPendingTickets().size());  // "In attesa"
        assertEquals(0, result.getResolvedTickets().size());
        assertEquals(0, result.getTakeOnTickets().size());

        verify(criteriaBuilder, times(1)).createQuery(Ticket.class);
    }

    @Test
    void testAddDateRangePredicateForThisWeek() {
        // Arrange
        TicketFilterRequest request = new TicketFilterRequest();
        request.setDateRangeType("THIS_WEEK");

        List<Predicate> predicates = new ArrayList<>();

        LocalDate now = LocalDate.now();
        LocalDateTime expectedStartDate = now.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime expectedEndDate = now.with(DayOfWeek.SUNDAY).atTime(23, 59, 59);

        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.between(any(), eq(expectedStartDate), eq(expectedEndDate))).thenReturn(mockPredicate);

        // Act
        ticketService.addDateRangePredicate(request, criteriaBuilder, root, predicates);

        // Assert
        assertEquals(1, predicates.size());
        assertEquals(mockPredicate, predicates.getFirst());
    }

    @Test
    void testAddDateRangePredicateForThisMonth() {
        // Arrange
        TicketFilterRequest request = new TicketFilterRequest();
        request.setDateRangeType("THIS_MONTH");

        List<Predicate> predicates = new ArrayList<>();

        LocalDate now = LocalDate.now();
        LocalDateTime expectedStartDate = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime expectedEndDate = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);

        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.between(any(), eq(expectedStartDate), eq(expectedEndDate))).thenReturn(mockPredicate);

        // Act
        ticketService.addDateRangePredicate(request, criteriaBuilder, root, predicates);

        // Assert
        assertEquals(1, predicates.size());
        assertEquals(mockPredicate, predicates.getFirst());
    }

    @Test
    void testAddDateRangePredicateForToday() {
        // Arrange
        TicketFilterRequest request = new TicketFilterRequest();
        request.setDateRangeType("TODAY");

        List<Predicate> predicates = new ArrayList<>();

        LocalDate now = LocalDate.now();
        LocalDateTime expectedStartDate = now.atStartOfDay();
        LocalDateTime expectedEndDate = now.atTime(23, 59, 59);

        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.between(any(), eq(expectedStartDate), eq(expectedEndDate))).thenReturn(mockPredicate);

        // Act
        ticketService.addDateRangePredicate(request, criteriaBuilder, root, predicates);

        // Assert
        assertEquals(1, predicates.size());
        assertEquals(mockPredicate, predicates.getFirst());
    }

    @Test
    void testAddDateRangePredicateAlways() {
        // Arrange
        // Act
        List<Predicate> predicates = new ArrayList<>();
        ticketService.addDateRangePredicate(ticketFilterRequest, criteriaBuilder, root, predicates);

        // Assert
        assertTrue(predicates.isEmpty());
        verify(criteriaBuilder, never()).between(any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testAddDateRangePredicateNullRangeType() {
        when(ticketFilterRequest.getDateRangeType()).thenReturn(null);

        // Act
        List<Predicate> predicates = new ArrayList<>();
        ticketService.addDateRangePredicate(ticketFilterRequest, criteriaBuilder, root, predicates);

        // Assert
        assertTrue(predicates.isEmpty());
        verify(criteriaBuilder, never()).between(any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testAddDateRangePredicate_NullDateRangeType() {
        // Arrange
        TicketFilterRequest request = new TicketFilterRequest();
        request.setDateRangeType(null);

        List<Predicate> predicates = new ArrayList<>();

        // Act
        ticketService.addDateRangePredicate(request, criteriaBuilder, root, predicates);

        // Assert
        assertEquals(0, predicates.size());  // No predicate should be added when dateRangeType is null
    }

    @Test
    void testGetTicketFiltered_WithPriorityFilter() {
        // Arrange
        ticketFilterRequest.setPriority(String.valueOf(PriorityFlag.HIGH));

        List<Ticket> tickets = Collections.singletonList(ticket1);
        List<TicketResponseDTO> ticketDTOs = Collections.singletonList(ticketDTO1);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Ticket.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Ticket.class)).thenReturn(root);
        when(ticketMapper.ticketsToTicketDTOs(tickets)).thenReturn(ticketDTOs);

        // Simula il comportamento della query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(tickets);

        // Act
        FilteredTicketsResponse result = ticketService.getTicketFiltered(ticketFilterRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getAllTickets().size());
        assertEquals(1, result.getPendingTickets().size());  // "In attesa"
        assertEquals(0, result.getResolvedTickets().size());
        assertEquals(0, result.getTakeOnTickets().size());

        verify(criteriaBuilder, times(1)).createQuery(Ticket.class);
    }

    @Test
    void testGetTicketFiltered_WithSorting() {
        // Arrange
        ticketFilterRequest.setSortByCreationDate("DESC");

        List<Ticket> tickets = Arrays.asList(ticket2, ticket1);
        List<TicketResponseDTO> ticketDTOs = Arrays.asList(ticketDTO1, ticketDTO2);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Ticket.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Ticket.class)).thenReturn(root);
        when(ticketMapper.ticketsToTicketDTOs(tickets)).thenReturn(ticketDTOs);

        // Simula il comportamento della query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(tickets);

        // Act
        FilteredTicketsResponse result = ticketService.getTicketFiltered(ticketFilterRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getAllTickets().size());
        assertEquals(1, result.getPendingTickets().size());  // "In attesa"
        assertEquals(1, result.getResolvedTickets().size()); // "Accettato"
        assertEquals(0, result.getTakeOnTickets().size());

        verify(criteriaBuilder, times(1)).createQuery(Ticket.class);
    }

    @Test
    void testUpdatePriorities() {
        // Arrange
        ticket1.setId(1L);
        ticket1.setPriorityFlag(PriorityFlag.LOW);

        ticket2.setId(2L);
        ticket2.setPriorityFlag(PriorityFlag.MEDIUM);

        Ticket ticket3 = new Ticket();
        ticket3.setId(3L);
        ticket3.setPriorityFlag(PriorityFlag.HIGH);

        // Lista di ticket simulata
        List<Ticket> tickets = Arrays.asList(ticket1, ticket2, ticket3);

        // Configura il comportamento del repository
        when(ticketRepository.findAllOpenTickets()).thenReturn(tickets);

        // Configura il comportamento del ticketPriorityAssigner
        when(ticketPriorityAssigner.assignPriorityToTicket(ticket1)).thenReturn(PriorityFlag.MEDIUM);
        when(ticketPriorityAssigner.assignPriorityToTicket(ticket2)).thenReturn(PriorityFlag.MEDIUM); // Nessun cambiamento
        when(ticketPriorityAssigner.assignPriorityToTicket(ticket3)).thenReturn(PriorityFlag.LOW);

        // Act
        ticketService.updatePriorities();

        // Assert
        verify(ticketRepository, times(1)).save(ticket1); // Cambia da LOW a MEDIUM
        verify(ticketRepository, times(0)).save(ticket2); // Nessun cambiamento
        verify(ticketRepository, times(1)).save(ticket3); // Cambia da HIGH a LOW

        // Verifica che il metodo assignPriorityToTicket sia chiamato per ogni ticket
        verify(ticketPriorityAssigner, times(1)).assignPriorityToTicket(ticket1);
        verify(ticketPriorityAssigner, times(1)).assignPriorityToTicket(ticket2);
        verify(ticketPriorityAssigner, times(1)).assignPriorityToTicket(ticket3);
    }

    @Test
    void testCreateReport_ReportReviews() {
        PendingState mockState = new PendingState(emailService);

        // Arrange
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Modifica qui: Cambia `setReportedProductId` in `setReportedReviewId` e imposta 100L
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setReportedReviewId(100L);  // Cambiato per fare riferimento alla recensione

        Review reportedReview = new Review();
        reportedReview.setId(100L); // Imposta l'ID della recensione

        // Configura il comportamento dei mock
        when(ticketMapper.ticketRequestDTOToTicket(ticketRequestDTO)).thenReturn(ticket);
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(reportedReview)); // Usa la recensione giusta
        when(ticketRepository.findByTicketRequesterAndReportedReviewId(any(User.class), eq(100L))).thenReturn(Optional.empty()); // Usa un matcher generico per l'utente
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(ticketPriorityAssigner.assignPriorityToTicket(ticket)).thenReturn(PriorityFlag.LOW);

        TicketResponseDTO mockTicketResponseDTO = new TicketResponseDTO();
        mockTicketResponseDTO.setId(ticket.getId());
        mockTicketResponseDTO.setTitle(ticket.getTitle());
        mockTicketResponseDTO.setDescription(ticket.getDescription());
        mockTicketResponseDTO.setState(ticket.getState());

        when(ticketMapper.ticketToTicketDTO(ticket)).thenReturn(mockTicketResponseDTO);

        // Act
        TicketResponseDTO response = ticketService.reportReviews(ticketRequestDTO);

        // Assert
        assertNotNull(response);

        // Verifica delle chiamate ai metodi delle dipendenze
        verify(ticketMapper).ticketRequestDTOToTicket(ticketRequestDTO);
        verify(reviewRepository).findById(100L); // Verifica che venga cercata la recensione con ID 100
        verify(ticketRepository).findByTicketRequesterAndReportedReviewId(any(User.class), eq(100L)); // Verifica con un matcher generico
        verify(ticketPriorityAssigner).assignPriorityToTicket(ticket);
        verify(ticketRepository).save(ticket);

        // Verifica della chiamata a sendReportConfirmation
        verify(emailService).sendReportConfirmation(ticket);

        // Controllo delle proprietà del ticket
        assertEquals(PriorityFlag.LOW, ticket.getPriorityFlag());
        assertEquals(mockState.getStatusMessage(), ticket.getState());
    }

    @Test
    void testCreateReport_ReportUser() {
        PendingState mockState = new PendingState(emailService);

        // Arrange
        User reportedUser = new User();
        reportedUser.setId(2L);  // L'utente che deve essere segnalato
        reportedUser.setName("ReportedUser");
        reportedUser.setSurname("ReportedSurname");
        reportedUser.setEmail("reported@example.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Imposta l'ID dell'utente segnalato
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setReportedUserId(2L);  // Imposta ID dell'utente segnalato

        // Configura il comportamento dei mock
        when(ticketMapper.ticketRequestDTOToTicket(ticketRequestDTO)).thenReturn(ticket);
        when(userRepository.findById(2L)).thenReturn(Optional.of(reportedUser));  // Restituisce l'utente segnalato
        when(ticketRepository.findByTicketRequesterAndReportedUserId(any(User.class), eq(2L))).thenReturn(Optional.empty()); // Usa un matcher generico per l'utente
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(ticketPriorityAssigner.assignPriorityToTicket(ticket)).thenReturn(PriorityFlag.LOW);

        TicketResponseDTO mockTicketResponseDTO = new TicketResponseDTO();
        mockTicketResponseDTO.setId(ticket.getId());
        mockTicketResponseDTO.setTitle(ticket.getTitle());
        mockTicketResponseDTO.setDescription(ticket.getDescription());
        mockTicketResponseDTO.setState(ticket.getState());

        when(ticketMapper.ticketToTicketDTO(ticket)).thenReturn(mockTicketResponseDTO);

        // Act
        TicketResponseDTO response = ticketService.reportUser(ticketRequestDTO);

        // Assert
        assertNotNull(response);

        // Verifica delle chiamate ai metodi delle dipendenze
        verify(ticketMapper).ticketRequestDTOToTicket(ticketRequestDTO);
        verify(userRepository).findById(2L); // Verifica che venga cercato l'utente con ID 2
        verify(ticketRepository).findByTicketRequesterAndReportedUserId(any(User.class), eq(2L)); // Verifica con un matcher generico
        verify(ticketPriorityAssigner).assignPriorityToTicket(ticket);
        verify(ticketRepository).save(ticket);

        // Verifica della chiamata a sendReportConfirmation
        verify(emailService).sendReportConfirmation(ticket);

        // Controllo delle proprietà del ticket
        assertEquals(PriorityFlag.LOW, ticket.getPriorityFlag());
        assertEquals(mockState.getStatusMessage(), ticket.getState());
    }

    @Test
    void testCreateReport_ReportProduct() {
        PendingState mockState = new PendingState(emailService);

        // Arrange
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setReportedProductId(300L);

        Product reportedProduct = new Product();
        reportedProduct.setId(300L);

        // Configura il comportamento del mock
        doReturn(ticket).when(ticketMapper).ticketRequestDTOToTicket(ticketRequestDTO);
        doReturn(Optional.of(reportedProduct)).when(productRepository).findById(300L);
        doReturn(Optional.empty()).when(ticketRepository)
                .findByTicketRequesterAndReportedProductId(any(User.class), eq(300L)); // Usa un matcher generico per l'utente
        doReturn(ticket).when(ticketRepository).save(ticket);
        doReturn(PriorityFlag.LOW).when(ticketPriorityAssigner).assignPriorityToTicket(ticket);


        doReturn(ticketResponseDTO).when(ticketMapper).ticketToTicketDTO(ticket);

        // Act
        TicketResponseDTO response = ticketService.reportProduct(ticketRequestDTO);

        // Assert
        assertNotNull(response);

        verify(ticketMapper).ticketRequestDTOToTicket(ticketRequestDTO);
        verify(productRepository).findById(300L);
        verify(ticketRepository).findByTicketRequesterAndReportedProductId(any(User.class), eq(300L)); // Verifica con un matcher generico
        verify(ticketPriorityAssigner).assignPriorityToTicket(ticket);
        verify(ticketRepository).save(ticket);

        // Verifica della chiamata a sendReportConfirmation
        verify(emailService).sendReportConfirmation(ticket);

        // Controllo delle proprietà del ticket
        assertEquals(PriorityFlag.LOW, ticket.getPriorityFlag());
        assertEquals(mockState.getStatusMessage(), ticket.getState());
    }

    @Test
    void testCreateReport_ReviewAlreadyReported() {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Arrange
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setReportedReviewId(100L);

        Review mockReview = new Review();
        mockReview.setId(100L);

        when(ticketMapper.ticketRequestDTOToTicket(ticketRequestDTO)).thenReturn(ticket);
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(mockReview));
        // Modifica qui: utilizziamo any(User.class) per evitare il problema di matching
        when(ticketRepository.findByTicketRequesterAndReportedReviewId(any(User.class), eq(100L)))
                .thenReturn(Optional.of(ticket));

        // Act & Assert
        InvalidEntityDataException exception = assertThrows(InvalidEntityDataException.class, () -> ticketService.reportReviews(ticketRequestDTO));

        assertEquals("Dati non validi per ticket con ID recensione segnalata = 100. L'utente ha già una segnalazione in sospeso per questa recensione.", exception.getMessage());
        verify(ticketMapper).ticketRequestDTOToTicket(ticketRequestDTO);
        verify(reviewRepository, times(1)).findById(100L);  // Modifica qui per aspettarsi solo una chiamata
        verify(ticketRepository).findByTicketRequesterAndReportedReviewId(any(User.class), eq(100L));
    }

    @Test
    void testCreateReport_ReportReviews_EntityNotFoundException() {
        // Arrange
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setReportedReviewId(100L); // ID recensione che non esiste

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock per il repository recensioni: la recensione non esiste
        when(reviewRepository.findById(100L)).thenReturn(Optional.empty());

        when(ticketMapper.ticketRequestDTOToTicket(ticketRequestDTO)).thenReturn(ticket);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> ticketService.reportReviews(ticketRequestDTO));

        // Verifica che l'eccezione contenga il messaggio corretto
        assertEquals("Nessun recensione con ID = 100 è stato trovato.", exception.getMessage());
    }

    @Test
    void testCreateReport_ReportReviews_InvalidEntityDataException_DuplicateReport() {
        // Arrange
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setReportedReviewId(100L);

        Review mockReview = new Review();
        mockReview.setId(100L);

        // Simuliamo l'autenticazione dell'utente
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock del repository recensioni: la recensione esiste
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(mockReview));

        // Mock del repository ticket: simula una segnalazione già presente per la stessa recensione
        when(ticketRepository.findByTicketRequesterAndReportedReviewId(any(User.class), eq(100L)))
                .thenReturn(Optional.of(new Ticket())); // Simula una segnalazione già presente

        when(ticketMapper.ticketRequestDTOToTicket(ticketRequestDTO)).thenReturn(ticket);

        // Act & Assert
        InvalidEntityDataException exception = assertThrows(InvalidEntityDataException.class, () -> ticketService.reportReviews(ticketRequestDTO));

        // Verifica che l'eccezione contenga il messaggio corretto
        assertEquals("Dati non validi per ticket con ID recensione segnalata = 100. L'utente ha già una segnalazione in sospeso per questa recensione.", exception.getMessage());
    }

    @Test
    void testCreateReport_ReportUser_EntityNotFoundException() {
        // Arrange
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setReportedUserId(100L); // ID che non esiste

        // Simuliamo l'autenticazione dell'utente
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock del repository utente: l'utente con l'ID 100 non esiste
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        when(ticketMapper.ticketRequestDTOToTicket(ticketRequestDTO)).thenReturn(ticket);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> ticketService.reportUser(ticketRequestDTO));

        // Verifica che l'eccezione contenga il messaggio corretto
        assertEquals("Nessun utente con ID = 100 è stato trovato.", exception.getMessage());
    }

    @Test
    void testCreateReport_ReportUser_InvalidEntityDataException_DuplicateReport() {
        // Arrange
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setReportedUserId(100L); // ID dell'utente segnalato

        // Creiamo l'utente segnalato
        User mockReportedUser = new User();
        mockReportedUser.setId(100L);

        // Simuliamo l'autenticazione dell'utente
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock del repository per restituire l'utente segnalato
        when(userRepository.findById(100L)).thenReturn(Optional.of(mockReportedUser)); // L'utente segnalato esiste

        // Mock del ticketRepository per simulare una segnalazione già presente
        when(ticketRepository.findByTicketRequesterAndReportedUserId(any(User.class), eq(100L)))
                .thenReturn(Optional.of(new Ticket())); // Simula una segnalazione già esistente

        ticket.setTicketRequester(user);
        when(ticketMapper.ticketRequestDTOToTicket(ticketRequestDTO)).thenReturn(ticket);

        // Act & Assert
        InvalidEntityDataException exception = assertThrows(InvalidEntityDataException.class, () -> ticketService.reportUser(ticketRequestDTO));

        // Verifica che l'eccezione contenga il messaggio corretto
        assertEquals("Dati non validi per ticket con ID utente segnalato = 100. L'utente ha già una segnalazione in sospeso contro questo utente.", exception.getMessage());
    }

    @Test
    void testCreateReport_ReportProduct_EntityNotFoundException() {
        // Arrange
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setReportedProductId(100L); // ID che non esiste

        // Simuliamo l'autenticazione dell'utente
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock per il repository prodotto, che restituisce Optional.empty() (prodotto non esistente)
        when(productRepository.findById(100L)).thenReturn(Optional.empty());

        ticket.setTicketRequester(user);

        when(ticketMapper.ticketRequestDTOToTicket(ticketRequestDTO)).thenReturn(ticket);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> ticketService.reportProduct(ticketRequestDTO));

        // Verifica che l'eccezione contenga il messaggio corretto
        assertEquals("Nessun prodotto con ID = 100 è stato trovato.", exception.getMessage());
    }

    @Test
    void testCreateReport_ReportProduct_InvalidEntityDataException_DuplicateReport() {
        // Arrange
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setReportedProductId(100L);

        Product mockProduct = new Product();
        mockProduct.setId(100L);

        // Simuliamo l'autenticazione dell'utente
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock per il repository prodotto: il prodotto esiste
        when(productRepository.findById(100L)).thenReturn(Optional.of(mockProduct));

        // Mock per il repository ticket: c'è già una segnalazione per questo prodotto
        when(ticketRepository.findByTicketRequesterAndReportedProductId(any(User.class), eq(100L)))
                .thenReturn(Optional.of(new Ticket())); // Simuliamo una segnalazione già esistente

        // Mock del ticketMapper per restituire un oggetto Ticket valido
        Ticket mockTicket = new Ticket();
        mockTicket.setId(1L);
        mockTicket.setTitle("TestTitle");
        mockTicket.setDescription("TestDescription");
        mockTicket.setTicketRequester(user);
        when(ticketMapper.ticketRequestDTOToTicket(ticketRequestDTO)).thenReturn(mockTicket);

        // Act & Assert
        InvalidEntityDataException exception = assertThrows(InvalidEntityDataException.class, () -> ticketService.reportProduct(ticketRequestDTO));

        // Verifica che l'eccezione contenga il messaggio corretto
        assertEquals("Dati non validi per ticket con ID prodotto segnalato = 100. L'utente ha già una segnalazione in sospeso contro questo prodotto.", exception.getMessage());
    }

    @Test
    void testCreateReport_DataIntegrityViolationException() {
        // Arrange
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setTitle("TestTitle");
        ticketRequestDTO.setDescription("TestDescription");
        ticketRequestDTO.setReportedProductId(100L); // ID del prodotto che viene segnalato

        // Creiamo un prodotto fittizio da segnalare
        Product mockProduct = new Product();
        mockProduct.setId(100L);

        // Simuliamo l'autenticazione dell'utente
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock del repository per restituire il prodotto e la verifica se una segnalazione esiste già
        when(productRepository.findById(100L)).thenReturn(Optional.of(mockProduct)); // Il prodotto esiste
        // Utilizza any(User.class) per evitare il confronto strettissimo sugli oggetti User
        when(ticketRepository.findByTicketRequesterAndReportedProductId(any(User.class), eq(100L)))
                .thenReturn(Optional.of(new Ticket())); // Simula una segnalazione già esistente

        when(ticketMapper.ticketRequestDTOToTicket(ticketRequestDTO)).thenReturn(ticket);

        // Act & Assert
        InvalidEntityDataException exception = assertThrows(InvalidEntityDataException.class, () -> ticketService.reportProduct(ticketRequestDTO));

        // Verifica che l'eccezione contenga il messaggio corretto
        assertEquals("Dati non validi per ticket con ID prodotto segnalato = 100. L'utente ha già una segnalazione in sospeso contro questo prodotto.", exception.getMessage());
    }

    @Test
    void testCreateReport_InvalidDataEntity_WhenSaveFails() {
        // Arrange
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setReportedProductId(100L);

        Product mockProduct = new Product();
        mockProduct.setId(100L);

        // Simuliamo l'autenticazione dell'utente
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock del repository prodotto: il prodotto esiste
        when(productRepository.findById(100L)).thenReturn(Optional.of(mockProduct));

        // Mock del repository ticket: nessuna segnalazione presente
        when(ticketRepository.findByTicketRequesterAndReportedProductId(any(User.class), eq(100L)))
                .thenReturn(Optional.empty());

        // Simula un'eccezione durante il salvataggio del ticket
        when(ticketRepository.save(ticket)).thenThrow(new DataIntegrityViolationException(""));

        when(ticketMapper.ticketRequestDTOToTicket(ticketRequestDTO)).thenReturn(ticket);

        // Act & Assert
        InvalidEntityDataException exception = assertThrows(InvalidEntityDataException.class, () -> ticketService.reportProduct(ticketRequestDTO));

        // Verifica che l'eccezione contenga il messaggio corretto
        assertEquals("Dati non validi per ticket con ID utente = 1. L'utente ha già una segnalazione in sospeso.", exception.getMessage());
    }
}