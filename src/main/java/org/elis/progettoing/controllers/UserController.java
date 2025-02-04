package org.elis.progettoing.controllers;

import jakarta.validation.Valid;
import org.elis.progettoing.dto.request.user.UserEditRequest;
import org.elis.progettoing.dto.response.user.UserResponseDTO;
import org.elis.progettoing.exception.ActiveUserEditException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.service.definition.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for managing user-related operations, such as retrieving, updating,
 * enabling, disabling, and deleting users.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    /**
     * Constructs an instance of {@code UserController}.
     *
     * @param userService the service managing user-related business logic.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to retrieve all users.
     *
     * @return a {@link ResponseEntity} containing a list of {@link UserResponseDTO}
     *         and HTTP status 200 (OK).
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve a user by their ID.
     *
     * @param id the ID of the user.
     * @return a {@link ResponseEntity} containing the {@link UserResponseDTO}
     *         and HTTP status 200 (OK).
     * @throws EntityNotFoundException if the user with the given ID is not found.
     */
    @GetMapping("/getById")
    public ResponseEntity<UserResponseDTO> getUserById(@RequestParam("id") long id) throws EntityNotFoundException {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve a user by their email.
     *
     * @param email the email of the user.
     * @return a {@link ResponseEntity} containing the {@link UserResponseDTO}
     *         and HTTP status 200 (OK).
     * @throws EntityNotFoundException if no user with the given email is found.
     */
    @GetMapping("/getByEmail")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@RequestParam("email") String email) throws EntityNotFoundException {
        return new ResponseEntity<>(userService.findByEmail(email), HttpStatus.OK);
    }

    /**
     * Endpoint to enable a user by their ID.
     *
     * @param userId the ID of the user to enable.
     * @return a {@link ResponseEntity} containing the updated {@link UserResponseDTO}
     *         and HTTP status 200 (OK).
     * @throws EntityNotFoundException if no user with the given ID is found.
     * @throws ActiveUserEditException if the user cannot be enabled due to restrictions.
     */
    @PatchMapping("/enable")
    public ResponseEntity<UserResponseDTO> enableUser(@RequestParam("userId") long userId) throws EntityNotFoundException, ActiveUserEditException {
        return new ResponseEntity<>(userService.enableById(userId), HttpStatus.OK);
    }

    /**
     * Endpoint to disable a user by their ID.
     *
     * @param userId the ID of the user to disable.
     * @return a {@link ResponseEntity} containing the updated {@link UserResponseDTO}
     *         and HTTP status 200 (OK).
     * @throws EntityNotFoundException if no user with the given ID is found.
     * @throws ActiveUserEditException if the user cannot be disabled due to restrictions.
     */
    @PatchMapping("/disable")
    public ResponseEntity<UserResponseDTO> disableUser(@RequestParam("userId") long userId) throws EntityNotFoundException, ActiveUserEditException {
        return new ResponseEntity<>(userService.disableById(userId), HttpStatus.OK);
    }

    /**
     * Endpoint to delete a user by their ID.
     *
     * @param id the ID of the user to delete.
     * @return a {@link ResponseEntity} containing a boolean indicating success or failure
     *         and HTTP status 200 (OK).
     * @throws EntityNotFoundException if no user with the given ID is found.
     * @throws EntityDeletionException if the user cannot be deleted due to some issue.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> removeUser(@RequestParam("id") long id) throws EntityNotFoundException, EntityDeletionException {
        return new ResponseEntity<>(userService.remove(id), HttpStatus.OK);
    }

    /**
     * Endpoint to update user details, including their profile photo.
     *
     * @param userRequestDTO the data to update the user with.
     * @param userPhoto      the profile photo to associate with the user.
     * @return a {@link ResponseEntity} containing the updated {@link UserResponseDTO}
     *         and HTTP status 200 (OK).
     * @throws EntityNotFoundException if no user with the given data is found.
     * @throws ActiveUserEditException if the user cannot be updated due to restrictions.
     */
    @PatchMapping("/update")
    public ResponseEntity<UserResponseDTO> update(@Valid @RequestPart("userData") UserEditRequest userRequestDTO,
                                                  @RequestPart(value = "userPhoto", required = false) MultipartFile userPhoto) throws EntityNotFoundException, ActiveUserEditException {
        return new ResponseEntity<>(userService.update(userRequestDTO, userPhoto), HttpStatus.OK);
    }

    /**
     * Endpoint per aggiornare il ruolo di un utente.
     *
     * @param id      ID dell'utente.
     * @param newRole Nuovo ruolo da assegnare all'utente.
     * @return {@link ResponseEntity} con il {@link UserResponseDTO} aggiornato.
     * @throws EntityNotFoundException se l'utente con l'ID fornito non esiste.
     */
    @PatchMapping("/updateRole")
    public ResponseEntity<UserResponseDTO> updateUserRole(@RequestParam("id") long id, @RequestParam("role") String newRole) throws EntityNotFoundException {
        UserResponseDTO updatedUser = userService.updateUserRole(id, newRole);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve all users whose name, surname, or nickname contains the search term.
     *
     * @param search the search term to filter users by.
     * @return a {@link ResponseEntity} containing a list of {@link UserResponseDTO}
     *         and HTTP status 200 (OK).
     */
    @PostMapping("/getUsersFiltered")
    public ResponseEntity<List<UserResponseDTO>> getUsersFiltered(@RequestParam("search") String search) {
        return new ResponseEntity<>(userService.getAllUserFiltered(search), HttpStatus.OK);
    }
}
