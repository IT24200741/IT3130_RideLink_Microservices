package com.ridelink.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.account.dto.RegisterRequest;
import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UpdateStatusRequest;
import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import com.ridelink.account.model.User;
import com.ridelink.account.repository.UserRepository;
import com.ridelink.account.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void getUserById_Success() throws Exception {
        RegisterRequest register = new RegisterRequest("Nimal Perera", "nimal@example.com", "Password123", "+94711122334", Role.PASSENGER);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail("nimal@example.com").orElseThrow();

        mockMvc.perform(get("/api/v1/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("nimal@example.com"))
                .andExpect(jsonPath("$.data.name").value("Nimal Perera"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void updateProfile_Success() throws Exception {
        RegisterRequest register = new RegisterRequest("Nimal Perera", "nimal@example.com", "Password123", "+94711122334", Role.PASSENGER);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail("nimal@example.com").orElseThrow();

        UpdateProfileRequest updateReq = new UpdateProfileRequest("Nimal Updated", "+94770000000");

        mockMvc.perform(put("/api/v1/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Nimal Updated"))
                .andExpect(jsonPath("$.data.phone").value("+94770000000"));
    }

    @Test
    void updateStatus_Success() throws Exception {
        RegisterRequest register = new RegisterRequest("Nimal Perera", "nimal@example.com", "Password123", "+94711122334", Role.PASSENGER);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail("nimal@example.com").orElseThrow();

        UpdateStatusRequest statusReq = new UpdateStatusRequest(AccountStatus.SUSPENDED);

        mockMvc.perform(patch("/api/v1/users/" + user.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SUSPENDED"));
    }

    @Autowired
    private TokenService tokenService;

    @Test
    void getUserById_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getUserById_WithPrefixedUserId_Success() throws Exception {
        RegisterRequest register = new RegisterRequest("Nimal Perera", "nimal@example.com", "Password123", "+94711122334", Role.PASSENGER);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail("nimal@example.com").orElseThrow();

        // Testing endpoint with "USR-" prefix format
        mockMvc.perform(get("/api/v1/users/USR-" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value("USR-" + user.getId()))
                .andExpect(jsonPath("$.data.email").value("nimal@example.com"));
    }

    @Test
    void updateProfile_WithForeignUserToken_ReturnsForbidden() throws Exception {
        RegisterRequest register1 = new RegisterRequest("User One", "one@example.com", "Password123", "+94711122334", Role.PASSENGER);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register1)))
                .andExpect(status().isCreated());

        User user1 = userRepository.findByEmail("one@example.com").orElseThrow();

        // Generate token for a DIFFERENT user ID (e.g. user 999)
        String foreignToken = tokenService.generateToken(999L, "other@example.com", Role.PASSENGER);

        UpdateProfileRequest updateReq = new UpdateProfileRequest("Hacked Name", "+94770000000");

        mockMvc.perform(put("/api/v1/users/" + user1.getId())
                        .header("Authorization", "Bearer " + foreignToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }
}
