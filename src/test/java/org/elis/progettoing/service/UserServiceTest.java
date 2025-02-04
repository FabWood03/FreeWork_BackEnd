package org.elis.progettoing.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.elis.progettoing.dto.request.user.UserEditRequest;
import org.elis.progettoing.dto.response.user.UserResponseDTO;
import org.elis.progettoing.enumeration.Role;
import org.elis.progettoing.exception.ActiveUserEditException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityEditException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.UserMapper;
import org.elis.progettoing.models.User;
import org.elis.progettoing.repository.ProductRepository;
import org.elis.progettoing.repository.ReviewRepository;
import org.elis.progettoing.repository.TicketRepository;
import org.elis.progettoing.repository.UserRepository;
import org.elis.progettoing.service.implementation.LocalStorageService;
import org.elis.progettoing.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<User> criteriaQuery;

    @Mock
    private Root<User> root;

    @Mock
    private TypedQuery<User> typedQuery;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UsernamePasswordAuthenticationToken authentication;

    @Mock
    private LocalStorageService localStorageService;

    @Mock
    private Expression<String> lowerName;

    @Mock
    private Expression<String> lowerSurname;

    @Mock
    private Expression<String> lowerNickname;

    @Mock
    private Predicate namePredicate;

    @Mock
    private Predicate surnamePredicate;

    @Mock
    private Predicate nicknamePredicate;

    @Mock
    private Predicate orPredicate;

    @InjectMocks
    private UserServiceImpl userService;

    private User authenticatedUser;
    private User targetUser;
    private User user;
    private UserResponseDTO userResponseDTO;
    private UserEditRequest userRequestDTO;
    private final User user1 = new User();
    private final User user2 = new User();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String uploadedPhotoId = "photoId";

        user1.setId(1L);
        user1.setName("Mario");
        user1.setSurname("Rossi");
        user1.setNickname("mario_rossi");

        user2.setId(2L);
        user2.setName("Luca");
        user2.setSurname("Bianchi");
        user2.setNickname("luca_bianchi");

        // Configurazione base dei mock per Criteria API
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setRole(Role.ADMIN);
        authenticatedUser.setActive(true);
        authenticatedUser.setUrlUserPhoto(uploadedPhotoId);

        userRequestDTO = new UserEditRequest();
        userRequestDTO.setName("UpdatedName");
        userRequestDTO.setSurname("UpdatedSurname");
        userRequestDTO.setBirthDate(LocalDate.parse("2000-01-01"));
        userRequestDTO.setNickname("Nick");
        userRequestDTO.setEducation("Education");

        targetUser = new User();
        targetUser.setId(2L);
        targetUser.setRole(Role.BUYER);
        targetUser.setActive(true);

        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john.doe@example.com");
        user.setRole(Role.ADMIN);

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("John");
        userResponseDTO.setSurname("Doe");
        userResponseDTO.setEmail("john.doe@example.com");

        authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null);
    }

    @Test
    void testUpdate_UserExists_ShouldUpdateUser() {
        MultipartFile userPhoto = mock(MultipartFile.class);
        String uploadedPhotoId = "photoId";

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(localStorageService.updateUserProfilePhoto(userPhoto, authenticatedUser.getUrlUserPhoto())).thenReturn(uploadedPhotoId);
        when(userRepository.findById(1L)).thenReturn(Optional.of(authenticatedUser));
        when(userMapper.userToUserResponseDTO(any(User.class))).thenReturn(new UserResponseDTO());

        UserResponseDTO response = userService.update(userRequestDTO, userPhoto);

        verify(localStorageService).updateUserProfilePhoto(userPhoto, authenticatedUser.getUrlUserPhoto());
        verify(userRepository).save(authenticatedUser);

        assertNotNull(response, "La risposta del servizio non dovrebbe essere nulla.");
        assertEquals(uploadedPhotoId, authenticatedUser.getUrlUserPhoto(), "L'ID della foto dell'utente dovrebbe essere stato aggiornato.");
        assertEquals("UpdatedName", authenticatedUser.getName(), "Il nome dell'utente dovrebbe essere aggiornato.");
    }


    @Test
    void testUpdate_UserNotFound_ShouldThrowException() {
        MultipartFile userPhoto = mock(MultipartFile.class);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.update(userRequestDTO, userPhoto));
        assertEquals("Nessun utente con ID = 1 è stato trovato.", exception.getMessage());
    }

    @Test
    void testRemove_UserExists_ShouldRemoveUser() {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User userToRemove = new User();
        userToRemove.setId(2L);
        userToRemove.setRole(Role.BUYER);
        userToRemove.setUrlUserPhoto("photoUrl");

        when(userRepository.findById(2L)).thenReturn(Optional.of(userToRemove));

        boolean result = userService.remove(2L);

        verify(ticketRepository).unsetReportedUser(2L);
        verify(ticketRepository).unsetRequesterId(2L);
        verify(reviewRepository).unsetUser(2L);
        verify(productRepository).unsetUser(2L);
        verify(userRepository).delete(userToRemove);

        assertTrue(result);
    }

    @Test
    void testRemove_AuthenticatedUserHasLowerRoleAndTriesToDeleteAnotherUser() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        user.setRole(Role.BUYER);

        User userToRemove = new User();
        userToRemove.setId(2L);
        userToRemove.setRole(Role.MODERATOR);
        userToRemove.setUrlUserPhoto("photoUrl");

        authenticatedUser.setRole(Role.BUYER);

        when(userRepository.findById(2L)).thenReturn(Optional.of(userToRemove));

        ActiveUserEditException exception = assertThrows(ActiveUserEditException.class, () -> userService.remove(2L));

        assertEquals("Si è verificato un errore durante il tentativo di eliminare l'utente con ID = 2.", exception.getMessage());
    }

    @Test
    void testRemove_UserCannotRemove_ShouldThrowEntityDeletionException() {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User userToRemove = new User();
        userToRemove.setId(2L);
        userToRemove.setRole(Role.BUYER);
        userToRemove.setUrlUserPhoto("photoUrl");

        when(userRepository.findById(2L)).thenReturn(Optional.of(userToRemove));
        doThrow(new RuntimeException("Database error")).when(userRepository).delete(userToRemove);

        EntityDeletionException exception = assertThrows(EntityDeletionException.class, () -> userService.remove(2L));

        assertEquals("Si è verificato un errore durante il tentativo di eliminare utente con ID = 2.", exception.getMessage());
    }

    @Test
    void testRemove_UserNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.remove(1L));

        assertEquals("Nessun utente con ID = 1 è stato trovato.", exception.getMessage());
    }

    @Test
    void testRemove_UserCannotRemoveHigherRole_ShouldThrowActiveUserEditException() {
        User mockUser = new User();
        mockUser.setId(2L);
        mockUser.setRole(Role.ADMIN);

        authenticatedUser.setRole(Role.MODERATOR);

        when(userRepository.findById(2L)).thenReturn(Optional.of(mockUser));

        ActiveUserEditException exception = assertThrows(ActiveUserEditException.class, () -> userService.remove(2L));

        assertEquals("Si è verificato un errore durante il tentativo di eliminare l'utente con ID = 2.", exception.getMessage());
    }

    @Test
    void testUpdate_UserExists_ShouldThrowEntityEditException() {
        MultipartFile userPhoto = mock(MultipartFile.class);
        String uploadedPhotoId = "photoId";

        when(localStorageService.updateUserProfilePhoto(userPhoto, authenticatedUser.getUrlUserPhoto())).thenReturn(uploadedPhotoId);
        when(userRepository.findById(1L)).thenReturn(Optional.of(authenticatedUser));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        doThrow(new RuntimeException("Database error")).when(userRepository).save(any(User.class));
        EntityEditException exception = assertThrows(EntityEditException.class, () -> userService.update(userRequestDTO, userPhoto));

        assertEquals("Si è verificato un errore nell'aggiornamento dell'entità ID con utente = 1.", exception.getMessage());

        verify(userRepository).save(authenticatedUser);
    }

    @Test
    void testFindAll_ShouldReturnUserList() {
        List<User> mockUsers = List.of(new User(), new User());
        List<UserResponseDTO> mockUserDTOs = List.of(new UserResponseDTO(), new UserResponseDTO());

        when(userRepository.findAll()).thenReturn(mockUsers);
        when(userMapper.usersToUserDTOs(mockUsers)).thenReturn(mockUserDTOs);

        List<UserResponseDTO> response = userService.findAll();

        assertNotNull(response);
        assertEquals(2, response.size());

        verify(userMapper).usersToUserDTOs(mockUsers);
    }

    @Test
    void enableById_ShouldEnableUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        userService.enableById(2L);

        assertTrue(targetUser.isActive());
        verify(userRepository).save(targetUser);
    }

    @Test
    void enableById_ShouldThrowActiveUserEditException_WhenDisablingSelf() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(authenticatedUser));

        ActiveUserEditException exception = assertThrows(ActiveUserEditException.class, () -> userService.enableById(1L));

        assertEquals("Si è verificato un errore durante il tentativo di attivare l'utente con ID = 1.", exception.getMessage());
    }

    @Test
    void enableById_ShouldThrowActiveUserEditException_WhenRoleNotSufficient() {
        authenticatedUser.setRole(Role.BUYER);
        authenticatedUser.setId(1L); // Ensure authenticatedUser has an ID
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        targetUser.setRole(Role.ADMIN);

        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        ActiveUserEditException exception = assertThrows(ActiveUserEditException.class, () -> userService.enableById(2L));

        assertEquals("Si è verificato un errore durante il tentativo di attivare l'utente con ID = 2.", exception.getMessage());
    }

    @Test
    void enableById_ShouldThrowActiveUserEditException_WhenSaveFails() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        doThrow(new RuntimeException("Errore")).when(userRepository).save(targetUser);
        ActiveUserEditException exception = assertThrows(ActiveUserEditException.class, () -> userService.enableById(2L));

        assertEquals("Si è verificato un errore durante il tentativo di attivare l'utente con ID = 2.", exception.getMessage());
    }

    @Test
    void testDisableById_ShouldDisableUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        userService.disableById(2L);

        assertFalse(targetUser.isActive());

        verify(userRepository).save(targetUser);
    }

    @Test
    void testDisableById_ShouldThrowActiveUserEditException_WhenDisablingSelf() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(authenticatedUser));

        ActiveUserEditException exception = assertThrows(ActiveUserEditException.class, () -> userService.disableById(1L));

        assertEquals("Si è verificato un errore durante il tentativo di disattivare l'utente con ID = 1.", exception.getMessage());
    }

    @Test
    void testDisableById_ShouldThrowActiveUserEditException_WhenRoleNotSufficient() {
        targetUser.setRole(Role.ADMIN);

        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        ActiveUserEditException exception = assertThrows(ActiveUserEditException.class, () -> userService.disableById(2L));

        assertEquals("Si è verificato un errore durante il tentativo di disattivare l'utente con ID = 2.", exception.getMessage());
    }

    @Test
    void testDisableById_ShouldThrowActiveUserEditException_WhenSaveFails() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        ActiveUserEditException exception = assertThrows(ActiveUserEditException.class, () -> userService.disableById(2L));

        assertEquals("Si è verificato un errore durante il tentativo di disattivare l'utente con ID = 2.", exception.getMessage());
    }

    @Test
    void testFindById_UserExists_ShouldReturnUserResponseDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());

        verify(userRepository).findById(1L);
        verify(userMapper).userToUserResponseDTO(user);
    }

    @Test
    void testFindById_UserNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));

        assertEquals("Nessun utente con ID = 1 è stato trovato.", exception.getMessage());

        verify(userRepository).findById(1L);
    }

    @Test
    void testFindByEmail_UserExists_ShouldReturnUserResponseDTO() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.findByEmail(user.getEmail());

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        assertEquals("john.doe@example.com", result.getEmail());

        verify(userRepository).findByEmail(user.getEmail());
        verify(userMapper).userToUserResponseDTO(user);
    }

    @Test
    void testFindByEmail_UserNotFound_ShouldThrowEntityNotFoundException() {
        String email = "john.doe@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.findByEmail(email));

        assertEquals("Nessun utente con email = john.doe@example.com è stato trovato.", exception.getMessage());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void testUpdateUserRole_UserExists_ShouldUpdateUserRole() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        userService.updateUserRole(2L, String.valueOf(Role.ADMIN));

        assertEquals(Role.ADMIN, targetUser.getRole());
        verify(userRepository).save(targetUser);
    }

    @Test
    void testUpdateUserRole_UserNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        String role = String.valueOf(Role.ADMIN);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(2L, role));

        assertEquals("Nessun utente con ID = 2 è stato trovato.", exception.getMessage());
    }

    @Test
    void testUpdateUserRole_AuthenticatedUserHasLowerRoleAndTriesToUpdateAnotherUser() {
        user.setRole(Role.BUYER);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        authenticatedUser.setRole(Role.BUYER);
        targetUser.setRole(Role.ADMIN); // Ensure targetUser has a role

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String role = String.valueOf(Role.ADMIN);

        ActiveUserEditException exception = assertThrows(ActiveUserEditException.class, () -> userService.updateUserRole(2L, role));

        assertEquals("Si è verificato un errore durante il tentativo di aggiornare il ruolo l'utente con ID = 2.", exception.getMessage());
    }

    @Test
    void testUpdateUserRole_UserCannotUpdateRole_ShouldThrowEntityEditException() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        doThrow(new RuntimeException("Database error")).when(userRepository).save(targetUser);

        String role = String.valueOf(Role.ADMIN);

        EntityEditException exception = assertThrows(EntityEditException.class, () -> userService.updateUserRole(2L, role));

        assertEquals("Si è verificato un errore nell'aggiornamento dell'entità ID con utente = 2.", exception.getMessage());
    }

    @Test
    void getAllUserFiltered_WithEmptySearch_ShouldReturnAllUsers() {
        // Configurazione
        List<User> allUsers = Arrays.asList(user1, user2);
        when(typedQuery.getResultList()).thenReturn(allUsers);
        when(userMapper.usersToUserDTOs(allUsers)).thenReturn(
                Arrays.asList(
                        new UserResponseDTO(1L, "Mario"),
                        new UserResponseDTO(2L, "Luca")
                )
        );

        // Esecuzione con ricerca vuota
        List<UserResponseDTO> result = userService.getAllUserFiltered("");

        // Verifiche
        verify(criteriaQuery, never()).where(any(Expression.class));
        assertEquals(2, result.size());
    }

    @Test
    void getAllUserFiltered_WithNullSearch_ShouldReturnAllUsers() {
        // Configurazione
        List<User> allUsers = Arrays.asList(user1, user2);
        when(typedQuery.getResultList()).thenReturn(allUsers);
        when(userMapper.usersToUserDTOs(allUsers)).thenReturn(
                Arrays.asList(
                        new UserResponseDTO(1L, "Mario"),
                        new UserResponseDTO(2L, "Luca")
                )
        );

        // Esecuzione con ricerca nulla
        List<UserResponseDTO> result = userService.getAllUserFiltered(null);

        // Verifiche
        verify(criteriaQuery, never()).where(any(Expression.class));
        assertEquals(2, result.size());
    }

    @Test
    void getAllUserFiltered_WithWhitespaceSearch_ShouldReturnAllUsers() {
        // Configurazione
        List<User> allUsers = Arrays.asList(user1, user2);
        when(typedQuery.getResultList()).thenReturn(allUsers);
        when(userMapper.usersToUserDTOs(allUsers)).thenReturn(
                Arrays.asList(
                        new UserResponseDTO(1L, "Mario"),
                        new UserResponseDTO(2L, "Luca")
                )
        );

        // Esecuzione con spazi bianchi
        List<UserResponseDTO> result = userService.getAllUserFiltered("   ");

        // Verifiche
        verify(criteriaQuery, never()).where(any(Expression.class));
        assertEquals(2, result.size());
    }

    @Test
    void getAllUserFiltered_WithEmptySearch_ShouldNotApplyFilters() {
        // Configurazione ricerca vuota
        String search = "";
        List<User> allUsers = Arrays.asList(user1, user2);

        // Configurazione risultato query
        when(typedQuery.getResultList()).thenReturn(allUsers);
        when(userMapper.usersToUserDTOs(allUsers)).thenReturn(
                Arrays.asList(
                        new UserResponseDTO(1L, "Mario"),
                        new UserResponseDTO(2L, "Luca")
                )
        );

        // Esecuzione
        List<UserResponseDTO> result = userService.getAllUserFiltered(search);

        // Verifiche
        // Verifica che non siano stati applicati filtri
        verify(criteriaQuery, never()).where(any(Predicate[].class));

        // Verifica il risultato
        assertEquals(2, result.size());
    }

    @Test
    void getAllUserFiltered_WithNullSearch_ShouldNotApplyFilters() {
        // Configurazione ricerca nulla
        String search = null;
        List<User> allUsers = Arrays.asList(user1, user2);

        // Configurazione risultato query
        when(typedQuery.getResultList()).thenReturn(allUsers);
        when(userMapper.usersToUserDTOs(allUsers)).thenReturn(
                Arrays.asList(
                        new UserResponseDTO(1L, "Mario"),
                        new UserResponseDTO(2L, "Luca")
                )
        );

        // Esecuzione
        List<UserResponseDTO> result = userService.getAllUserFiltered(search);

        // Verifiche
        // Verifica che non siano stati applicati filtri
        verify(criteriaQuery, never()).where(any(Predicate[].class));

        // Verifica il risultato
        assertEquals(2, result.size());
    }

    @Test
    void getAllUserFiltered_WithWhitespaceSearch_ShouldNotApplyFilters() {
        // Configurazione ricerca con spazi bianchi
        String search = "   ";
        List<User> allUsers = Arrays.asList(user1, user2);

        // Configurazione risultato query
        when(typedQuery.getResultList()).thenReturn(allUsers);
        when(userMapper.usersToUserDTOs(allUsers)).thenReturn(
                Arrays.asList(
                        new UserResponseDTO(1L, "Mario"),
                        new UserResponseDTO(2L, "Luca")
                )
        );

        // Esecuzione
        List<UserResponseDTO> result = userService.getAllUserFiltered(search);

        // Verifiche
        // Verifica che non siano stati applicati filtri
        verify(criteriaQuery, never()).where(any(Predicate[].class));

        // Verifica il risultato
        assertEquals(2, result.size());
    }
}
