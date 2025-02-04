package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.request.user.UserEditRequest;
import org.elis.progettoing.dto.response.user.UserResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface for the UserService class. Provides methods for retrieving, updating, and deleting users.
 */
public interface UserService {
    List<UserResponseDTO> findAll();

    UserResponseDTO findById(long id);

    UserResponseDTO findByEmail(String email);

    UserResponseDTO enableById(long id);

    UserResponseDTO disableById(long id);

    boolean remove(long id);

    UserResponseDTO update(UserEditRequest userRequestDTO, MultipartFile userPhoto);

    UserResponseDTO updateUserRole(long id, String newRole);

    List<UserResponseDTO> getAllUserFiltered(String search);
}
