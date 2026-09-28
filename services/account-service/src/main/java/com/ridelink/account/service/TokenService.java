package com.ridelink.account.service;

import com.ridelink.account.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class TokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final long DEFAULT_EXPIRY_MILLIS = 86400000L; // 24 hours

    @Value("${security.jwt.secret:ridelink-account-service-super-secret-key-2026-it3130}")
    private String secretKey = "ridelink-account-service-super-secret-key-2026-it3130";

    public TokenService() {
    }

    public TokenService(String secretKey) {
        this.secretKey = secretKey;
    }

    public static class TokenClaims {
        private final String userId;
        private final String email;
        private final Role role;
        private final long expiresAt;

        public TokenClaims(String userId, String email, Role role, long expiresAt) {
            this.userId = userId;
            this.email = email;
            this.role = role;
            this.expiresAt = expiresAt;
        }

        public String getUserId() {
            return userId;
        }

        public String getEmail() {
            return email;
        }

        public Role getRole() {
            return role;
        }

        public long getExpiresAt() {
            return expiresAt;
        }
    }

    public String generateToken(String userId, String email, Role role) {
        long expiresAt = System.currentTimeMillis() + DEFAULT_EXPIRY_MILLIS;
        String payload = userId + ":" + email + ":" + role.name() + ":" + expiresAt;
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signature = sign(encodedPayload);
        return encodedPayload + "." + signature;
    }

    public TokenClaims validateAndParseToken(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authentication token");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Malformed authentication token");
        }

        String encodedPayload = parts[0];
        String expectedSignature = sign(encodedPayload);

        if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), parts[1].getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or tampered token signature");
        }

        try {
            byte[] decoded = Base64.getUrlDecoder().decode(encodedPayload);
            String payload = new String(decoded, StandardCharsets.UTF_8);
            String[] fields = payload.split(":");
            if (fields.length != 4) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token payload structure");
            }

            String userId = fields[0];
            String email = fields[1];
            Role role = Role.valueOf(fields[2]);
            long expiresAt = Long.parseLong(fields[3]);

            if (System.currentTimeMillis() > expiresAt) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has expired. Please login again.");
            }

            return new TokenClaims(userId, email, role, expiresAt);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token content: " + e.getMessage());
        }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating HMAC signature", e);
        }
    }
}
