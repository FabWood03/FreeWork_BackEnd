package org.elis.progettoing.config;

import org.elis.progettoing.enumeration.Role;
import org.elis.progettoing.models.User;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Custom {@link AuthorizationManager} that handles authorization based on a set of required roles.
 * <p>
 * This class is responsible for checking whether a user has the required role to access a resource.
 * It is used to enforce role-based access control (RBAC) in the application.
 * </p>
 */
@Component
public class RoleManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final Set<Role> requiredRoles;

    /**
     * Constructs a {@link RoleManager} with a set of required roles for authorization.
     *
     * @param requiredRoles the set of roles that are required to access a resource
     */
    public RoleManager(Set<Role> requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    /**
     * Checks if the authenticated user has one of the required roles.
     *
     * This method verifies whether the user is authenticated and whether their role matches any of the
     * required roles for accessing the resource.
     *
     * @param authentication a {@link Supplier} that provides the current {@link Authentication}
     * @param object the current {@link RequestAuthorizationContext} (not used in this method)
     * @return an {@link AuthorizationDecision} that indicates whether the user has the required role
     */
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        // Check if the authentication object is null or the user is not authenticated
        if (authentication == null || authentication.get() == null || !authentication.get().isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        // Get the principal (the authenticated user) from the authentication
        Object principal = authentication.get().getPrincipal();

        // Ensure the principal is of type User
        if (!(principal instanceof User user)) {
            return new AuthorizationDecision(false);
        }

        // Check if the user has a role
        if (user.getRole() == null) {
            return new AuthorizationDecision(false);
        }

        // Check if the user's role matches any of the required roles
        boolean hasRole = requiredRoles.contains(user.getRole());
        return new AuthorizationDecision(hasRole);
    }

    /**
     * Returns the set of roles that are required for authorization.
     *
     * @return the set of required roles
     */
    public Set<Role> getRequiredRoles() {
        return requiredRoles;
    }
}
