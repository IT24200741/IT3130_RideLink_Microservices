package com.ridelink.ride.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret:ridelink-account-service-super-secret-key-2026-it3130}")
    private String jwtSecret;

    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractClaims(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isBlank()) return null;

        String[] parts = token.split("\\.");
        if (parts.length == 2) {
            try {
                byte[] decoded = Base64.getUrlDecoder().decode(parts[0]);
                String payload = new String(decoded, StandardCharsets.UTF_8);
                String[] fields = payload.split(":");
                if (fields.length >= 3) {
                    return fields[0];
                }
            } catch (Exception ignored) {}
        }

        try {
            Claims claims = extractClaims(token);
            String userId = claims.get("userId", String.class);
            return userId != null ? userId : claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public String extractRole(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isBlank()) return null;

        String[] parts = token.split("\\.");
        if (parts.length == 2) {
            try {
                byte[] decoded = Base64.getUrlDecoder().decode(parts[0]);
                String payload = new String(decoded, StandardCharsets.UTF_8);
                String[] fields = payload.split(":");
                if (fields.length >= 3) {
                    return fields[2];
                }
            } catch (Exception ignored) {}
        }

        try {
            Claims claims = extractClaims(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
