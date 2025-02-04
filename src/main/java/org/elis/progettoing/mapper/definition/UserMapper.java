package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.request.user.UserRequestDTO;
import org.elis.progettoing.dto.response.user.UserResponseDTO;
import org.elis.progettoing.models.User;

import java.util.List;

/**
 * Interface for mapping between User entities, UserRequestDTOs, and UserResponseDTOs.
 * This interface defines methods for converting between User request DTOs and response DTOs,
 * as well as mapping User entities to their corresponding DTO representations.
 */
public interface UserMapper {

    /**
     * Converts a UserRequestDTO to a User entity.
     *
     * @param userRequestDTO the UserRequestDTO to be converted
     * @return the User entity populated with data from the UserRequestDTO
     */
    User userRequestDTOToUser(UserRequestDTO userRequestDTO);

    /**
     * Converts a User entity to a UserResponseDTO.
     *
     * @param user the User entity to be converted
     * @return the UserResponseDTO populated with data from the User entity
     */
    UserResponseDTO userToUserResponseDTO(User user);

    /**
     * Converts a list of User entities to a list of UserResponseDTOs.
     *
     * @param users the list of User entities to be converted
     * @return the list of UserResponseDTOs populated with data from the User entities
     */
    List<UserResponseDTO> usersToUserDTOs(List<User> users);
}
