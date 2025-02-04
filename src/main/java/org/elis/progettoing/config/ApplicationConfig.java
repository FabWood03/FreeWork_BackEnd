package org.elis.progettoing.config;

import org.elis.progettoing.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application Security Configuration.
 * <p>
 * This class configures the authentication system and user management,
 * including the authentication provider, authentication manager, and password encoder.
 * </p>
 */
@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    /**
     * Constructor for the configuration class.
     *
     * @param userRepository the repository for accessing user data
     */
    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Bean providing an implementation of {@link UserDetailsService}.
     * This method loads a user from the database using their email.
     * If the user is not found, a {@link UsernameNotFoundException} is thrown.
     *
     * @return an instance of {@link UserDetailsService} for loading users
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Bean for creating an {@link AuthenticationProvider}.
     * This method provides an {@link AuthenticationProvider} that uses a {@link DaoAuthenticationProvider}
     * to authenticate users by checking their credentials in the database.
     *
     * @return an instance of {@link AuthenticationProvider} for user authentication
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        // Set the userDetailsService to retrieve user details from the database
        authenticationProvider.setUserDetailsService(userDetailsService());

        // Set the password encoder to verify the password
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    /**
     * Bean for creating an {@link AuthenticationManager}.
     * This method creates an {@link AuthenticationManager} using the provided
     * {@link AuthenticationConfiguration} for user authentication.
     *
     * @param config the authentication configuration
     * @return an instance of {@link AuthenticationManager}
     * @throws Exception if an error occurs during authentication manager creation
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Retrieve the authentication manager from the provided AuthenticationConfiguration
        return config.getAuthenticationManager();
    }

    /**
     * Bean for creating a {@link PasswordEncoder}.
     * This method creates a {@link PasswordEncoder} that uses the BCrypt hashing algorithm.
     * The encoder is used for securely encoding and verifying user passwords.
     *
     * @return an instance of {@link PasswordEncoder} for password encoding
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Return an instance of BCryptPasswordEncoder for password encryption
        return new BCryptPasswordEncoder();
    }
}
