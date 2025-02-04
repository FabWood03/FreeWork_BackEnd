package org.elis.progettoing.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    // Inject the JwtService and mock UserDetails
    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    // Set up the test data before each test
    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Initialize JwtService instance
        jwtService = new JwtService();

        // Set SECRET field using reflection to simulate real scenario
        Field secretField = JwtService.class.getDeclaredField("SECRET");
        secretField.setAccessible(true);
        secretField.set(jwtService, "mySecretKeyForJWTGenerationWhichIsVeryLongAndBase64Encoded");

        // Set JWT expiration time to 1 hour (in milliseconds)
        Field expirationField = JwtService.class.getDeclaredField("JWT_EXPIRATION");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, 1000 * 60 * 60); // 1 hour in milliseconds
    }

    @Test
    void testGenerateToken() {
        String email = "test@example.com";

        // Generate the token using the email
        String token = jwtService.generateToken(email);

        // Extract the email from the generated token
        String extractedEmail = jwtService.extractEmail(token);

        // Assert that the extracted email matches the expected email
        Assertions.assertEquals(email, extractedEmail);
    }

    @Test
    void testExtractExpiration() {
        String email = "test@example.com";
        String token = jwtService.generateToken(email);

        // Extract the expiration date from the token
        Date expirationDate = jwtService.extractExpiration(token);

        // Assert that the expiration date is not null and is in the future
        Assertions.assertNotNull(expirationDate);
        Assertions.assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void testValidateTokenValid() {
        String email = "test@example.com";
        String token = jwtService.generateToken(email);

        // Mock the username from userDetails to match the email in the token
        Mockito.when(userDetails.getUsername()).thenReturn(email);

        // Assert that the token is valid when the username matches
        Assertions.assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void testValidateTokenInvalid() {
        String email = "test@example.com";
        String wrongEmail = "wrong@example.com";
        String token = jwtService.generateToken(email);

        // Mock the username from userDetails to be a different email
        when(userDetails.getUsername()).thenReturn(wrongEmail);

        // Assert that the token is not valid when the username doesn't match
        assertFalse(jwtService.validateToken(token, userDetails));
    }
}
