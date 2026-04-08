package com.example.learning_platform;

import com.example.learning_platform.controller.AuthController;
import com.example.learning_platform.dto.UserLoginDto;
import com.example.learning_platform.dto.UserRegistrationDto;
import com.example.learning_platform.model.User;
import com.example.learning_platform.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser
    void register_Success() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("nick");
        dto.setPassword("12345");

        when(userService.registerUser(any())).thenReturn(new User());

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    @WithMockUser
    void register_UsernameAlreadyExists() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("nick");
        dto.setPassword("12345");

        when(userService.registerUser(any())).thenThrow(new RuntimeException("Username already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    @WithMockUser
    void login_Success() throws Exception {
        UserLoginDto dto = new UserLoginDto();
        dto.setUsername("nick");
        dto.setPassword("12345");

        User user = new User();
        user.setUsername("nick");
        user.setPasswordHash("hashedPassword");

        when(userService.findByUsername("nick")).thenReturn(user);
        when(passwordEncoder.matches("12345", "hashedPassword")).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    @WithMockUser
    void login_UserNotFound() throws Exception {
        UserLoginDto dto = new UserLoginDto();
        dto.setUsername("unknown");
        dto.setPassword("12345");

        when(userService.findByUsername("unknown")).thenReturn(null);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser
    void login_InvalidPassword() throws Exception {
        UserLoginDto dto = new UserLoginDto();
        dto.setUsername("nick");
        dto.setPassword("wrongpassword");

        User user = new User();
        user.setUsername("nick");
        user.setPasswordHash("hashedPassword");

        when(userService.findByUsername("nick")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid password"));
    }
}