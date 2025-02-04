package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.TokenDTO;
import org.elis.progettoing.dto.request.user.UserLoginRequest;
import org.elis.progettoing.dto.request.user.UserRequestDTO;

/**
 * Interface for the AuthService class. Provides methods for registering and logging in users.
 */
public interface AuthService {
    TokenDTO registerAdmin(UserRequestDTO userRequestDTO);

    TokenDTO registerModerator(UserRequestDTO userRequestDTO);

    TokenDTO registerBuyer(UserRequestDTO userRequestDTO);

    TokenDTO register(UserRequestDTO userRequestDTO);

    TokenDTO login(UserLoginRequest userLoginRequest);
}
