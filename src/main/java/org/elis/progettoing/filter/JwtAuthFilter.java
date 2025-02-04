package org.elis.progettoing.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.elis.progettoing.security.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * JwtAuthFilter is a custom filter for handling JWT authentication in the application.
 * It checks for the presence of a valid JWT token in the Authorization header of incoming HTTP requests.
 * If the token is valid, it authenticates the user and sets the authentication in the security context.
 * If there is an exception during the authentication process, it is handled by a handler exception resolver.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**
     * Constructs the JwtAuthFilter with necessary dependencies.
     *
     * @param jwtService the service for working with JWT tokens
     * @param userDetailsService the service for loading user details based on username
     * @param handlerExceptionResolver the resolver for handling exceptions during authentication
     */
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    /**
     * Filters incoming requests, checking for a valid JWT token in the Authorization header.
     * If a valid token is found, the user's authentication is set in the security context.
     *
     * @param request the HTTP request to be filtered
     * @param response the HTTP response
     * @param filterChain the filter chain to pass the request through if no errors occur
     * @throws IOException if an input or output error occurs during filtering
     * @throws ServletException if a servlet-related error occurs during filtering
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");

        // If Authorization header is missing or does not start with "Bearer ", pass the request to the next filter
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authorization.substring("Bearer ".length());
            String email = jwtService.extractEmail(token);

            // Check if the email is valid and the user is not already authenticated
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if(!userDetails.isEnabled()){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // Validate JWT token and set the user in the security context if valid
                if (Boolean.TRUE.equals(jwtService.validateToken(token, userDetails))) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Handle exceptions via the handler exception resolver
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }

        // Pass the request to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
