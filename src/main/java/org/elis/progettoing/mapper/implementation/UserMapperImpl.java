package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.user.UserRequestDTO;
import org.elis.progettoing.dto.response.user.UserResponseDTO;
import org.elis.progettoing.enumeration.Role;
import org.elis.progettoing.mapper.definition.UserMapper;
import org.elis.progettoing.models.Cart;
import org.elis.progettoing.models.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the UserMapper interface, which provides methods to convert
 * between User-related request and response DTOs and their corresponding entity models.
 */
@Component
public class UserMapperImpl implements UserMapper {

    /**
     * Converts a UserRequestDTO to a User entity.
     *
     * @param userRequestDTO the DTO containing user data to be converted
     * @return a User entity populated with data from the DTO, or null if the DTO is null
     */
    @Override
    public User userRequestDTOToUser(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null) {
            return null;
        }

        User user = new User();

        user.setCart(userRequestDTOToCart(userRequestDTO));
        user.setName(userRequestDTO.getName());
        user.setSurname(userRequestDTO.getSurname());
        user.setNickname(userRequestDTO.getNickname());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
        if (userRequestDTO.getRole() != null) {
            user.setRole(Enum.valueOf(Role.class, userRequestDTO.getRole()));
        }
        user.setBirthDate(userRequestDTO.getBirthDate());
        user.setEducation(userRequestDTO.getEducation());
        user.setActive(userRequestDTO.isActive());
        user.setRanking(userRequestDTO.getRanking());
        user.setFiscalCode(userRequestDTO.getFiscalCode());

        return user;
    }

    /**
     * Converts a User entity to a UserResponseDTO.
     *
     * @param user the User entity to be converted
     * @return a UserResponseDTO populated with data from the User entity, or null if the entity is null
     */
    @Override
    public UserResponseDTO userToUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setImageFolderUrl(user.getUrlUserPhoto());
        userResponseDTO.setCartId(userCartId(user));
        userResponseDTO.setId(user.getId());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setSurname(user.getSurname());
        userResponseDTO.setNickname(user.getNickname());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setBirthDate(user.getBirthDate());
        userResponseDTO.setFiscalCode(user.getFiscalCode());
        if (user.getRole() != null) {
            userResponseDTO.setRole(user.getRole().name());
        }
        userResponseDTO.setActive(user.isActive());
        userResponseDTO.setRating(user.getRanking());
        userResponseDTO.setPortfolio(user.getPortfolio());
        userResponseDTO.setEducation(user.getEducation());
        userResponseDTO.setSkills(user.getSkills());
        userResponseDTO.setBio(user.getBio());
        userResponseDTO.setBasedIn(user.getBasedIn());
        userResponseDTO.setLanguages(user.getLanguages());

        return userResponseDTO;
    }

    /**
     * Converts a list of User entities to a list of UserResponseDTOs.
     *
     * @param users the list of User entities to be converted
     * @return a list of UserResponseDTOs, or an empty list if the input list is null
     */
    @Override
    public List<UserResponseDTO> usersToUserDTOs(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }

        List<UserResponseDTO> list = new ArrayList<>(users.size());
        for (User user : users) {
            list.add(userToUserResponseDTO(user));
        }

        return list;
    }

    /**
     * Converts a UserRequestDTO to a Cart entity.
     *
     * @param userRequestDTO the DTO containing cart data to be converted
     * @return a Cart entity populated with data from the DTO, or null if the DTO is null
     */
    public Cart userRequestDTOToCart(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null) {
            return null;
        }

        Cart cart = new Cart();

        cart.setId(userRequestDTO.getCartId());

        return cart;
    }

    /**
     * Retrieves the cart ID associated with a User entity.
     *
     * @param user the User entity
     * @return the cart ID if the cart exists, or 0 if the cart or user is null
     */
    public long userCartId(User user) {
        if (user == null) {
            return 0L;
        }
        Cart cart = user.getCart();
        if (cart == null) {
            return 0L;
        }
        return cart.getId();
    }
}
