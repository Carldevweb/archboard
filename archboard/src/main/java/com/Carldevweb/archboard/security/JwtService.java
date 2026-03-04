package com.Carldevweb.archboard.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final String CLAIM_UID = "uid";
    private static final String CLAIM_ROLE = "role";

    private final SecretKey key;
    private final long expirationSeconds;

    public JwtService(
            @Value("${archboard.jwt.secret}") String secret,
            @Value("${archboard.jwt.expiration-seconds}") long expirationSeconds
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("archboard.jwt.secret must be at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(Long userId, String subjectEmail, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
                .subject(subjectEmail)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim(CLAIM_UID, String.valueOf(userId))
                .claim(CLAIM_ROLE, role)
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        String raw = parseClaims(token).get(CLAIM_UID, String.class);
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Missing uid claim");
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid uid claim: " + raw);
        }
    }

    public String extractRole(String token) {
        String raw = parseClaims(token).get(CLAIM_ROLE, String.class);
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Missing role claim");
        }
        return raw;
    }
}