package org.elis.progettoing.config;

import org.elis.progettoing.enumeration.Role;
import org.elis.progettoing.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Objects;
import java.util.Set;

class RoleManagerTest {

    @Test
    void testRoleManager_withRequiredRoles() {
        // Create a RoleManager with required roles
        RoleManager roleManager = new RoleManager(Set.of(Role.ADMIN, Role.BUYER));

        // Verify that the required roles are correct
        Set<Role> requiredRoles = roleManager.getRequiredRoles();
        Assertions.assertTrue(requiredRoles.contains(Role.ADMIN));
        Assertions.assertTrue(requiredRoles.contains(Role.BUYER));
    }

    @Test
    void testCheck_withValidUserAndRole() {
        // Create a RoleManager with a required role
        RoleManager roleManager = new RoleManager(Set.of(Role.ADMIN));

        // Mock the authenticated user
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);

        User mockUser = Mockito.mock(User.class);
        Mockito.when(mockUser.getRole()).thenReturn(Role.ADMIN);
        Mockito.when(authentication.getPrincipal()).thenReturn(mockUser);

        // Verify that the user has the required role
        AuthorizationDecision decision = roleManager.check(() -> authentication, null);
        Assertions.assertTrue(Objects.requireNonNull(decision).isGranted());
    }

    @Test
    void testCheck_withInvalidRole() {
        // Create a RoleManager with a required role
        RoleManager roleManager = new RoleManager(Set.of(Role.ADMIN));

        // Mock the authenticated user
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);

        User mockUser = Mockito.mock(User.class);
        Mockito.when(mockUser.getRole()).thenReturn(Role.BUYER); // Invalid role
        Mockito.when(authentication.getPrincipal()).thenReturn(mockUser);

        // Verify that the user does not have the required role
        AuthorizationDecision decision = roleManager.check(() -> authentication, null);
        Assertions.assertFalse(Objects.requireNonNull(decision).isGranted());
    }

    @Test
    void testCheck_withUnauthenticatedUser() {
        // Create a RoleManager with a required role
        RoleManager roleManager = new RoleManager(Set.of(Role.ADMIN));

        // Mock the unauthenticated user
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(false);

        // Verify that access is denied
        AuthorizationDecision decision = roleManager.check(() -> authentication, null);
        Assertions.assertFalse(Objects.requireNonNull(decision).isGranted());
    }

    @Test
    void testCheck_RoleNull() {
        // Create a RoleManager with a required role
        RoleManager roleManager = new RoleManager(Set.of(Role.ADMIN));

        // Mock the authenticated user
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);

        User mockUser = Mockito.mock(User.class);
        Mockito.when(mockUser.getRole()).thenReturn(null); // Null role
        Mockito.when(authentication.getPrincipal()).thenReturn(mockUser);

        // Verify that access is denied
        AuthorizationDecision decision = roleManager.check(() -> authentication, null);
        Assertions.assertFalse(Objects.requireNonNull(decision).isGranted());
    }

    @Test
    void testCheck_whenAuthenticationIsNull() {
        Set<Role> requiredRoles = Set.of(Role.ADMIN);
        RoleManager roleManager = new RoleManager(requiredRoles);
        RequestAuthorizationContext context = Mockito.mock(RequestAuthorizationContext.class);

        // Simulate authentication == null
        AuthorizationDecision decision = roleManager.check(() -> null, context);

        // The decision should be false because authentication is null
        Assertions.assertFalse(Objects.requireNonNull(decision).isGranted(), "Should not be granted when authentication is null");
    }

    @Test
    void testCheck_whenAuthenticationGetIsNull() {
        Set<Role> requiredRoles = Set.of(Role.ADMIN);
        RoleManager roleManager = new RoleManager(requiredRoles);
        RequestAuthorizationContext context = Mockito.mock(RequestAuthorizationContext.class);

        // Simulate authentication.get() == null
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(null);  // Principal is null

        AuthorizationDecision decision = roleManager.check(() -> authentication, context);

        // The decision should be false because authentication.get() is null
        Assertions.assertFalse(Objects.requireNonNull(decision).isGranted(), "Should not be granted when authentication.get() is null");
    }

    @Test
    void testCheck_whenAuthenticationIsNotAuthenticated() {
        Set<Role> requiredRoles = Set.of(Role.ADMIN);
        RoleManager roleManager = new RoleManager(requiredRoles);
        RequestAuthorizationContext context = Mockito.mock(RequestAuthorizationContext.class);

        // Simulate authentication.isAuthenticated() == false
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(false);  // Not authenticated

        AuthorizationDecision decision = roleManager.check(() -> authentication, context);

        // The decision should be false because the authentication is not valid
        Assertions.assertFalse(Objects.requireNonNull(decision).isGranted(), "Should not be granted when authentication is not authenticated");
    }

    @Test
    void testCheck_whenAuthenticationIsAuthenticated() {
        // Create the RoleManager with a set of required roles
        Set<Role> requiredRoles = Set.of(Role.ADMIN);
        RoleManager roleManager = new RoleManager(requiredRoles);

        // Create a mock authorization context
        RequestAuthorizationContext context = Mockito.mock(RequestAuthorizationContext.class);

        // Simulate an authenticated Authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);  // Simulate valid authentication

        // Simulate the user with a role
        User user = Mockito.mock(User.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(user.getRole()).thenReturn(Role.ADMIN);

        // Simulate the authentication behavior
        AuthorizationDecision decision = roleManager.check(() -> authentication, context);

        // Verify that the decision is true
        Assertions.assertTrue(Objects.requireNonNull(decision).isGranted(), "Should be granted when authentication is authenticated");
    }

    @Test
    void testCheck_whenPrincipalIsNotUser() {
        // Create the RoleManager with a set of required roles
        Set<Role> requiredRoles = Set.of(Role.ADMIN);
        RoleManager roleManager = new RoleManager(requiredRoles);

        // Create a mock authorization context
        RequestAuthorizationContext context = Mockito.mock(RequestAuthorizationContext.class);

        // Simulate an Authentication with a principal that is not a User instance
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getPrincipal()).thenReturn("NotAUser");  // Principal is not a User

        // Simulate the authentication behavior
        AuthorizationDecision decision = roleManager.check(() -> authentication, context);

        // Verify that the decision is false because the principal is not a User instance
        Assertions.assertFalse(Objects.requireNonNull(decision).isGranted(), "Should not be granted when principal is not an instance of User");
    }
}
