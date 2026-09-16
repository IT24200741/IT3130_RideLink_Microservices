package com.ridelink.account.controller;

import com.ridelink.account.dto.ApiResponse;
import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UpdateStatusRequest;
import com.ridelink.account.dto.UserResponse;
import com.ridelink.account.service.AccountService;
import com.ridelink.account.model.Role;
import com.ridelink.account.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final AccountService accountService;
    private final TokenService tokenService;

    public UserController(AccountService accountService, TokenService tokenService) {
        this.accountService = accountService;
        this.tokenService = tokenService;
    }

    private Long parseUserId(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID cannot be empty");
        }
        String cleanId = id.trim();
        if (cleanId.toUpperCase().startsWith("USR-")) {
            cleanId = cleanId.substring(4);
        }
        try {
            return Long.parseLong(cleanId);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID format: " + id);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        Long userId = parseUserId(id);
        UserResponse response = accountService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody UpdateProfileRequest request) {
        Long targetUserId = parseUserId(id);

        if (authHeader != null && !authHeader.isBlank()) {
            TokenService.TokenClaims claims = tokenService.validateAndParseToken(authHeader);
            if (!claims.getUserId().equals(targetUserId) && claims.getRole() != Role.ADMIN) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update another user's profile");
            }
        }

        UserResponse response = accountService.updateProfile(targetUserId, request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody UpdateStatusRequest request) {
        Long targetUserId = parseUserId(id);

        if (authHeader != null && !authHeader.isBlank()) {
            TokenService.TokenClaims claims = tokenService.validateAndParseToken(authHeader);
            if (claims.getRole() != Role.ADMIN) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can modify account status");
            }
        }

        UserResponse response = accountService.updateStatus(targetUserId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok("Account status updated successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> response = accountService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
