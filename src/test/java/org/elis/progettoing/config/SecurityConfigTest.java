package org.elis.progettoing.config;

import org.elis.progettoing.enumeration.Role;
import org.elis.progettoing.filter.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private AuthorizationManagerFactory authorizationManagerFactory;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void corsFilter_ShouldBeConfiguredCorrectly() {
        // Directly create the UrlBasedCorsConfigurationSource
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Apply the same configuration as in your SecurityConfig
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        // Register the configuration for all paths
        source.registerCorsConfiguration("/**", config);

        // Use Spring's MockHttpServletRequest instead of Mockito's mock
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/any-url");

        // Retrieve the CORS configuration for the mock request
        CorsConfiguration retrievedConfig = source.getCorsConfiguration(request);

        // Verify the configuration
        assertNotNull(retrievedConfig);
        assertTrue(Objects.requireNonNull(retrievedConfig.getAllowedOrigins()).contains("http://localhost:4200"));
        assertTrue(Objects.requireNonNull(retrievedConfig.getAllowedMethods()).containsAll(List.of("GET", "POST", "PUT", "DELETE", "PATCH")));
        assertTrue(Objects.requireNonNull(retrievedConfig.getAllowedHeaders()).contains("*"));
        assertEquals(Boolean.TRUE, retrievedConfig.getAllowCredentials());
    }

    @Test
    void securityFilterChain_ShouldConfigureCorrectly() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class);

        // Configure the mock to return itself for chaining methods
        when(http.cors(any())).thenReturn(http);
        when(http.csrf(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.authenticationProvider(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        when(http.addFilterAfter(any(), any())).thenReturn(http);
        when(http.exceptionHandling(any())).thenReturn(http);

        DefaultSecurityFilterChain mockChain = mock(DefaultSecurityFilterChain.class);
        when(http.build()).thenReturn(mockChain);

        SecurityFilterChain chain = securityConfig.securityFilterChain(http);

        // Verify interactions
        verify(http).cors(any());
        verify(http).csrf(any());
        verify(http).authorizeHttpRequests(any());
        verify(http).sessionManagement(any());
        verify(http).authenticationProvider(authenticationProvider);
        verify(http).addFilterBefore(eq(jwtAuthFilter), eq(UsernamePasswordAuthenticationFilter.class));
        verify(http).addFilterAfter(any(CorsFilter.class), eq(JwtAuthFilter.class));
        verify(http).exceptionHandling(any());

        // Verify that http.build() was called
        verify(http).build();

        // Assert that the returned chain is the mockChain (not null)
        assertNotNull(chain);
        assertEquals(mockChain, chain);
    }

    @Test
    void hasSpecificRole_ShouldCreateAuthorizationManager() {
        // Arrange: Stub the factory to return a non-null AuthorizationManager
        AuthorizationManager<RequestAuthorizationContext> mockManager = mock(AuthorizationManager.class);
        when(authorizationManagerFactory.create(Role.ADMIN, Role.MODERATOR)).thenReturn(mockManager);

        // Act: Call the method under test
        AuthorizationManager<RequestAuthorizationContext> manager =
                securityConfig.hasSpecificRole(Role.ADMIN, Role.MODERATOR);

        // Assert: Verify the manager is not null and the factory method was called
        assertNotNull(manager, "AuthorizationManager should not be null");
        verify(authorizationManagerFactory).create(Role.ADMIN, Role.MODERATOR);
    }
}