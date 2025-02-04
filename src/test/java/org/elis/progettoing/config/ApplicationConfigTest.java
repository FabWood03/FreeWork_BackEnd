package org.elis.progettoing.config;

import org.elis.progettoing.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Test
    void testUserDetailsService_ThrowsUsernameNotFoundException_WhenUserNotFound() {
        // Mock the behavior of userRepository to return an empty Optional for non-existing user
        Mockito.when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();

        // Assert that UsernameNotFoundException is thrown when trying to load a non-existent user
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent@example.com"));
    }

    @Test
    void testAuthenticationProvider() throws NoSuchFieldException, IllegalAccessException {
        // Test the authentication provider
        AuthenticationProvider authenticationProvider = applicationConfig.authenticationProvider();
        Assertions.assertNotNull(authenticationProvider);

        DaoAuthenticationProvider daoAuthenticationProvider = (DaoAuthenticationProvider) authenticationProvider;

        // Access the private fields of DaoAuthenticationProvider
        Field userDetailsServiceField = DaoAuthenticationProvider.class.getDeclaredField("userDetailsService");
        userDetailsServiceField.setAccessible(true);
        UserDetailsService userDetailsService = (UserDetailsService) userDetailsServiceField.get(daoAuthenticationProvider);

        Field passwordEncoderField = DaoAuthenticationProvider.class.getDeclaredField("passwordEncoder");
        passwordEncoderField.setAccessible(true);
        passwordEncoder = (PasswordEncoder) passwordEncoderField.get(daoAuthenticationProvider);

        // Assertions to verify the correct types
        Assertions.assertInstanceOf(UserDetailsService.class, userDetailsService);
        Assertions.assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
    }

    @Test
    void testAuthenticationManager() throws Exception {
        // Mock and verify authentication manager
        AuthenticationManager mockAuthManager = Mockito.mock(AuthenticationManager.class);
        Mockito.when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthManager);

        authenticationManager = applicationConfig.authenticationManager(authenticationConfiguration);
        Assertions.assertNotNull(authenticationManager);
    }

    @Test
    void testPasswordEncoder() {
        // Verify that the password encoder is properly initialized
        PasswordEncoder encoder = applicationConfig.passwordEncoder();
        Assertions.assertNotNull(encoder);
        Assertions.assertInstanceOf(BCryptPasswordEncoder.class, encoder);
    }
}
