package org.elis.progettoing.config;

import org.elis.progettoing.enumeration.Role;
import org.elis.progettoing.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Security configuration class for the application.
 * <p>
 * This configuration sets up security features including JWT authentication, role-based access control, CORS handling,
 * and custom exception handling.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final AuthorizationManagerFactory authorizationManager;

    // Constants for roles
    private static final String ADMIN = "ADMIN";
    private static final String MODERATOR = "MODERATOR";
    private static final String SELLER = "SELLER";
    private static final String BUYER = "BUYER";

    /**
     * Constructor that initializes the security configuration with the required beans.
     *
     * @param jwtAuthFilter the JWT authentication filter used to process incoming requests.
     * @param authenticationProvider the authentication provider used for user authentication.
     * @param handlerExceptionResolver the handler for exceptions thrown during security processing.
     * @param authorizationManagerFactory the factory used to create custom authorization managers.
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, AuthenticationProvider authenticationProvider, HandlerExceptionResolver handlerExceptionResolver, AuthorizationManagerFactory authorizationManagerFactory) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.authorizationManager = authorizationManagerFactory;
    }

    /**
     * Defines the role hierarchy for the application.
     * <p>
     * Roles are arranged in a hierarchy, allowing higher-level roles to inherit permissions from lower-level roles.
     * </p>
     *
     * @return the role hierarchy defining the relationship between roles.
     */
    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
                """
                 ROLE_ADMIN > ROLE_MODERATOR
                 ROLE_MODERATOR > ROLE_SELLER
                 ROLE_SELLER > ROLE_BUYER
                """
        );
    }

    /**
     * Creates a {@link MethodSecurityExpressionHandler} with the defined role hierarchy.
     * This handler is used for method-level security and role-based access control.
     *
     * @return the security expression handler used for method security.
     */
    @Bean
    static MethodSecurityExpressionHandler createSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    /**
     * Configures the HTTP security settings for the application.
     * <p>
     * This method defines which endpoints require which roles for access and sets up JWT authentication, CORS handling,
     * session management, and exception handling.
     * </p>
     *
     * @param http the {@link HttpSecurity} object used to configure the HTTP security settings.
     * @return the configured {@link SecurityFilterChain}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        // Public requests for static resources
                        .requestMatchers("/images/**").permitAll()

                        // Auth requests
                        .requestMatchers(POST, "/api/auth/registerAdmin", "/api/auth/registerModerator").hasRole(ADMIN)
                        .requestMatchers(POST, "/api/auth/registerBuyer", "/api/auth/login").permitAll()

                        // Ticket requests
                        .requestMatchers(GET, "/api/ticket/getTicketById", "/api/ticket/getAll", "/api/ticket/getResolvedTickets", "/api/ticket/getPendingTickets", "/api/ticket/getTakeOnTickets").hasRole(MODERATOR)
                        .requestMatchers(POST, "/api/ticket/sellerRequest").access(hasSpecificRole(Role.BUYER))
                        .requestMatchers(POST, "/api/ticket/reportReviews", "/api/ticket/reportUser", "/api/ticket/reportProduct").access(hasSpecificRole(Role.BUYER, Role.SELLER))
                        .requestMatchers(POST, "/api/ticket/refuseTicket", "/api/ticket/acceptTicket", "/api/ticket/takeOnTicket", "/api/ticket/filter").hasRole(MODERATOR)

                        // User requests
                        .requestMatchers(GET, "/api/user/getById", "/api/user/getByEmail").hasRole(BUYER)
                        .requestMatchers(GET, "/api/user/getAll").hasRole(MODERATOR)
                        .requestMatchers(PATCH, "/api/user/enable", "/api/user/disable", "/api/user/updateRole").hasRole(MODERATOR)
                        .requestMatchers(PATCH, "/api/user/update").hasRole(BUYER)
                        .requestMatchers(DELETE, "/api/user/delete").hasRole(BUYER)
                        .requestMatchers(POST, "/api/user/getUsersFiltered").hasRole(MODERATOR)

                        // Cart requests
                        .requestMatchers(GET, "/api/cart/findById", "/api/cart/findByUserId").hasRole(BUYER)
                        .requestMatchers(POST, "/api/cart/addPurchasedProduct").access(hasSpecificRole(Role.BUYER, Role.SELLER))
                        .requestMatchers(DELETE, "/api/cart/removePurchasedProduct").access(hasSpecificRole(Role.BUYER, Role.SELLER))

                        // SubCategory requests
                        .requestMatchers(GET, "/api/subCategory/getAll", "/api/subCategory/getById", "/api/subCategory/findProductsBySubCategoryId", "/api/subCategory/findFiltered").hasRole(BUYER)
                        .requestMatchers(POST, "/api/subCategory/create").hasRole(MODERATOR)
                        .requestMatchers(PATCH, "/api/subCategory/update").hasRole(MODERATOR)
                        .requestMatchers(DELETE, "/api/subCategory/delete").hasRole(MODERATOR)

                        // MacroCategory requests
                        .requestMatchers(GET, "/api/macroCategory/getAll", "/api/macroCategory/findById", "/api/macroCategory/findFiltered").hasRole(BUYER)
                        .requestMatchers(POST, "/api/macroCategory/create").hasRole(MODERATOR)
                        .requestMatchers(PATCH, "/api/macroCategory/update").hasRole(MODERATOR)
                        .requestMatchers(DELETE, "/api/macroCategory/delete").hasRole(MODERATOR)

                        // Product requests
                        .requestMatchers(GET, "/api/products/findAll", "/api/products/details", "/api/products/findAllByUserId", "/api/products/summary", "/api/products/summaryByUserId").hasRole(BUYER)
                        .requestMatchers(GET, "/api/products/getTags", "/api/products/getPurchaseHistoryWithAnalysis").hasRole(SELLER)
                        .requestMatchers(POST, "/api/products/createProduct").access(hasSpecificRole(Role.SELLER))
                        .requestMatchers(DELETE, "/api/products/removeProduct").access(hasSpecificRole(Role.SELLER))

                        // Auction Requests
                        .requestMatchers(GET, "/api/auction/details", "/api/auction/active", "/api/auction/closed", "/api/auction/pending", "/api/auction/auctionActiveSummary",
                                "/api/auction/auctionPendingSummary", "/api/auction/summaryByUserId", "/api/auction/getClosedAuctionsByUser", "/api/auction/getPendingAuctionsByUser",
                                "/api/auction/getOpenAuctionsByUser").hasRole(BUYER)
                        .requestMatchers(GET, "/api/auction/subscribed").access(hasSpecificRole(Role.SELLER))
                        .requestMatchers(POST, "/api/auction/create", "/api/auction/assignWinner").access(hasSpecificRole(Role.BUYER, Role.SELLER))
                        .requestMatchers(POST, "/api/auction/subscribeUserNotification").access(hasSpecificRole(Role.SELLER))
                        .requestMatchers(PATCH, "/api/auction/update").access(hasSpecificRole(Role.BUYER, Role.SELLER))
                        .requestMatchers(DELETE, "/api/auction/delete").access(hasSpecificRole(Role.BUYER, Role.SELLER))

                        // Offer requests
                        .requestMatchers(GET, "/api/offer/getOfferByUser").hasRole(BUYER)
                        .requestMatchers(GET, "/api/offer/getOfferById", "/api/offer/getOffersByAuctionId").hasRole(BUYER)
                        .requestMatchers(POST, "/api/offer/create").access(hasSpecificRole(Role.SELLER))
                        .requestMatchers(PATCH, "/api/offer/update").access(hasSpecificRole(Role.SELLER))
                        .requestMatchers(DELETE, "/api/offer/delete").access(hasSpecificRole(Role.SELLER))

                        // Order requests
                        .requestMatchers(GET, "/api/order/getOrderByUser").access(hasSpecificRole(Role.BUYER, Role.SELLER))
                        .requestMatchers(POST, "/api/order/create").access(hasSpecificRole(Role.BUYER, Role.SELLER))
                        .requestMatchers(POST, "/api/order/getFilteredOrders").access(hasSpecificRole(Role.BUYER, Role.SELLER))
                        .requestMatchers(GET, "/api/order/getReceivedOrdersBySeller", "/api/order/getDelayedOrdersBySeller", "/api/order/getTakeOnOrdersBySeller", "/api/order/getRefusedOrdersBySeller", "/api/order/getDeliveredOrdersBySeller", "/api/order/getPendingOrdersBySeller", "/api/order/getOrderProductById").hasRole(SELLER)
                        .requestMatchers(POST, "/api/order/acceptSingleOrderProduct", "/api/order/refuseSingleOrderProduct", "/api/order/getFilteredOrdersBySeller").hasRole(SELLER)

                        // Review requests
                        .requestMatchers(GET, "/api/reviews/findByProductId", "/api/reviews/getReviewsReceivedByUserId", "/api/reviews/getReviewsByUserId", "/api/reviews/reviewSummaryByProductId", "/api/reviews/reviewSummaryByUserId").hasRole(BUYER)
                        .requestMatchers(POST, "/api/reviews/create").hasRole(BUYER)

                        //Filter requests
                        .requestMatchers(POST, "/api/filter/filterHome").hasRole(BUYER)

                        // Default request
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(corsFilter(), JwtAuthFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> handlerExceptionResolver.resolveException(request, response, null, authException))
                        .accessDeniedHandler((request, response, accessDeniedException) -> handlerExceptionResolver.resolveException(request, response, null, accessDeniedException))
                );

        return http.build();
    }

    /**
     * Creates a custom {@link AuthorizationManager} for role-based authorization checks.
     *
     * @param roles the roles to check against for authorization.
     * @return the created {@link AuthorizationManager} instance.
     */
    AuthorizationManager<RequestAuthorizationContext> hasSpecificRole(Role... roles) {
        return authorizationManager.create(roles);
    }

    /**
     * Creates a CORS filter to handle cross-origin requests.
     * <p>
     * The filter allows specified origins, methods, and headers, enabling CORS support for the application.
     * </p>
     *
     * @return the configured {@link CorsFilter}.
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Add all allowed origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH")); // Add all allowed methods
        configuration.setAllowedHeaders(List.of("*")); // Add all allowed headers
        configuration.setAllowCredentials(true); // Enable support for credentials (cookies)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }
}
