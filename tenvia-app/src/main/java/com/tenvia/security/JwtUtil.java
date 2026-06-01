package com.tenvia.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for generating, parsing, and validating JSON Web Tokens (JWT).
 * This class uses the HMAC SHA-256 algorithm to sign tokens.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    /**
     * Generates a new JWT token for a given user ID.
     * The token includes the user ID as both the subject and a custom claim.
     *
     * @param userId the ID of the user to generate the token for
     * @return a signed JWT string
     */
    public String generateToken(Long userId) {
        return Jwts.builder()
                .signWith(getSignInKey())
                .subject(userId.toString())
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .compact();
    }

    /**
     * Extracts the subject (username/userId) from the provided JWT string.
     *
     * @param jwt the signed JWT string
     * @return the extracted subject as a String
     */
    public String extractUsername(String jwt) {
        SecretKey key = getSignInKey();
        Jws<Claims> parsedClaims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt);
        return parsedClaims.getPayload().getSubject();
    }

    /**
     * Validates whether the given JWT string is valid.
     *
     * @param jwt the signed JWT string to validate
     * @return true if the token is valid, false otherwise (e.g., tampered, expired)
     */
    public boolean isValidToken(String jwt) {
        SecretKey key = getSignInKey();
        try {
            Jwts.parser().verifyWith(key).build().parse(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
