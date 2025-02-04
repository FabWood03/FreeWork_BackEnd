package org.elis.progettoing.service.implementation;

import org.elis.progettoing.dto.TokenDTO;
import org.elis.progettoing.dto.request.user.UserLoginRequest;
import org.elis.progettoing.dto.request.user.UserRequestDTO;
import org.elis.progettoing.dto.response.user.UserResponseDTO;
import org.elis.progettoing.enumeration.Role;
import org.elis.progettoing.exception.InvalidCredentialsException;
import org.elis.progettoing.exception.TokenGenerationException;
import org.elis.progettoing.exception.UserNotActiveException;
import org.elis.progettoing.exception.entity.EntityAlreadyExistsException;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.mapper.definition.UserMapper;
import org.elis.progettoing.models.Cart;
import org.elis.progettoing.models.User;
import org.elis.progettoing.repository.UserRepository;
import org.elis.progettoing.security.JwtService;
import org.elis.progettoing.service.definition.AuthService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Implementation of {@link AuthService} for handling user authentication and registration.
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    /**
     * Constructor of {@link AuthServiceImpl}.
     *
     * @param passwordEncoder Password encoder.
     * @param userRepository Repository of users.
     * @param authenticationManager Authentication manager.
     * @param jwtService Service for managing JWT tokens.
     * @param userMapper Mapper for conversion between user objects and DTOs.
     */
    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository,
                           AuthenticationManager authenticationManager, JwtService jwtService, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    /**
     * Register a new user with administrator role.
     *
     * @param userRequestDTO Data of the user to be registered.
     * @return {@link UserResponseDTO} with details of the registered user.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenDTO registerAdmin(UserRequestDTO userRequestDTO) {
        userRequestDTO.setRole(Role.ADMIN.name());
        return register(userRequestDTO);
    }

    /**
     * Register a new user with moderator role.
     *
     * @param userRequestDTO Data of the user to be registered.
     * @return {@link UserResponseDTO} with details of the registered user.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenDTO registerModerator(UserRequestDTO userRequestDTO) {
        userRequestDTO.setRole(Role.MODERATOR.name());
        return register(userRequestDTO);
    }

    /**
     * Register a new user with buyer role.
     *
     * @param userRequestDTO Data of the user to be registered.
     * @return {@link UserResponseDTO} with details of the registered user.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenDTO registerBuyer(UserRequestDTO userRequestDTO) {
        userRequestDTO.setRole(Role.BUYER.name());
        return register(userRequestDTO);
    }

    /**
     * Register a new user with the specified role and create an associated empty cart.
     *
     * @param userRequestDTO Data of the user to be registered.
     * @return {@link UserResponseDTO} with details of the registered user.
     * @throws EntityAlreadyExistsException if the email is already associated with another account.
     * @throws EntityCreationException if an error occurs during user creation.
     * @throws TokenGenerationException if an error occurs during JWT token generation.
     */
    @Override
    public TokenDTO register(UserRequestDTO userRequestDTO) {
        User user = userMapper.userRequestDTOToUser(userRequestDTO);
        String token;
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Cart cart = new Cart(0, user, new ArrayList<>(), LocalDate.now());
        user.setCart(cart);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistsException("utente", "email", user.getEmail());
        } catch (Exception e) {
            throw new EntityCreationException("utente", "email", user.getEmail());
        }

        try {
            token = jwtService.generateToken(user.getEmail());
        } catch (Exception e) {
            throw new TokenGenerationException("Errore durante la generazione del token JWT per l'utente con email: " + user.getEmail());
        }

        return new TokenDTO(token);
    }

    /**
     * Authenticate the user with provided email and password and generate a JWT token.
     *
     * @param userLoginRequest User login data.
     * @return {@link TokenDTO} with the generated token.
     * @throws EntityNotFoundException if email is not associated with any user.
     * @throws InvalidCredentialsException if the password is incorrect.
     * @throws UserNotActiveException if the user is not active.
     * @throws TokenGenerationException if an error occurs during JWT token generation.
     */
    @Override
    public TokenDTO login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmail(userLoginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("utente", "email", userLoginRequest.getEmail()));

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Credenziali errate");
        }

        if (!user.isActive()) {
            throw new UserNotActiveException("L'utente con l'email " + user.getEmail() + " non è attivo.");
        }

        String token;

        try {
            token = jwtService.generateToken(user.getEmail());
        } catch (Exception e) {
            throw new TokenGenerationException("Errore durante la generazione del token JWT per l'utente con email: " + user.getEmail());
        }

        return new TokenDTO(token);
    }
}
