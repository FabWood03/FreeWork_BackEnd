package org.elis.progettoing.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for handling JWT tokens.
 * <p>
 *     This class provides methods for generating, validating and extracting information from JWT tokens.
 *     It uses the io.jsonwebtoken library for token processing.
 *     The secret key and expiration time are read from the application.properties file.
 * </p>
 */
@Service
public class JwtService {
    @Value("${security.jwt.secret}")
    private String SECRET;

    @Value("${security.jwt.expiration}")
    private long JWT_EXPIRATION;

    /**
     * Generates a JWT token for the given email.
     *
     * @param email The email to generate the token for.
     * @return The generated JWT token.
     */
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>(); // -> creo una mappa vuota che conterrà le informazioni che voglio inserire nel token JWT
        return createToken(claims, email, JWT_EXPIRATION);
    }

    /**
     * Extracts the secret key from the `SECRET` property.
     *
     * @return A SecretKey object representing the secret key used to sign and verify JWTs.
     */
    private SecretKey getSignInKey() {
        // Decodes the base64 encoded secret key.
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        // Converts the decoded key bytes into a SecretKey object using the HMAC SHA-256 algorithm.
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Creates a JWT token with the given claims, email and expiration time.
     *
     * @param claims     The claims to include in the token.
     * @param email      The email to include in the token.
     * @param expiration The expiration time of the token.
     * @return The generated JWT token.
     */
    private String createToken(Map<String, Object> claims, String email, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Extracts the email from the given JWT token.
     *
     * @param token The JWT token to extract the email from.
     * @return The email extracted from the token.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a claim from the given JWT token using the provided resolver function.
     *
     * @param token         The JWT token to extract the claim from.
     * @param claimsResolver The resolver function to extract the claim.
     * @param <T>           The type of the claim.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the given JWT token.
     *
     * @param token The JWT token to extract the claims from.
     * @return The extracted claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks if the given JWT token is expired.
     *
     * @param token The JWT token to check.
     * @return {@code true} if the token is expired, {@code false} otherwise.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token)
                .before(new Date());
    }

    /**
     * Extracts the expiration date from the given JWT token.
     *
     * @param token The JWT token to extract the expiration date from.
     * @return The expiration date extracted from the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Validates the given JWT token for the provided user details.
     *
     * @param token       The JWT token to validate.
     * @param userDetails The user details to validate the token against.
     * @return {@code true} if the token is valid, {@code false} otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);

        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
