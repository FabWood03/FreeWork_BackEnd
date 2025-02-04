package org.elis.progettoing.service;


import org.elis.progettoing.dto.TokenDTO;
import org.elis.progettoing.dto.request.user.UserLoginRequest;
import org.elis.progettoing.dto.request.user.UserRequestDTO;
import org.elis.progettoing.enumeration.Role;
import org.elis.progettoing.exception.InvalidCredentialsException;
import org.elis.progettoing.exception.TokenGenerationException;
import org.elis.progettoing.exception.UserNotActiveException;
import org.elis.progettoing.exception.entity.EntityAlreadyExistsException;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.mapper.definition.UserMapper;
import org.elis.progettoing.models.Cart;
import org.elis.progettoing.models.User;
import org.elis.progettoing.repository.UserRepository;
import org.elis.progettoing.security.JwtService;
import org.elis.progettoing.service.implementation.AuthServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    AuthServiceImpl authService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    JwtService jwtService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testLogin_Success() {
        User user = new User();
        user.setEmail("user@example.it");
        user.setPassword("12345678");
        user.setActive(true);

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail("user@example.it");
        userLoginRequest.setPassword("12345678");

        Mockito.when(userRepository.findByEmail(userLoginRequest.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        Mockito.when(jwtService.generateToken(user.getEmail())).thenReturn("mocked-jwt-token");

        TokenDTO tokenDTO = authService.login(userLoginRequest);

        Assertions.assertEquals("mocked-jwt-token", tokenDTO.getToken());

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(userLoginRequest.getEmail());
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
        Mockito.verify(jwtService, Mockito.times(1)).generateToken(user.getEmail());
    }

    @Test
    void testLogin_InvalidCredentials() {
        User user = new User();
        user.setEmail("user@example.it");
        user.setPassword("encoded-password");
        user.setActive(false);

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail("user@example.it");
        userLoginRequest.setPassword("wrongpassword");  // Password sbagliata

        Mockito.when(userRepository.findByEmail(userLoginRequest.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Credenziali non valide"));

        Assertions.assertThrows(InvalidCredentialsException.class, () -> authService.login(userLoginRequest));

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(userLoginRequest.getEmail());
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogin_UserNotActive() {
        User user = new User();
        user.setEmail("user@example.it");
        user.setPassword("encoded-password");
        user.setActive(false);

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail("user@example.it");
        userLoginRequest.setPassword("12345678");

        Mockito.when(userRepository.findByEmail(userLoginRequest.getEmail())).thenReturn(Optional.of(user));

        Assertions.assertThrows(UserNotActiveException.class, () -> authService.login(userLoginRequest));

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(userLoginRequest.getEmail());
    }

    @Test
    void testLogin_TokenGenerationError() {
        // Arrange
        User user = new User();
        user.setEmail("user@example.it");
        user.setPassword("encoded-password");
        user.setActive(true);

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail("user@example.it");
        userLoginRequest.setPassword("12345678");

        Mockito.when(userRepository.findByEmail(userLoginRequest.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);  // Simuliamo che l'autenticazione abbia successo
        Mockito.when(jwtService.generateToken(user.getEmail())).thenThrow(new RuntimeException("Error generating token"));  // Simuliamo un errore nella generazione del token

        // Act & Assert
        Assertions.assertThrows(TokenGenerationException.class, () -> authService.login(userLoginRequest));

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(userLoginRequest.getEmail());
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testRegisterAdmin_Success() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("buyer@example.it");
        userRequestDTO.setPassword("adminpassword");

        userRequestDTO.setRole(Role.ADMIN.name());

        User user = new User();
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());

        Mockito.when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn("encodedPassword");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        Mockito.when(jwtService.generateToken(user.getEmail())).thenReturn("generatedToken");

        Mockito.when(userMapper.userRequestDTOToUser(Mockito.any(UserRequestDTO.class))).thenReturn(user);

        TokenDTO result = authService.registerAdmin(userRequestDTO);

        Assertions.assertEquals("ADMIN", userRequestDTO.getRole());

        Assertions.assertNotNull(result);  // Verifica che il risultato non sia null
        Assertions.assertEquals("generatedToken", result.getToken());  // Verifica che il token sia corretto

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));

        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(Mockito.anyString());

        Mockito.verify(jwtService, Mockito.times(1)).generateToken(Mockito.anyString());

        Mockito.verify(userMapper, Mockito.times(1)).userRequestDTOToUser(Mockito.any(UserRequestDTO.class));
    }

    @Test
    void testRegisterModerator_Success() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("buyer@example.it");
        userRequestDTO.setPassword("moderatorpassword");

        userRequestDTO.setRole(Role.MODERATOR.name());

        User user = new User();
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());

        Mockito.when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn("encodedPassword");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        Mockito.when(jwtService.generateToken(user.getEmail())).thenReturn("generatedToken");

        Mockito.when(userMapper.userRequestDTOToUser(Mockito.any(UserRequestDTO.class))).thenReturn(user);

        TokenDTO result = authService.registerModerator(userRequestDTO);

        Assertions.assertEquals("MODERATOR", userRequestDTO.getRole());

        Assertions.assertNotNull(result);
        Assertions.assertEquals("generatedToken", result.getToken());

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));

        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(Mockito.anyString());

        Mockito.verify(jwtService, Mockito.times(1)).generateToken(Mockito.anyString());

        Mockito.verify(userMapper, Mockito.times(1)).userRequestDTOToUser(Mockito.any(UserRequestDTO.class));
    }

    @Test
    void testRegisterBuyer_Success() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("buyer@example.it");
        userRequestDTO.setPassword("buyerpassword");

        userRequestDTO.setRole(Role.BUYER.name());

        User user = new User();
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());

        Mockito.when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn("encodedPassword");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        Mockito.when(jwtService.generateToken(user.getEmail())).thenReturn("generatedToken");

        Mockito.when(userMapper.userRequestDTOToUser(Mockito.any(UserRequestDTO.class))).thenReturn(user);

        TokenDTO result = authService.registerBuyer(userRequestDTO);

        Assertions.assertEquals("BUYER", userRequestDTO.getRole());

        Assertions.assertNotNull(result);  // Verifica che il risultato non sia null
        Assertions.assertEquals("generatedToken", result.getToken());  // Verifica che il token sia corretto

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));

        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(Mockito.anyString());

        Mockito.verify(jwtService, Mockito.times(1)).generateToken(Mockito.anyString());

        Mockito.verify(userMapper, Mockito.times(1)).userRequestDTOToUser(Mockito.any(UserRequestDTO.class));
    }

    @Test
    void testRegister_DataIntegrityViolationException() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("existinguser@example.it");
        userRequestDTO.setPassword("password123");

        // 2. Creazione dell'oggetto User che useremo nel test
        User user = new User();
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());

        // 3. Mocking delle dipendenze
        Mockito.when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn("encodedPassword");

        // Mocking di userMapper per mappare il DTO in un oggetto User
        Mockito.when(userMapper.userRequestDTOToUser(userRequestDTO)).thenReturn(user);

        // Simuliamo che venga sollevata una DataIntegrityViolationException durante il salvataggio dell'utente
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenThrow(new DataIntegrityViolationException("Email già presente"));

        Assertions.assertThrows(EntityAlreadyExistsException.class, () -> authService.register(userRequestDTO));
        // 6. Verifica che il metodo generateToken non venga mai invocato
        Mockito.verify(jwtService, Mockito.never()).generateToken(Mockito.anyString());
    }

    @Test
    void testRegister_EntityCreationException() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("newuser@example.it");
        userRequestDTO.setPassword("password123");

        User user = new User();
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());

        Mockito.when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn("encodedPassword");

        Mockito.when(userMapper.userRequestDTOToUser(userRequestDTO)).thenReturn(user);

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenThrow(new RuntimeException("Generic exception"));

        Assertions.assertThrows(EntityCreationException.class, () -> authService.register(userRequestDTO));

        Mockito.verify(jwtService, Mockito.never()).generateToken(Mockito.anyString());
    }

    @Test
    void testRegister_TokenGenerationError() {
        // 1. Creazione dei dati di input
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("user@example.it");
        userRequestDTO.setPassword("12345678");

        // 2. Creazione dell'oggetto User che useremo nel test
        User user = new User();
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword("encoded-password");
        user.setActive(true);

        Cart cart = new Cart(0, user, new ArrayList<>(), LocalDate.now());
        user.setCart(cart);

        // 3. Mocking delle dipendenze
        Mockito.when(userMapper.userRequestDTOToUser(userRequestDTO)).thenReturn(user);

        // Permettiamo il mock su qualsiasi password
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded-password");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // Simuliamo un errore nella generazione del token
        Mockito.when(jwtService.generateToken(user.getEmail())).thenThrow(new RuntimeException("Error generating token"));

        // 4. Verifica che venga sollevata l'eccezione TokenGenerationException
        Assertions.assertThrows(TokenGenerationException.class, () -> authService.register(userRequestDTO));

        // 5. Verifica che il repository e l'autenticazione siano stati chiamati correttamente
        Mockito.verify(userMapper, Mockito.times(1)).userRequestDTOToUser(userRequestDTO);
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(jwtService, Mockito.times(1)).generateToken(user.getEmail());
    }

}

