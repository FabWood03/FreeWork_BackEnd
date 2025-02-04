package org.elis.progettoing.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.elis.progettoing.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    // Set up the test data before each test
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSkipFilterIfAuthorizationHeaderIsMissing() throws ServletException, IOException {
        // Mocking the behavior of the methods
        when(request.getHeader("Authorization")).thenReturn(null);

        // Invoking the method under test
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Verifying the interactions
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void shouldSkipFilterIfAuthorizationHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        // Mocking the behavior of the methods
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // Invoking the method under test
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Verifying the interactions
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void shouldSetAuthenticationIfJwtIsValidAndUserIsEnabled() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String email = "user@example.com";

        // Mocking the behavior of the methods
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(userDetails.isEnabled()).thenReturn(true);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);

        // Invoking the method under test
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assertions
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, SecurityContextHolder.getContext().getAuthentication());

        // Verifying the interactions
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReturnUnauthorizedIfUserIsDisabled() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String email = "user@example.com";

        // Mocking the behavior of the methods
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(userDetails.isEnabled()).thenReturn(false);

        // Invoking the method under test
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Verifying the interactions
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verifyNoInteractions(filterChain);
    }

    @Test
    void shouldHandleExceptionDuringJwtProcessing() throws ServletException, IOException {
        String token = "invalid.jwt.token";

        // Mocking the behavior of the methods
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractEmail(token)).thenThrow(new RuntimeException("Invalid Token"));

        // Invoking the method under test
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Verifying the interactions
        verify(handlerExceptionResolver).resolveException(eq(request), eq(response), isNull(), any(RuntimeException.class));
        verifyNoInteractions(filterChain);
    }

    @Test
    void shouldNotSetAuthenticationIfJwtIsInvalid() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        String email = "user@example.com";

        // Mocking the behavior of the methods
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(userDetails.isEnabled()).thenReturn(true);
        when(jwtService.validateToken(token, userDetails)).thenReturn(false);

        // Invoking the method under test
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assertions
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verifying the interactions
        verify(filterChain).doFilter(request, response);
    }
}