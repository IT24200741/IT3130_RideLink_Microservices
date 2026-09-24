package com.ridelink.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.account.dto.LoginRequest;
import com.ridelink.account.dto.RegisterRequest;
import com.ridelink.account.model.Role;
import com.ridelink.account.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ridelink.account.BaseIntegrationTest;

class AuthControllerTest extends BaseIntegrationTest {

    @BeforeEach
    void setUp() {
        super.setupDatabaseMock();
    }

    @Test
    void registerUser_Success() throws Exception {
        RegisterRequest request = new RegisterRequest("Sunil Perera", "sunil@example.com", "Secret123", "+94771234567", Role.PASSENGER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("sunil@example.com"))
                .andExpect(jsonPath("$.data.userId").exists());
    }

    @Test
    void registerUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("Sunil", "invalid-email-format", "Secret123", "+94771234567", Role.PASSENGER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void loginUser_Success() throws Exception {
        RegisterRequest register = new RegisterRequest("Sunil Perera", "sunil@example.com", "Secret123", "+94771234567", Role.PASSENGER);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest("sunil@example.com", "Secret123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.role").value("PASSENGER"));
    }

    @Test
    void loginUser_WrongPassword_ReturnsUnauthorized() throws Exception {
        RegisterRequest register = new RegisterRequest("Sunil Perera", "sunil@example.com", "Secret123", "+94771234567", Role.PASSENGER);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest("sunil@example.com", "WrongPassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void loginUser_SuspendedAccount_ReturnsForbidden() throws Exception {
        RegisterRequest register = new RegisterRequest("Sunil Perera", "sunil@example.com", "Secret123", "+94771234567", Role.PASSENGER);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        com.ridelink.account.model.User user = userRepository.findByEmail("sunil@example.com").orElseThrow();
        user.setStatus(com.ridelink.account.model.AccountStatus.SUSPENDED);
        userRepository.save(user);

        LoginRequest login = new LoginRequest("sunil@example.com", "Secret123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void healthCheck_ReturnsUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("account-service"));
    }

    @Test
    void registerUser_AdminRole_ReturnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("Malicious Admin", "admin@example.com", "Secret123", "+94771234567", Role.ADMIN);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
