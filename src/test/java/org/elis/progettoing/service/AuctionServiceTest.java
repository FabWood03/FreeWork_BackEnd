package org.elis.progettoing.service;

import org.elis.progettoing.dto.request.auction.AuctionRequestDTO;
import org.elis.progettoing.dto.response.auction.AuctionDetailsDTO;
import org.elis.progettoing.dto.response.auction.AuctionSummaryDTO;
import org.elis.progettoing.dto.response.user.UserResponseDTO;
import org.elis.progettoing.enumeration.AuctionStatus;
import org.elis.progettoing.exception.auction.AuctionException;
import org.elis.progettoing.exception.auction.AuctionOwnershipException;
import org.elis.progettoing.exception.auction.InvalidAuctionStateException;
import org.elis.progettoing.exception.auction.UserSubscriptionException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityEditException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.AuctionMapper;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.models.auction.AuctionSubscription;
import org.elis.progettoing.models.category.MacroCategory;
import org.elis.progettoing.models.category.SubCategory;
import org.elis.progettoing.pattern.observerPattern.AuctionManager;
import org.elis.progettoing.repository.*;
import org.elis.progettoing.service.implementation.AuctionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@EnableScheduling
class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionSubscriptionRepository auctionSubscriptionRepository;

    @Mock
    private MacroCategoryRepository macroCategoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private AuctionMapper auctionMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private User mockUser;

    @Mock
    private AuctionManager auctionManager;

    @InjectMocks
    private AuctionServiceImpl auctionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(mock(UsernamePasswordAuthenticationToken.class));
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mockUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreateAuction_WithValidRequest_ReturnsAuctionDetailsDTO() {
        AuctionRequestDTO auctionRequestDTO = new AuctionRequestDTO();
        auctionRequestDTO.setStartAuctionDate(LocalDateTime.now().plusHours(1));
        auctionRequestDTO.setEndAuctionDate(LocalDateTime.now().plusHours(2));

        Auction mockAuction = new Auction();
        mockAuction.setId(1L);
        mockAuction.setStartAuctionDate(auctionRequestDTO.getStartAuctionDate());
        mockAuction.setEndAuctionDate(auctionRequestDTO.getEndAuctionDate());
        mockAuction.setStatus(AuctionStatus.PENDING);

        when(auctionMapper.auctionRequestDTOToAuction(auctionRequestDTO)).thenReturn(mockAuction);
        when(auctionMapper.auctionToAuctionResponseDTO(mockAuction)).thenReturn(new AuctionDetailsDTO());

        AuctionDetailsDTO result = auctionService.createAuction(auctionRequestDTO);

        assertNotNull(result);
        verify(auctionRepository, times(1)).save(mockAuction);
    }

    @Test
    void testDeleteAuction_WhenAuctionNotFound_ThrowsEntityNotFoundException() {
        when(auctionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> auctionService.deleteAuction(1L));
    }

    @Test
    void testDeleteAuction_WhenAuctionIsPendingAndUserIsOwner_DeletesSuccessfully() {
        // Crea un oggetto User mock
        mockUser.setId(1L);
        // Crea un oggetto Auction mock e associane un User
        Auction mockAuction = new Auction();
        mockAuction.setId(1L);
        mockAuction.setStatus(AuctionStatus.PENDING);
        mockAuction.setOwner(mockUser); // Associa il mockUser all'asta

        // Configura il comportamento del repository per restituire l'asta
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(mockAuction));

        // Simula l'autenticazione dell'utente
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Esegui il test
        boolean result = auctionService.deleteAuction(1L);

        // Verifica i risultati
        assertTrue(result);
        verify(auctionRepository, times(1)).delete(mockAuction);
    }

    @Test
    void testDeleteAuction_WhenUserIsNotOwner_ThrowsAuctionOwnershipException() {
        // Crea un'asta mock
        Auction mockAuction = new Auction();
        mockAuction.setId(1L);
        User owner = new User();
        owner.setId(2L);  // Proprietario diverso
        mockAuction.setOwner(owner);

        // Simula l'autenticazione dell'utente
        when(mockUser.getId()).thenReturn(1L);  // Utente diverso

        // Mock del repository
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(mockAuction));

        // Verifica che venga lanciata l'eccezione AuctionOwnershipException
        assertThrows(AuctionOwnershipException.class, () -> auctionService.deleteAuction(1L));
    }

    @Test
    void testDeleteAuction_WhenAuctionStatusIsNotPending_ThrowsAuctionException() {
        // Crea un mock esplicito di User
        when(mockUser.getId()).thenReturn(1L);  // Stub per restituire l'ID dell'utente

        // Crea un'asta mock con stato diverso da PENDING (ad esempio OPEN)
        Auction mockAuction = mock(Auction.class);
        when(mockAuction.getId()).thenReturn(1L);
        when(mockAuction.getStatus()).thenReturn(AuctionStatus.OPEN);  // Stato diverso da PENDING
        when(mockAuction.getOwner()).thenReturn(mockUser);  // L'asta ha come proprietario mockUser

        // Mock del repository
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(mockAuction));

        // Verifica che venga lanciata l'eccezione AuctionException
        assertThrows(AuctionException.class, () -> auctionService.deleteAuction(1L));
    }

    @Test
    void testDeleteAuction_WhenRepositoryDeleteFails_ThrowsEntityDeletionException() {
        // Crea i mock esplicitamente
        Auction mockAuction = mock(Auction.class);  // Mock esplicito di mockAuction

        // Configura i comportamenti dei mock
        when(mockUser.getId()).thenReturn(1L);  // Configura mock per restituire l'ID dell'utente
        when(mockAuction.getId()).thenReturn(1L);  // Configura mock per restituire l'ID dell'asta
        when(mockAuction.getStatus()).thenReturn(AuctionStatus.PENDING);
        when(mockAuction.getOwner()).thenReturn(mockUser);  // Configura mock per restituire l'utente proprietario

        // Mock del SecurityContext
        SecurityContext securityContextNew = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContextNew);

        // Impostare l'autenticazione per l'utente mockato
        when(securityContextNew.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(mockUser, null));

        // Simula il comportamento del repository per restituire l'asta
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(mockAuction));

        // Simula il comportamento di delete per lanciare un'eccezione
        doThrow(new RuntimeException("Errore durante l'eliminazione")).when(auctionRepository).delete(mockAuction);

        // Verifica che venga lanciata l'eccezione EntityDeletionException
        assertThrows(EntityDeletionException.class, () -> auctionService.deleteAuction(1L));
    }

    @Test
    void updateSingleAuctionStatus_shouldUpdateToOpen_whenPendingAndStartDateHasPassed() {
        // Arrange
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setStatus(AuctionStatus.PENDING);
        auction.setStartAuctionDate(LocalDateTime.now().minusMinutes(10));
        auction.setEndAuctionDate(LocalDateTime.now().plusMinutes(10));
        when(auctionRepository.save(auction)).thenReturn(auction);

        // Act
        auctionService.updateSingleAuctionStatus(auction);

        // Assert
        assertEquals(AuctionStatus.OPEN, auction.getStatus());
        verify(auctionRepository, times(1)).save(auction);
        verify(auctionManager, times(1)).notifyAuctionOpening(auction);
        verify(auctionManager, never()).notifyAuctionClosed(auction);
        verify(auctionManager, never()).notifyAuctionEndingSoon(auction);
    }

    @Test
    void updateSingleAuctionStatus_shouldUpdateToClosed_whenOpenAndEndDateHasPassed() {
        // Arrange
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setStatus(AuctionStatus.OPEN);
        auction.setEndAuctionDate(LocalDateTime.now().minusMinutes(10)); // End date is in the past

        when(auctionRepository.save(auction)).thenReturn(auction);

        // Act
        auctionService.updateSingleAuctionStatus(auction);

        // Assert
        assertEquals(AuctionStatus.CLOSED, auction.getStatus());
        verify(auctionRepository, times(1)).save(auction);
        verify(auctionManager, times(1)).notifyAuctionClosed(auction);
        verify(auctionManager, never()).notifyAuctionOpening(auction);
        verify(auctionManager, never()).notifyAuctionEndingSoon(auction);
    }

    @Test
    void updateSingleAuctionStatus_shouldSendEndingSoonNotification_whenOpenAndEndDateIsApproaching() {
        // Arrange
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setStatus(AuctionStatus.OPEN);
        auction.setEndAuctionDate(LocalDateTime.now().plusMinutes(30)); // Less than 1 hour to close

        // Act
        auctionService.updateSingleAuctionStatus(auction);

        // Assert
        verify(auctionManager, times(1)).notifyAuctionEndingSoon(auction);
        verify(auctionRepository, never()).save(auction);
        verify(auctionManager, never()).notifyAuctionOpening(auction);
        verify(auctionManager, never()).notifyAuctionClosed(auction);
    }

    @Test
    void updateSingleAuctionStatus_shouldNotUpdateOrNotify_whenNoConditionsAreMet() {
        // Arrange
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setStatus(AuctionStatus.PENDING);
        auction.setStartAuctionDate(LocalDateTime.now().plusMinutes(10)); // Start date is in the future

        // Act
        auctionService.updateSingleAuctionStatus(auction);

        // Assert
        verify(auctionRepository, never()).save(auction);
        verify(auctionManager, never()).notifyAuctionOpening(auction);
        verify(auctionManager, never()).notifyAuctionClosed(auction);
        verify(auctionManager, never()).notifyAuctionEndingSoon(auction);
    }

    @Test
    void updateSingleAuctionStatus_shouldThrowAuctionException_whenErrorOccurs() {
        // Arrange
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setStatus(AuctionStatus.PENDING);
        auction.setStartAuctionDate(LocalDateTime.now().minusMinutes(10)); // Start date is in the past

        when(auctionRepository.save(auction)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        AuctionException exception = assertThrows(AuctionException.class, () -> auctionService.updateSingleAuctionStatus(auction));

        assertTrue(exception.getMessage().contains("Errore durante l'aggiornamento dell'asta con ID: 1"));
        verify(auctionRepository, times(1)).save(auction);
        verify(auctionManager, never()).notifyAuctionOpening(auction);
        verify(auctionManager, never()).notifyAuctionClosed(auction);
        verify(auctionManager, never()).notifyAuctionEndingSoon(auction);
    }

    @Test
    void getAuctionDetails_shouldReturnAuctionDetails_whenAuctionExists() {
        // Arrange: prepariamo un'asta simulata
        long auctionId = 1L;
        Auction auction = new Auction();
        auction.setId(auctionId);
        auction.setTitle("Test Auction");

        AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO();
        auctionDetailsDTO.setId(auctionId);
        auctionDetailsDTO.setTitle("Test Auction");

        // Simuliamo il repository e il mapper
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(auctionMapper.auctionToAuctionResponseDTO(auction)).thenReturn(auctionDetailsDTO);

        // Act: chiamiamo il metodo sotto test
        AuctionDetailsDTO result = auctionService.getAuctionDetails(auctionId);

        // Assert: verifichiamo che il risultato sia corretto
        assertNotNull(result);
        assertEquals(auctionId, result.getId());
        assertEquals("Test Auction", result.getTitle());

        // Verifica che il repository e il mapper siano stati chiamati
        verify(auctionRepository, times(1)).findById(auctionId);
        verify(auctionMapper, times(1)).auctionToAuctionResponseDTO(auction);
    }

    @Test
    void getAuctionDetails_shouldThrowEntityNotFoundException_whenAuctionDoesNotExist() {
        // Arrange: simuliamo un'asta non trovata
        long auctionId = 1L;

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

        // Act & Assert: verifichiamo che venga lanciata EntityNotFoundException
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> auctionService.getAuctionDetails(auctionId));

        // Verifichiamo i dettagli dell'eccezione
        assertEquals("Nessun Asta con ID = 1 è stato trovato.", exception.getMessage());

        // Verifica che il repository sia stato chiamato correttamente
        verify(auctionRepository, times(1)).findById(auctionId);

        // Verifica che il mapper non sia mai stato chiamato
        verifyNoInteractions(auctionMapper);
    }

    private User mockAuthenticatedUser(long userId, String email) {
        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        return user;
    }

    @Test
    void subscribeUserNotification_shouldSucceed_whenAllConditionsAreMet() {
        // Arrange
        long auctionId = 1L;
        User user = mockAuthenticatedUser(2L, "user@example.com");

        Auction auction = new Auction();
        auction.setId(auctionId);
        auction.setStatus(AuctionStatus.PENDING);
        User owner = new User();
        owner.setId(3L);
        auction.setOwner(owner);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        doNothing().when(auctionManager).subscribe(auction, user);

        // Act
        boolean result = auctionService.subscribeUserNotification(auctionId);

        // Assert
        assertTrue(result);
        verify(auctionRepository, times(1)).findById(auctionId);
        verify(auctionManager, times(1)).subscribe(auction, user);
    }

    @Test
    void subscribeUserNotification_shouldThrowEntityNotFoundException_whenAuctionDoesNotExist() {
        // Arrange
        long auctionId = 1L;
        mockAuthenticatedUser(2L, "user@example.com");

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> auctionService.subscribeUserNotification(auctionId));

        assertEquals("Nessun asta con ID = 1 è stato trovato.", exception.getMessage());

        verify(auctionRepository, times(1)).findById(auctionId);
        verifyNoInteractions(auctionManager);
    }

    @Test
    void subscribeUserNotification_shouldThrowAuctionOwnershipException_whenUserIsOwner() {
        // Arrange
        long auctionId = 1L;
        User user = mockAuthenticatedUser(3L, "owner@example.com");

        Auction auction = new Auction();
        auction.setId(auctionId);
        auction.setStatus(AuctionStatus.PENDING);
        auction.setOwner(user);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));

        // Act & Assert
        AuctionOwnershipException exception = assertThrows(AuctionOwnershipException.class, () -> auctionService.subscribeUserNotification(auctionId));

        assertEquals("L'utente con ID owner@example.com è il proprietario dell'asta con ID 1 e non può eseguire questa operazione: iscrizione alle notifiche", exception.getMessage());

        verify(auctionRepository, times(1)).findById(auctionId);
        verifyNoInteractions(auctionManager);
    }

    @Test
    void subscribeUserNotification_shouldThrowAuctionException_whenAuctionStatusIsInvalid() {
        // Arrange
        long auctionId = 1L;

        Auction auction = new Auction();
        auction.setId(auctionId);
        auction.setStatus(AuctionStatus.CLOSED); // Invalid status
        User owner = new User();
        owner.setId(3L);
        auction.setOwner(owner);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));

        // Act & Assert
        AuctionException exception = assertThrows(AuctionException.class, () -> auctionService.subscribeUserNotification(auctionId));

        assertTrue(exception.getMessage().contains("L'asta con ID " + auctionId + " è chiusa o in attesa"));

        verify(auctionRepository, times(1)).findById(auctionId);
        verifyNoInteractions(auctionManager);
    }

    @Test
    void subscribeUserNotification_shouldThrowUserSubscriptionException_whenSubscriptionFails() {
        // Arrange
        long auctionId = 1L;
        User user = mockAuthenticatedUser(2L, "user@example.com");

        Auction auction = new Auction();
        auction.setId(auctionId);
        auction.setStatus(AuctionStatus.PENDING);
        User owner = new User();
        owner.setId(3L);
        auction.setOwner(owner);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        doThrow(new RuntimeException("Subscription error")).when(auctionManager).subscribe(auction, user);

        // Act & Assert
        UserSubscriptionException exception = assertThrows(UserSubscriptionException.class, () -> auctionService.subscribeUserNotification(auctionId));

        assertTrue(exception.getMessage().contains("Errore durante l'iscrizione dell'utente alle notifiche per l'asta con ID: " + auctionId));

        verify(auctionRepository, times(1)).findById(auctionId);
        verify(auctionManager, times(1)).subscribe(auction, user);
    }

    @Test
    void testListActiveAuctions_WithActiveAuctions_ReturnsAuctionDetailsList() {
        Auction mockAuction = new Auction();
        mockAuction.setStatus(AuctionStatus.OPEN);
        when(auctionRepository.findByStatus(AuctionStatus.OPEN)).thenReturn(List.of(mockAuction));
        when(auctionMapper.auctionToAuctionResponseDTO(mockAuction)).thenReturn(new AuctionDetailsDTO());

        List<AuctionDetailsDTO> auctions = auctionService.listActiveAuctions();

        assertFalse(auctions.isEmpty());
        verify(auctionRepository, times(1)).findByStatus(AuctionStatus.OPEN);
    }

    @Test
    void testListActiveAuctions_NoActiveAuctions_ThrowsEntityNotFoundException() {
        // Arrange: simuliamo che il repository restituisca una lista vuota
        when(auctionRepository.findByStatus(AuctionStatus.OPEN)).thenReturn(Collections.emptyList());

        // Act & Assert: verifichiamo che venga lanciata EntityNotFoundException
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> auctionService.listActiveAuctions());

        // Verifica che il messaggio dell'eccezione sia corretto
        assertEquals("Nessun asta con stato = OPEN è stato trovato.", exception.getMessage());

        // Verifica che il repository sia stato chiamato correttamente
        verify(auctionRepository, times(1)).findByStatus(AuctionStatus.OPEN);

        // Verifica che il mapper non sia mai stato chiamato
        verifyNoInteractions(auctionMapper);
    }

    @Test
    void updateAuction_SaveFails_ThrowsEntityEditException() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setStatus(AuctionStatus.PENDING);
        auction.setOwner(mockUser);

        // Configurazione dati di test
        AuctionRequestDTO request = new AuctionRequestDTO();
        request.setId(1L);
        request.setTitle("Nuovo Titolo");

        Auction mappedAuction = new Auction();
        mappedAuction.setId(1L);
        mappedAuction.setTitle("Nuovo Titolo");
        mappedAuction.setStatus(AuctionStatus.PENDING);
        mappedAuction.setOwner(mockUser);

        // Configurazione mock
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        when(auctionMapper.auctionRequestDTOToAuction(request)).thenReturn(mappedAuction);

        // Simula un errore durante il salvataggio
        when(auctionRepository.save(mappedAuction)).thenThrow(new RuntimeException("Errore di database"));

        // Esecuzione e verifica dell'eccezione
        EntityEditException exception = assertThrows(EntityEditException.class,
                () -> auctionService.updateAuction(request));

        // Verifica del messaggio di errore
        assertEquals("Si è verificato un errore nell'aggiornamento dell'entità ID con asta = 1.", exception.getMessage());

        // Verifica delle interazioni
        verify(auctionMapper).auctionRequestDTOToAuction(request);
        verify(auctionRepository).save(mappedAuction);
        verify(auctionMapper, never()).auctionToAuctionResponseDTO(any()); // Verifica che il mapping di response non venga chiamato
    }

    @Test
    void updateAuction_VerifyFullMappingAndSaveProcess() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setStatus(AuctionStatus.PENDING);
        auction.setOwner(mockUser);

        // Configurazione dati di test
        AuctionRequestDTO request = new AuctionRequestDTO();
        request.setId(1L);
        request.setTitle("Nuovo Titolo");

        Auction mappedAuction = new Auction();
        mappedAuction.setId(1L);
        mappedAuction.setTitle("Nuovo Titolo");
        mappedAuction.setStatus(AuctionStatus.PENDING);
        mappedAuction.setOwner(mockUser);

        AuctionDetailsDTO expectedDTO = new AuctionDetailsDTO();
        expectedDTO.setTitle("Nuovo Titolo");

        // Configurazione mock
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        when(auctionMapper.auctionRequestDTOToAuction(request)).thenReturn(mappedAuction);
        when(auctionRepository.save(mappedAuction)).thenReturn(mappedAuction);
        when(auctionMapper.auctionToAuctionResponseDTO(mappedAuction)).thenReturn(expectedDTO);

        // Esecuzione
        AuctionDetailsDTO result = auctionService.updateAuction(request);

        // Verifiche puntuali
        verify(auctionMapper).auctionRequestDTOToAuction(request);
        verify(auctionRepository).save(mappedAuction); // Verifica salvataggio dell'oggetto mappato
        verify(auctionMapper).auctionToAuctionResponseDTO(mappedAuction);

        // Verifica integrità dati
        assertEquals("Nuovo Titolo", result.getTitle());
        assertSame(expectedDTO, result);
    }

    @Test
    void testUpdateAuction_InvalidState_ThrowsInvalidAuctionStateException() {
        // Crea un utente mock
        mockUser.setId(1L);

        // Simula l'autenticazione
        Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Crea un DTO di richiesta per l'asta
        AuctionRequestDTO auctionRequestDTO = new AuctionRequestDTO();
        auctionRequestDTO.setId(1L);
        auctionRequestDTO.setStartAuctionDate(LocalDateTime.now().plusHours(1));
        auctionRequestDTO.setEndAuctionDate(LocalDateTime.now().plusHours(2));
        auctionRequestDTO.setMacroCategoryId(1L); // ID valido della macro categoria
        auctionRequestDTO.setSubCategoryId(1L);   // ID valido della sottocategoria

        // Crea un'asta mock con stato diverso da PENDING
        Auction mockAuction = new Auction();
        mockAuction.setId(1L);
        mockAuction.setStatus(AuctionStatus.OPEN);  // Stato diverso da PENDING
        mockAuction.setOwner(mockUser); // Imposta l'utente come proprietario

        // Mock dei repository per le categorie
        MacroCategory mockMacroCategory = new MacroCategory();
        mockMacroCategory.setId(1L);
        SubCategory mockSubCategory = new SubCategory();
        mockSubCategory.setId(1L);

        when(auctionRepository.findById(1L)).thenReturn(Optional.of(mockAuction));
        when(macroCategoryRepository.findById(1L)).thenReturn(Optional.of(mockMacroCategory));
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(mockSubCategory));

        // Verifica che venga lanciata l'eccezione InvalidAuctionStateException
        assertThrows(InvalidAuctionStateException.class, () -> auctionService.updateAuction(auctionRequestDTO));
    }

    @Test
    void testUpdateAuction_UserNotOwner_ThrowsAuctionOwnershipException() {
        // Crea un utente mock (utente che cerca di modificare)
        mockUser.setId(1L);

        // Crea un altro utente mock (proprietario dell'asta)
        User mockOwner = new User();
        mockOwner.setId(2L);  // ID diverso da quello dell'utente che sta cercando di modificare

        // Simula l'autenticazione
        Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Crea un DTO di richiesta per l'asta
        AuctionRequestDTO auctionRequestDTO = new AuctionRequestDTO();
        auctionRequestDTO.setId(1L);
        auctionRequestDTO.setStartAuctionDate(LocalDateTime.now().plusHours(1));
        auctionRequestDTO.setEndAuctionDate(LocalDateTime.now().plusHours(2));
        auctionRequestDTO.setMacroCategoryId(1L);  // ID valido della macro categoria
        auctionRequestDTO.setSubCategoryId(1L);   // ID valido della sottocategoria

        // Crea un'asta mock con il proprietario diverso dall'utente autenticato
        Auction mockAuction = new Auction();
        mockAuction.setId(1L);
        mockAuction.setStatus(AuctionStatus.PENDING);  // Stato PENDING
        mockAuction.setOwner(mockOwner);  // Proprietario diverso

        // Mock delle categorie
        MacroCategory mockMacroCategory = new MacroCategory();
        mockMacroCategory.setId(1L);
        SubCategory mockSubCategory = new SubCategory();
        mockSubCategory.setId(1L);

        // Mock dei repository
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(mockAuction));
        when(macroCategoryRepository.findById(1L)).thenReturn(Optional.of(mockMacroCategory));
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(mockSubCategory));

        // Verifica che venga lanciata l'eccezione AuctionOwnershipException
        assertThrows(AuctionOwnershipException.class, () -> auctionService.updateAuction(auctionRequestDTO));
    }

    @Test
    void testGetAuctionSummary_ReturnsMappedActiveAuctionSummaryList() {
        // Crea un mock per l'oggetto Auction
        Auction mockAuction1 = new Auction();
        mockAuction1.setId(1L);
        mockAuction1.setStatus(AuctionStatus.OPEN);
        mockAuction1.setTitle("Auction 1");

        Auction mockAuction2 = new Auction();
        mockAuction2.setId(2L);
        mockAuction2.setStatus(AuctionStatus.OPEN);
        mockAuction2.setTitle("Auction 2");

        // Crea una lista di aste mockate
        List<Auction> mockAuctions = List.of(mockAuction1, mockAuction2);

        // Crea un mock per l'oggetto AuctionSummaryDTO
        AuctionSummaryDTO mockAuctionSummary1 = new AuctionSummaryDTO();
        mockAuctionSummary1.setId(1L);
        mockAuctionSummary1.setTitle("Auction 1 Summary");

        AuctionSummaryDTO mockAuctionSummary2 = new AuctionSummaryDTO();
        mockAuctionSummary2.setId(2L);
        mockAuctionSummary2.setTitle("Auction 2 Summary");

        // Configura i comportamenti dei mock
        when(auctionRepository.findByStatus(AuctionStatus.OPEN)).thenReturn(mockAuctions);
        when(auctionMapper.auctionToAuctionSummaryDTO(mockAuction1)).thenReturn(mockAuctionSummary1);
        when(auctionMapper.auctionToAuctionSummaryDTO(mockAuction2)).thenReturn(mockAuctionSummary2);

        // Esegui il test del metodo getAuctionSummary
        List<AuctionSummaryDTO> result = auctionService.getActiveAuctionSummary();

        // Verifica che il risultato contenga i due oggetti AuctionSummaryDTO correttamente mappati
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Auction 1 Summary", result.get(0).getTitle());
        assertEquals("Auction 2 Summary", result.get(1).getTitle());

        // Verifica che il metodo findByStatus sia stato chiamato con AuctionStatus.OPEN
        verify(auctionRepository).findByStatus(AuctionStatus.OPEN);
        // Verifica che il mapper abbia eseguito la mappatura correttamente per ogni asta
        verify(auctionMapper).auctionToAuctionSummaryDTO(mockAuction1);
        verify(auctionMapper).auctionToAuctionSummaryDTO(mockAuction2);
    }

    @Test
    void testListClosedAuctions_WithClosedAuctions_ReturnsMappedAuctionDetailsList() {
        // Crea alcune aste chiuse mockate
        Auction mockAuction1 = new Auction();
        mockAuction1.setId(1L);
        mockAuction1.setStatus(AuctionStatus.CLOSED);
        mockAuction1.setTitle("Closed Auction 1");

        Auction mockAuction2 = new Auction();
        mockAuction2.setId(2L);
        mockAuction2.setStatus(AuctionStatus.CLOSED);
        mockAuction2.setTitle("Closed Auction 2");

        List<Auction> mockClosedAuctions = List.of(mockAuction1, mockAuction2);

        // Crea alcuni DTO di risposta per le aste chiuse
        AuctionDetailsDTO mockAuctionDetails1 = new AuctionDetailsDTO();
        mockAuctionDetails1.setId(1L);
        mockAuctionDetails1.setTitle("Closed Auction 1");

        AuctionDetailsDTO mockAuctionDetails2 = new AuctionDetailsDTO();
        mockAuctionDetails2.setId(2L);
        mockAuctionDetails2.setTitle("Closed Auction 2");

        // Configura il comportamento dei mock
        when(auctionRepository.findByStatus(AuctionStatus.CLOSED)).thenReturn(mockClosedAuctions);
        when(auctionMapper.auctionToAuctionResponseDTO(mockAuction1)).thenReturn(mockAuctionDetails1);
        when(auctionMapper.auctionToAuctionResponseDTO(mockAuction2)).thenReturn(mockAuctionDetails2);

        // Esegui il test del metodo listClosedAuctions
        List<AuctionDetailsDTO> result = auctionService.listClosedAuctions();

        // Verifica che il risultato contenga i due oggetti AuctionDetailsDTO correttamente mappati
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Closed Auction 1", result.get(0).getTitle());
        assertEquals("Closed Auction 2", result.get(1).getTitle());

        // Verifica che il metodo findByStatus sia stato chiamato con AuctionStatus.CLOSED
        verify(auctionRepository).findByStatus(AuctionStatus.CLOSED);
        // Verifica che il mapper abbia eseguito la mappatura correttamente per ogni asta
        verify(auctionMapper).auctionToAuctionResponseDTO(mockAuction1);
        verify(auctionMapper).auctionToAuctionResponseDTO(mockAuction2);
    }

    @Test
    void testListClosedAuctions_NoClosedAuctions_ThrowsEntityNotFoundException() {
        // Crea una lista vuota di aste chiuse
        List<Auction> emptyClosedAuctions = new ArrayList<>();

        // Configura il comportamento del mock per restituire una lista vuota
        when(auctionRepository.findByStatus(AuctionStatus.CLOSED)).thenReturn(emptyClosedAuctions);

        // Verifica che venga lanciata l'eccezione EntityNotFoundException
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> auctionService.listClosedAuctions());

        // Verifica che l'eccezione contenga il messaggio corretto
        assertEquals("Nessun asta con stato = CLOSED è stato trovato.", exception.getMessage());

        // Verifica che il metodo findByStatus sia stato chiamato con AuctionStatus.CLOSED
        verify(auctionRepository).findByStatus(AuctionStatus.CLOSED);
    }

    @Test
    void shouldReturnPendingAuctions_whenThereArePendingAuctions() {
        // Arrange: Crea alcune aste in stato PENDING mockate
        Auction mockAuction1 = new Auction();
        mockAuction1.setId(1L);
        mockAuction1.setStatus(AuctionStatus.PENDING);
        mockAuction1.setTitle("Pending Auction 1");

        Auction mockAuction2 = new Auction();
        mockAuction2.setId(2L);
        mockAuction2.setStatus(AuctionStatus.PENDING);
        mockAuction2.setTitle("Pending Auction 2");

        List<Auction> mockPendingAuctions = List.of(mockAuction1, mockAuction2);

        AuctionDetailsDTO mockAuctionDetails1 = new AuctionDetailsDTO();
        mockAuctionDetails1.setId(1L);
        mockAuctionDetails1.setTitle("Pending Auction 1");

        AuctionDetailsDTO mockAuctionDetails2 = new AuctionDetailsDTO();
        mockAuctionDetails2.setId(2L);
        mockAuctionDetails2.setTitle("Pending Auction 2");

        // Configura il comportamento dei mock
        when(auctionRepository.findByStatus(AuctionStatus.PENDING)).thenReturn(mockPendingAuctions);
        when(auctionMapper.auctionToAuctionResponseDTO(mockAuction1)).thenReturn(mockAuctionDetails1);
        when(auctionMapper.auctionToAuctionResponseDTO(mockAuction2)).thenReturn(mockAuctionDetails2);

        // Act: Esegui il metodo
        List<AuctionDetailsDTO> result = auctionService.listPendingAuctions();

        // Assert: Verifica il risultato
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Pending Auction 1", result.get(0).getTitle());
        assertEquals("Pending Auction 2", result.get(1).getTitle());

        verify(auctionRepository).findByStatus(AuctionStatus.PENDING);
        verify(auctionMapper).auctionToAuctionResponseDTO(mockAuction1);
        verify(auctionMapper).auctionToAuctionResponseDTO(mockAuction2);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenNoPendingAuctionsAreFound() {
        // Arrange: Crea una lista vuota di aste in stato PENDING
        List<Auction> emptyPendingAuctions = new ArrayList<>();

        // Configura il comportamento del mock
        when(auctionRepository.findByStatus(AuctionStatus.PENDING)).thenReturn(emptyPendingAuctions);

        // Act & Assert: Verifica che venga lanciata l'eccezione
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> auctionService.listPendingAuctions());

        assertEquals("Nessun asta con stato = PENDING è stato trovato.", exception.getMessage());
        verify(auctionRepository).findByStatus(AuctionStatus.PENDING);
    }

    @Test
    void shouldReturnSubscribedAuctions_whenUserHasSubscribedAuctions() {
        // Arrange: Crea un mock per l'oggetto User
        mockUser.setId(1L);

        Auction mockAuction1 = new Auction();
        mockAuction1.setId(1L);
        mockAuction1.setStatus(AuctionStatus.OPEN);
        mockAuction1.setTitle("Subscribed Auction 1");

        Auction mockAuction2 = new Auction();
        mockAuction2.setId(2L);
        mockAuction2.setStatus(AuctionStatus.OPEN);
        mockAuction2.setTitle("Subscribed Auction 2");

        AuctionSubscription mockSubscription1 = new AuctionSubscription();
        mockSubscription1.setAuction(mockAuction1);
        mockSubscription1.setUser(mockUser);

        AuctionSubscription mockSubscription2 = new AuctionSubscription();
        mockSubscription2.setAuction(mockAuction2);
        mockSubscription2.setUser(mockUser);

        List<AuctionSubscription> mockSubscribedAuctions = List.of(mockSubscription1, mockSubscription2);

        AuctionDetailsDTO mockAuctionDetails1 = new AuctionDetailsDTO();
        mockAuctionDetails1.setId(1L);
        mockAuctionDetails1.setTitle("Subscribed Auction 1");

        AuctionDetailsDTO mockAuctionDetails2 = new AuctionDetailsDTO();
        mockAuctionDetails2.setId(2L);
        mockAuctionDetails2.setTitle("Subscribed Auction 2");

        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContext securityContextNew = mock(SecurityContext.class);
        when(securityContextNew.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContextNew);

        when(auctionSubscriptionRepository.findByUser(mockUser)).thenReturn(mockSubscribedAuctions);
        when(auctionMapper.auctionToAuctionResponseDTO(mockAuction1)).thenReturn(mockAuctionDetails1);
        when(auctionMapper.auctionToAuctionResponseDTO(mockAuction2)).thenReturn(mockAuctionDetails2);

        // Act: Esegui il metodo
        List<AuctionDetailsDTO> result = auctionService.listSubscribedAuctions();

        // Assert: Verifica il risultato
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Subscribed Auction 1", result.get(0).getTitle());
        assertEquals("Subscribed Auction 2", result.get(1).getTitle());

        verify(auctionSubscriptionRepository).findByUser(mockUser);
        verify(auctionMapper).auctionToAuctionResponseDTO(mockAuction1);
        verify(auctionMapper).auctionToAuctionResponseDTO(mockAuction2);
    }

    @Test
    void shouldReturnEmptyList_whenUserHasNoSubscribedAuctions() {
        // Arrange: Crea un mock per l'oggetto User
        mockUser.setId(1L);

        List<AuctionSubscription> emptySubscribedAuctions = new ArrayList<>();

        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContext securityContextNew = mock(SecurityContext.class);
        when(securityContextNew.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContextNew);

        when(auctionSubscriptionRepository.findByUser(mockUser)).thenReturn(emptySubscribedAuctions);

        // Act: Esegui il metodo
        List<AuctionDetailsDTO> result = auctionService.listSubscribedAuctions();

        // Assert: Verifica che il risultato sia vuoto
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(auctionSubscriptionRepository).findByUser(mockUser);
    }

    @Test
    void shouldMapSubscribedAuctionsCorrectly_whenThereAreMultipleSubscriptions() {
        // Arrange: Mock user and authentication
        when(mockUser.getId()).thenReturn(1L);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockUser, null);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Auction mockAuction1 = new Auction();
        mockAuction1.setId(1L);
        Auction mockAuction2 = new Auction();
        mockAuction2.setId(2L);

        AuctionSubscription subscription1 = new AuctionSubscription();
        subscription1.setAuction(mockAuction1);
        AuctionSubscription subscription2 = new AuctionSubscription();
        subscription2.setAuction(mockAuction2);

        List<AuctionSubscription> subscriptions = List.of(subscription1, subscription2);
        when(auctionSubscriptionRepository.findByUser(mockUser)).thenReturn(subscriptions);

        AuctionDetailsDTO mockDTO1 = new AuctionDetailsDTO();
        AuctionDetailsDTO mockDTO2 = new AuctionDetailsDTO();
        when(auctionMapper.auctionToAuctionResponseDTO(mockAuction1)).thenReturn(mockDTO1);
        when(auctionMapper.auctionToAuctionResponseDTO(mockAuction2)).thenReturn(mockDTO2);

        // Act: Call the method
        List<AuctionDetailsDTO> result = auctionService.listSubscribedAuctions();

        // Assert results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(mockDTO1));
        assertTrue(result.contains(mockDTO2));

        verify(auctionSubscriptionRepository, times(1)).findByUser(mockUser);

        ArgumentCaptor<Auction> auctionCaptor = ArgumentCaptor.forClass(Auction.class);
        verify(auctionMapper, times(2)).auctionToAuctionResponseDTO(auctionCaptor.capture());

        List<Auction> capturedAuctions = auctionCaptor.getAllValues();
        assertEquals(2, capturedAuctions.size());
        assertTrue(capturedAuctions.contains(mockAuction1));
        assertTrue(capturedAuctions.contains(mockAuction2));
    }

    @Test
    void testGetActiveAuctionSummaryByUserId() {
        // Dati di test
        long userId = 1L;

        // Creazione degli oggetti Auction mockati
        Auction auction1 = new Auction();
        auction1.setId(1L);
        auction1.setTitle("Auction 1");
        auction1.setStatus(AuctionStatus.OPEN);

        Auction auction2 = new Auction();
        auction2.setId(2L);
        auction2.setTitle("Auction 2");
        auction2.setStatus(AuctionStatus.CLOSED);

        List<Auction> auctions = List.of(auction1, auction2);

        // Creazione dei DTO di risposta
        AuctionSummaryDTO auctionSummaryDTO1 = new AuctionSummaryDTO();
        auctionSummaryDTO1.setId(1L);
        auctionSummaryDTO1.setTitle("Auction 1");

        AuctionSummaryDTO auctionSummaryDTO2 = new AuctionSummaryDTO();
        auctionSummaryDTO2.setId(2L);
        auctionSummaryDTO2.setTitle("Auction 2");

        List<AuctionSummaryDTO> expectedSummaryDTOs = List.of(auctionSummaryDTO1, auctionSummaryDTO2);

        // Configurazione dei mock
        when(auctionRepository.findByOwnerIdAndStatus(userId)).thenReturn(auctions);
        when(auctionMapper.auctionToAuctionSummaryDTO(auction1)).thenReturn(auctionSummaryDTO1);
        when(auctionMapper.auctionToAuctionSummaryDTO(auction2)).thenReturn(auctionSummaryDTO2);

        // Esecuzione del metodo da testare
        List<AuctionSummaryDTO> result = auctionService.getAuctionSummaryByUserId(userId);

        // Verifica che il risultato sia corretto
        assertEquals(expectedSummaryDTOs, result);
    }

    @Test
    void testGetPendingAuctionSummary() {
        // Crea un oggetto Auction di esempio
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setTitle("Test Auction");
        auction.setDescriptionProduct("This is a test auction.");
        auction.setStatus(AuctionStatus.PENDING);

        // Crea un AuctionSummaryDTO di esempio
        AuctionSummaryDTO auctionSummaryDTO = new AuctionSummaryDTO();
        auctionSummaryDTO.setId(auction.getId());
        auctionSummaryDTO.setTitle(auction.getTitle());
        auctionSummaryDTO.setDescription(auction.getDescriptionProduct());
        auctionSummaryDTO.setUser(new UserResponseDTO());
        // Arrange: Crea una lista di aste con stato PENDING
        List<Auction> auctions = List.of(auction);

        // Mocka il comportamento del repository
        when(auctionRepository.findByStatus(AuctionStatus.PENDING)).thenReturn(auctions);

        // Mocka il comportamento del mapper
        when(auctionMapper.auctionToAuctionSummaryDTO(auction)).thenReturn(auctionSummaryDTO);

        // Act: Chiama il metodo da testare
        List<AuctionSummaryDTO> result = auctionService.getPendingAuctionSummary();

        // Assert: Verifica che il risultato contenga il DTO corretto
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(auction.getId(), result.getFirst().getId());
        assertEquals(auction.getTitle(), result.getFirst().getTitle());
        assertEquals(auction.getDescriptionProduct(), result.getFirst().getDescription());
        assertNotNull(result.getFirst().getUser());

        // Verifica che i metodi del repository e del mapper siano stati chiamati
        verify(auctionRepository, times(1)).findByStatus(AuctionStatus.PENDING);
        verify(auctionMapper, times(1)).auctionToAuctionSummaryDTO(auction);
    }

    @Test
    void getClosedAndWithoutWinnerAuctionSummary_returnsAuctionSummaryList_whenAuctionsExist() {
        mockUser.setId(1L);

        Auction mockAuction1 = new Auction();
        mockAuction1.setId(1L);
        mockAuction1.setStatus(AuctionStatus.CLOSED);

        Auction mockAuction2 = new Auction();
        mockAuction2.setId(2L);
        mockAuction2.setStatus(AuctionStatus.CLOSED);

        List<Auction> mockAuctions = List.of(mockAuction1, mockAuction2);

        AuctionSummaryDTO mockSummaryDTO1 = new AuctionSummaryDTO();
        mockSummaryDTO1.setId(1L);

        AuctionSummaryDTO mockSummaryDTO2 = new AuctionSummaryDTO();
        mockSummaryDTO2.setId(2L);

        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContext securityContextNew = mock(SecurityContext.class);
        when(securityContextNew.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContextNew);

        when(auctionRepository.findByStatusAndWinnerIsNullAndOwner(AuctionStatus.CLOSED, mockUser)).thenReturn(mockAuctions);
        when(auctionMapper.auctionToAuctionSummaryDTO(mockAuction1)).thenReturn(mockSummaryDTO1);
        when(auctionMapper.auctionToAuctionSummaryDTO(mockAuction2)).thenReturn(mockSummaryDTO2);

        List<AuctionSummaryDTO> result = auctionService.getClosedAndWithoutWinnerAuctionSummary();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(auctionRepository).findByStatusAndWinnerIsNullAndOwner(AuctionStatus.CLOSED, mockUser);
        verify(auctionMapper).auctionToAuctionSummaryDTO(mockAuction1);
        verify(auctionMapper).auctionToAuctionSummaryDTO(mockAuction2);
    }

    @Test
    void getClosedAndWithoutWinnerAuctionSummary_returnsEmptyList_whenNoAuctionsExist() {
        mockUser.setId(1L);

        List<Auction> emptyAuctions = Collections.emptyList();

        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContext securityContextNew = mock(SecurityContext.class);
        when(securityContextNew.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContextNew);

        when(auctionRepository.findByStatusAndWinnerIsNullAndOwner(AuctionStatus.CLOSED, mockUser)).thenReturn(emptyAuctions);

        List<AuctionSummaryDTO> result = auctionService.getClosedAndWithoutWinnerAuctionSummary();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(auctionRepository).findByStatusAndWinnerIsNullAndOwner(AuctionStatus.CLOSED, mockUser);
        verifyNoInteractions(auctionMapper);
    }

    @Test
    void assignWinner_WithValidAuctionAndWinner_ReturnsTrue() {
        long auctionId = 1L;
        long winnerId = 2L;
        mockUser.setId(3L);
        Auction mockAuction = new Auction();
        mockAuction.setId(auctionId);
        mockAuction.setStatus(AuctionStatus.CLOSED);
        mockAuction.setOwner(mockUser);
        User mockWinner = new User();
        mockWinner.setId(winnerId);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(mockAuction));
        when(userRepository.findById(winnerId)).thenReturn(Optional.of(mockWinner));
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(mockUser, null));
        SecurityContextHolder.setContext(securityContext);

        boolean result = auctionService.assignWinner(auctionId, winnerId);

        assertTrue(result);
        verify(auctionRepository).save(mockAuction);
        verify(auctionManager).notifyAuctionWinner(mockAuction, mockWinner);
    }

    @Test
    void assignWinner_WhenAuctionNotFound_ThrowsEntityNotFoundException() {
        long auctionId = 1L;
        long winnerId = 2L;
        mockUser.setId(3L);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(mockUser, null));
        SecurityContextHolder.setContext(securityContext);

        assertThrows(EntityNotFoundException.class, () -> auctionService.assignWinner(auctionId, winnerId));
    }

    @Test
    void assignWinner_WhenWinnerNotFound_ThrowsEntityNotFoundException() {
        long auctionId = 1L;
        long winnerId = 2L;
        mockUser.setId(3L);
        Auction mockAuction = new Auction();
        mockAuction.setId(auctionId);
        mockAuction.setStatus(AuctionStatus.CLOSED);
        mockAuction.setOwner(mockUser);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(mockAuction));
        when(userRepository.findById(winnerId)).thenReturn(Optional.empty());
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(mockUser, null));
        SecurityContextHolder.setContext(securityContext);

        assertThrows(EntityNotFoundException.class, () -> auctionService.assignWinner(auctionId, winnerId));
    }

    @Test
    void assignWinner_WhenUserNotOwner_ThrowsAuctionOwnershipException() {
        long auctionId = 1L;
        long winnerId = 2L;
        mockUser.setId(3L);
        User mockOwner = new User();
        mockOwner.setId(4L);
        Auction mockAuction = new Auction();
        mockAuction.setId(auctionId);
        mockAuction.setStatus(AuctionStatus.CLOSED);
        mockAuction.setOwner(mockOwner);
        User mockWinner = new User();
        mockWinner.setId(winnerId);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(mockAuction));
        when(userRepository.findById(winnerId)).thenReturn(Optional.of(mockWinner));
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(mockUser, null));
        SecurityContextHolder.setContext(securityContext);

        assertThrows(AuctionOwnershipException.class, () -> auctionService.assignWinner(auctionId, winnerId));
    }

    @Test
    void assignWinner_WhenAuctionNotClosed_ThrowsAuctionException() {
        long auctionId = 1L;
        long winnerId = 2L;
        mockUser.setId(3L);
        Auction mockAuction = new Auction();
        mockAuction.setId(auctionId);
        mockAuction.setStatus(AuctionStatus.OPEN);
        mockAuction.setOwner(mockUser);
        User mockWinner = new User();
        mockWinner.setId(winnerId);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(mockAuction));
        when(userRepository.findById(winnerId)).thenReturn(Optional.of(mockWinner));
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(mockUser, null));
        SecurityContextHolder.setContext(securityContext);

        assertThrows(AuctionException.class, () -> auctionService.assignWinner(auctionId, winnerId));
    }

    @Test
    void assignWinner_WhenSaveFails_ThrowsAuctionException() {
        long auctionId = 1L;
        long winnerId = 2L;
        mockUser.setId(3L);
        Auction mockAuction = new Auction();
        mockAuction.setId(auctionId);
        mockAuction.setStatus(AuctionStatus.CLOSED);
        mockAuction.setOwner(mockUser);
        User mockWinner = new User();
        mockWinner.setId(winnerId);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(mockAuction));
        when(userRepository.findById(winnerId)).thenReturn(Optional.of(mockWinner));
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(mockUser, null));
        SecurityContextHolder.setContext(securityContext);
        doThrow(new RuntimeException("Database error")).when(auctionRepository).save(mockAuction);

        assertThrows(AuctionException.class, () -> auctionService.assignWinner(auctionId, winnerId));
    }

    @Test
    void getPendingAndWithoutWinnerAuctionSummary_NoAuctions_ReturnsEmptyList() {
        when(auctionRepository.findByStatusAndWinnerIsNullAndOwner(
                AuctionStatus.PENDING, mockUser))
                .thenReturn(Collections.emptyList());

        List<AuctionSummaryDTO> result =
                auctionService.getPendingAndWithoutWinnerAuctionSummary();

        assertTrue(result.isEmpty());
        verify(auctionRepository).findByStatusAndWinnerIsNullAndOwner(
                AuctionStatus.PENDING, mockUser);
    }

    @Test
    void getPendingAndWithoutWinnerAuctionSummary_WithAuctions_ReturnsMappedDTOs() {
        Auction auction = new Auction();
        List<Auction> auctions = Collections.singletonList(auction);
        AuctionSummaryDTO dto = new AuctionSummaryDTO();

        when(auctionRepository.findByStatusAndWinnerIsNullAndOwner(
                AuctionStatus.PENDING, mockUser)).thenReturn(auctions);
        when(auctionMapper.auctionToAuctionSummaryDTO(auction)).thenReturn(dto);

        List<AuctionSummaryDTO> result =
                auctionService.getPendingAndWithoutWinnerAuctionSummary();

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());
        verify(auctionMapper).auctionToAuctionSummaryDTO(auction);
    }

    // Test per getOpenAndWithoutWinnerAuctionSummary
    @Test
    void getOpenAndWithoutWinnerAuctionSummary_NoAuctions_ReturnsEmptyList() {
        when(auctionRepository.findByStatusAndWinnerIsNullAndOwner(
                AuctionStatus.OPEN, mockUser))
                .thenReturn(Collections.emptyList());

        List<AuctionSummaryDTO> result =
                auctionService.getOpenAndWithoutWinnerAuctionSummary();

        assertTrue(result.isEmpty());
        verify(auctionRepository).findByStatusAndWinnerIsNullAndOwner(
                AuctionStatus.OPEN, mockUser);
    }

    @Test
    void getOpenAndWithoutWinnerAuctionSummary_WithAuctions_ReturnsMappedDTOs() {
        Auction auction = new Auction();
        List<Auction> auctions = Collections.singletonList(auction);
        AuctionSummaryDTO dto = new AuctionSummaryDTO();

        when(auctionRepository.findByStatusAndWinnerIsNullAndOwner(
                AuctionStatus.OPEN, mockUser)).thenReturn(auctions);
        when(auctionMapper.auctionToAuctionSummaryDTO(auction)).thenReturn(dto);

        List<AuctionSummaryDTO> result =
                auctionService.getOpenAndWithoutWinnerAuctionSummary();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
        verify(auctionMapper).auctionToAuctionSummaryDTO(auction);
    }

    // Test per getAuctionSubscriptionByAuctionId
    @Test
    void getAuctionSubscriptionByAuctionId_AuctionExistsAndSubscribed_ReturnsTrue() {
        long auctionId = 1L;
        Auction auction = new Auction();

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(auctionSubscriptionRepository.existsByAuctionAndUser(auction, mockUser))
                .thenReturn(true);

        Boolean result = auctionService.getAuctionSubscriptionByAuctionId(auctionId);

        assertTrue(result);
        verify(auctionSubscriptionRepository).existsByAuctionAndUser(auction, mockUser);
    }

    @Test
    void getAuctionSubscriptionByAuctionId_AuctionExistsNotSubscribed_ReturnsFalse() {
        long auctionId = 1L;
        Auction auction = new Auction();

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(auctionSubscriptionRepository.existsByAuctionAndUser(auction, mockUser))
                .thenReturn(false);

        Boolean result = auctionService.getAuctionSubscriptionByAuctionId(auctionId);

        assertFalse(result);
        verify(auctionSubscriptionRepository).existsByAuctionAndUser(auction, mockUser);
    }

    @Test
    void getAuctionSubscriptionByAuctionId_AuctionNotFound_ThrowsException() {
        long auctionId = 1L;

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> auctionService.getAuctionSubscriptionByAuctionId(auctionId));
    }
}

