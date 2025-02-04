package org.elis.progettoing.controllers;

import jakarta.validation.Valid;
import org.elis.progettoing.dto.TokenDTO;
import org.elis.progettoing.dto.request.user.UserLoginRequest;
import org.elis.progettoing.dto.request.user.UserRequestDTO;
import org.elis.progettoing.exception.UserNotActiveException;
import org.elis.progettoing.exception.entity.EntityAlreadyExistsException;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.exception.entity.InvalidEntityDataException;
import org.elis.progettoing.service.definition.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing user authentication and registration operations.
 * Provides endpoints for user login and registration with different roles.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * Constructs an instance of {@code AuthController}.
     *
     * @param authService the authentication service that handles the business logic for login and registration.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint to register a new user with the role of administrator.
     *
     * @param userRequestDTO the user data for registration as an administrator.
     * @return a {@link ResponseEntity} containing the {@link TokenDTO} of the created user and HTTP status 201 (Created).
     * @throws EntityAlreadyExistsException if a user with the same email already exists.
     * @throws EntityCreationException      if an error occurs during user creation.
     */
    @PostMapping("/registerAdmin")
    public ResponseEntity<TokenDTO> registerAdmin(@Valid @RequestBody UserRequestDTO userRequestDTO) throws EntityAlreadyExistsException, EntityCreationException {
        return new ResponseEntity<>(authService.registerAdmin(userRequestDTO), HttpStatus.CREATED);
    }

    /**
     * Endpoint to register a new user with the role of buyer.
     *
     * @param userRequestDTO the user data for registration as a buyer.
     * @return a {@link ResponseEntity} containing the {@link TokenDTO} of the created user and HTTP status 201 (Created).
     * @throws EntityAlreadyExistsException if a user with the same email already exists.
     * @throws EntityCreationException      if an error occurs during user creation.
     */
    @PostMapping("/registerBuyer")
    public ResponseEntity<TokenDTO> registerBuyer(@Valid @RequestBody UserRequestDTO userRequestDTO) throws EntityAlreadyExistsException, EntityCreationException {
        return new ResponseEntity<>(authService.registerBuyer(userRequestDTO), HttpStatus.CREATED);
    }

    /**
     * Endpoint to register a new user with the role of moderator.
     *
     * @param userRequestDTO the user data for registration as a moderator.
     * @return a {@link ResponseEntity} containing the {@link TokenDTO} of the created user and HTTP status 201 (Created).
     * @throws EntityAlreadyExistsException if a user with the same email already exists.
     * @throws EntityCreationException      if an error occurs during user creation.
     */
    @PostMapping("/registerModerator")
    public ResponseEntity<TokenDTO> registerModerator(@Valid @RequestBody UserRequestDTO userRequestDTO) throws EntityAlreadyExistsException, EntityCreationException {
        return new ResponseEntity<>(authService.registerModerator(userRequestDTO), HttpStatus.CREATED);
    }

    /**
     * Endpoint to log in a user.
     *
     * @param userLoginRequest the user data required for login.
     * @return a {@link ResponseEntity} containing the {@link TokenDTO} with the generated authentication token and HTTP status 200 (OK).
     * @throws InvalidEntityDataException if the provided credentials are incorrect or invalid.
     * @throws UserNotActiveException     if the user is not active.
     * @throws EntityNotFoundException    if a user with the specified email does not exist.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody UserLoginRequest userLoginRequest) throws InvalidEntityDataException, UserNotActiveException, EntityNotFoundException {
        return new ResponseEntity<>(authService.login(userLoginRequest), HttpStatus.OK);
    }
}
