package org.elis.progettoing.config;

import org.elis.progettoing.enumeration.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class AuthorizationManagerFactoryTest {

    @Test
    void testCreate_withRoles() {
        // Create an instance of the AuthorizationManagerFactory
        AuthorizationManagerFactory authorizationManagerFactory = new AuthorizationManagerFactory();

        // Define roles to be used in the test
        Role role1 = Role.ADMIN;
        Role role2 = Role.BUYER;

        // Create an AuthorizationManager using the factory
        AuthorizationManager<RequestAuthorizationContext> authorizationManager =
                authorizationManagerFactory.create(role1, role2);

        // Assert that the authorization manager is not null
        Assertions.assertNotNull(authorizationManager);

        // Assert that the authorization manager is an instance of RoleManager
        Assertions.assertInstanceOf(RoleManager.class, authorizationManager);

        // Cast the authorization manager to RoleManager to verify the required roles
        RoleManager roleManager = (RoleManager) authorizationManager;
        Set<Role> roles = roleManager.getRequiredRoles();

        // Assert that the roles contain the expected values
        Assertions.assertTrue(roles.contains(role1));
        Assertions.assertTrue(roles.contains(role2));
    }
}
