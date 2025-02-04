package org.elis.progettoing.service.implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
import org.elis.progettoing.service.definition.UserService;
import org.hibernate.Hibernate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String USER_NAME = "utente";
    private final LocalStorageService localStorageService;
    private final TicketRepository ticketRepository;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    private final EntityManager entityManager;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, LocalStorageService localStorageService, TicketRepository ticketRepository, ReviewRepository reviewRepository, ProductRepository productRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.localStorageService = localStorageService;
        this.ticketRepository = ticketRepository;
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.entityManager = entityManager;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponseDTO update(UserEditRequest userRequestDTO, MultipartFile userPhoto) {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User userToUpdate = (User) authentication.getPrincipal();

        Optional<User> existingUser = userRepository.findById(userToUpdate.getId());
        if (existingUser.isEmpty()) {
            throw new EntityNotFoundException(USER_NAME, "ID", userToUpdate.getId());
        }

        userToUpdate = existingUser.get();
        userToUpdate.setName(userRequestDTO.getName());
        userToUpdate.setSurname(userRequestDTO.getSurname());
        userToUpdate.setBirthDate(userRequestDTO.getBirthDate());
        userToUpdate.setNickname(userRequestDTO.getNickname());
        userToUpdate.setEducation(userRequestDTO.getEducation());
        userToUpdate.setSkills(userRequestDTO.getSkills());
        userToUpdate.setBio(userRequestDTO.getBio());
        userToUpdate.setLanguages(userRequestDTO.getLanguages());

        // Aggiorna la foto profilo
        if (userPhoto != null && !userPhoto.isEmpty()) {
            String newPhotoPath = localStorageService.updateUserProfilePhoto(userPhoto, userToUpdate.getUrlUserPhoto());
            userToUpdate.setUrlUserPhoto(newPhotoPath);
        }

        try {
            userRepository.save(userToUpdate);
        } catch (Exception e) {
            throw new EntityEditException(USER_NAME, "ID", userToUpdate.getId());
        }

        return userMapper.userToUserResponseDTO(userToUpdate);
    }

    @Override
    public UserResponseDTO updateUserRole(long id, String newRole) {
        // Retrieve the authenticated user
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User userAuthenticated = (User) authentication.getPrincipal();

        // Retrieve the user to update
        User userToUpdateRole = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NAME, "ID", id));

        // Check if the authenticated user has a lower role than the user to update
        if (userAuthenticated.getRole().ordinal() <= userToUpdateRole.getRole().ordinal() || userAuthenticated.getId() == userToUpdateRole.getId()) {
            throw new ActiveUserEditException("aggiornare il ruolo", "ID", id);
        }

        // Update the user role
        Role role = Role.valueOf(newRole.toUpperCase());
        userToUpdateRole.setRole(role);

        try {
            userRepository.save(userToUpdateRole);
        } catch (Exception e) {
            throw new EntityEditException(USER_NAME, "ID", id);
        }

        return userMapper.userToUserResponseDTO(userToUpdateRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(long userId) {
        // Retrieve the authenticated user
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User userAuthenticated = (User) authentication.getPrincipal();

        // Retrive and check if the user to remove exists
        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NAME, "ID", userId));

        // Check if the user to remove is the same as the authenticated user or if the authenticated user has a lower role
        if ((userAuthenticated.getRole() == Role.BUYER || userAuthenticated.getRole() == Role.SELLER) && userAuthenticated.getId() != userToRemove.getId()) {
            throw new ActiveUserEditException("eliminare", "ID", userId);
        }

        // Check if the user to remove has a higher role than the authenticated user
        if (userAuthenticated.getRole().ordinal() <= userToRemove.getRole().ordinal()) {
            throw new ActiveUserEditException("eliminare", "ID", userId);
        }

        // Delete the user photo if it exists
        if (userAuthenticated.getUrlUserPhoto() != null) {
            localStorageService.deleteImage(userToRemove.getUrlUserPhoto());
        }

        // Unset the user from all tickets, reviews and products
        ticketRepository.unsetReportedUser(userId);
        ticketRepository.unsetRequesterId(userId);
        reviewRepository.unsetUser(userId);
        productRepository.unsetUser(userId);

        try {
            userRepository.delete(userToRemove);
        } catch (Exception e) {
            throw new EntityDeletionException(USER_NAME, "ID", userId);
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        // Retrieve all users
        List<User> users = userRepository.findAll();

        return userMapper.usersToUserDTOs(users);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findById(long userId) {
        // Retrieve the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NAME, "ID", userId));

        Hibernate.initialize(user.getLanguages());
        Hibernate.initialize(user.getBasedIn());

        return userMapper.userToUserResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        // Retrieve the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(USER_NAME, "email", email));

        return userMapper.userToUserResponseDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponseDTO enableById(long userId) {
        // Retrieve the authenticated user
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User userAuthenticated = (User) authentication.getPrincipal();

        // Check if the user to enable exists
        User userToEnable = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NAME, "ID", userId));
        // Check if the user to enable is the same as the authenticated user or if the authenticated user has a lower role
        if (userAuthenticated.getId() == userToEnable.getId() || userAuthenticated.getRole().ordinal() <= userToEnable.getRole().ordinal()) {
            throw new ActiveUserEditException("attivare", "ID", userId);
        }

        // Enable the user
        userToEnable.setActive(true);

        try {
            userRepository.save(userToEnable);
        } catch (Exception e) {
            throw new ActiveUserEditException("attivare", "ID", userId);
        }

        return userMapper.userToUserResponseDTO(userToEnable);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponseDTO disableById(long userId) {
        // Retrieve the authenticated user
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User userAuthenticated = (User) authentication.getPrincipal();

        // Verify that the user to disable exists
        User userToDisable = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NAME, "ID", userId));

        //  Check if the user to disable is the same as the authenticated user or if the authenticated user has a lower role
        if (userAuthenticated.getId() == userToDisable.getId() || userAuthenticated.getRole().ordinal() <= userToDisable.getRole().ordinal()) {
            throw new ActiveUserEditException("disattivare", "ID", userId);
        }

        // Disable the user
        userToDisable.setActive(false);

        try {
            userRepository.save(userToDisable);
        } catch (Exception e) {
            throw new ActiveUserEditException("disattivare", "ID", userId);
        }

        return userMapper.userToUserResponseDTO(userToDisable);
    }

    @Override
    public List<UserResponseDTO> getAllUserFiltered(String search) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        // Costruzione delle condizioni di filtro
        List<Predicate> predicates = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            String searchPattern = "%" + search.toLowerCase() + "%";

            predicates.add(cb.like(cb.lower(root.get("name")), searchPattern));
            predicates.add(cb.like(cb.lower(root.get("surname")), searchPattern));
            predicates.add(cb.like(cb.lower(root.get("nickname")), searchPattern));

            // Combinazione dei predicati con OR
            query.select(root).where(cb.or(predicates.toArray(new Predicate[0])));
        } else {
            // Se il filtro è vuoto, ritorna la lista completa degli utenti
            query.select(root);
        }

        // Esecuzione della query
        List<User> users = entityManager.createQuery(query).getResultList();

        return userMapper.usersToUserDTOs(users);
    }
}
