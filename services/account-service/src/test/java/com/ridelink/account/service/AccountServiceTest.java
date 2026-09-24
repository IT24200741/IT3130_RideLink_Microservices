package com.ridelink.account.service;

import com.ridelink.account.dto.AuthResponse;
import com.ridelink.account.dto.LoginRequest;
import com.ridelink.account.dto.RegisterRequest;
import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UserResponse;
import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import com.ridelink.account.model.User;
import com.ridelink.account.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AccountService accountService;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        accountService = new AccountService(userRepository, passwordEncoder, tokenService);
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest("Kamal Perera", "kamal@example.com", "Password123", "+94771234567", Role.PASSENGER);

        when(userRepository.existsByEmail("kamal@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashedPassword");

        User savedUser = new User("Kamal Perera", "kamal@example.com", "hashedPassword", "+94771234567", Role.PASSENGER);
        savedUser.setId("1");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = accountService.register(request);

        assertNotNull(response);
        assertEquals("USR-1", response.getUserId());
        assertEquals("kamal@example.com", response.getEmail());
        assertEquals(Role.PASSENGER, response.getRole());
        assertEquals(AccountStatus.ACTIVE, response.getStatus());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        RegisterRequest request = new RegisterRequest("Kamal Perera", "kamal@example.com", "Password123", "+94771234567", Role.PASSENGER);

        when(userRepository.existsByEmail("kamal@example.com")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> accountService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("kamal@example.com", "Password123");
        User user = new User("Kamal Perera", "kamal@example.com", "hashedPassword", "+94771234567", Role.PASSENGER);
        user.setId("1");

        when(userRepository.findByEmail("kamal@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "hashedPassword")).thenReturn(true);

        AuthResponse response = accountService.login(request);

        assertNotNull(response);
        assertEquals("USR-1", response.getUserId());
        assertEquals(Role.PASSENGER, response.getRole());
        assertNotNull(response.getToken());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        LoginRequest request = new LoginRequest("kamal@example.com", "WrongPassword");
        User user = new User("Kamal Perera", "kamal@example.com", "hashedPassword", "+94771234567", Role.PASSENGER);
        user.setId("1");

        when(userRepository.findByEmail("kamal@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> accountService.login(request));
    }

    @Test
    void login_SuspendedAccount_ThrowsForbidden() {
        LoginRequest request = new LoginRequest("kamal@example.com", "Password123");
        User user = new User("Kamal Perera", "kamal@example.com", "hashedPassword", "+94771234567", Role.PASSENGER);
        user.setId("1");
        user.setStatus(AccountStatus.SUSPENDED);

        when(userRepository.findByEmail("kamal@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "hashedPassword")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> accountService.login(request));
    }

    @Test
    void updateProfile_Success() {
        User user = new User("Kamal Perera", "kamal@example.com", "hashedPassword", "+94771234567", Role.PASSENGER);
        user.setId("1");

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UpdateProfileRequest updateReq = new UpdateProfileRequest("Kamal Silva", "+94779998888");
        UserResponse response = accountService.updateProfile("1", updateReq);

        assertNotNull(response);
        assertEquals("Kamal Silva", response.getName());
        assertEquals("+94779998888", response.getPhone());
    }

    @Test
    void updateStatus_Success() {
        User user = new User("Kamal Perera", "kamal@example.com", "hashedPassword", "+94771234567", Role.PASSENGER);
        user.setId("1");

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse response = accountService.updateStatus("1", AccountStatus.SUSPENDED);

        assertNotNull(response);
        assertEquals(AccountStatus.SUSPENDED, response.getStatus());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> accountService.getUserById("99"));
    }

    @Test
    void register_AdminRole_ThrowsException() {
        RegisterRequest request = new RegisterRequest("Admin User", "admin@example.com", "AdminPass123", "+94771234567", Role.ADMIN);

        assertThrows(ResponseStatusException.class, () -> accountService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
