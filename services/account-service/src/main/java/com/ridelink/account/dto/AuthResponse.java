package com.ridelink.account.dto;

import com.ridelink.account.model.Role;

public class AuthResponse {

    private String token;
    private String userId;
    private Role role;
    private long expiresIn;

    public AuthResponse() {
    }

    public AuthResponse(String token, String userId, Role role, long expiresIn) {
        this.token = token;
        this.userId = userId;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
