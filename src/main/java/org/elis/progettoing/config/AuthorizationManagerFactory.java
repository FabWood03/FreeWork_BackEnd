package org.elis.progettoing.config;

import org.elis.progettoing.enumeration.Role;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Factory for creating an {@link AuthorizationManager} based on specified roles.
 * <p>
 * This class provides a method to create an {@link AuthorizationManager} that can
 * be used to authorize requests based on the roles provided. It maps the provided
 * roles to a {@link RoleManager}, which handles authorization logic.
 * </p>
 */
@Component
public class AuthorizationManagerFactory {

    /**
     * Creates an {@link AuthorizationManager} that is responsible for authorizing
     * requests based on the specified roles.
     * <p>
     * The created {@link AuthorizationManager} will use the given roles to perform
     * authorization checks during request processing.
     * </p>
     *
     * @param roles the roles to be used for authorization checks
     * @return an {@link AuthorizationManager} that authorizes requests based on the given roles
     */
    public AuthorizationManager<RequestAuthorizationContext> create(Role... roles) {
        // Create and return a RoleManager that uses the specified roles for authorization
        return new RoleManager(Set.of(roles));
    }
}
