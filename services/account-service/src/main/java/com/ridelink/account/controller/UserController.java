package com.ridelink.account.controller;

import com.ridelink.account.dto.ApiResponse;
import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UpdateStatusRequest;
import com.ridelink.account.dto.UserResponse;
import com.ridelink.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final AccountService accountService;

    public UserController(AccountService accountService) {
        this.accountService = accountService;
    }

    private String parseUserId(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID cannot be empty");
        }
        String cleanId = id.trim();
        if (cleanId.toUpperCase().startsWith("USR-")) {
            cleanId = cleanId.substring(4);
        }
        if (cleanId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID format: " + id);
        }
        return cleanId;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        String userId = parseUserId(id);
        UserResponse response = accountService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @PathVariable String id,
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        String targetUserId = parseUserId(id);

        if (authentication != null && authentication.getPrincipal() instanceof String currentUserId) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!currentUserId.equals(targetUserId) && !isAdmin) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update another user's profile");
            }
        }

        UserResponse response = accountService.updateProfile(targetUserId, request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateStatusRequest request) {
        String targetUserId = parseUserId(id);
        UserResponse response = accountService.updateStatus(targetUserId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok("Account status updated successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> response = accountService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
