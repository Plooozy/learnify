package com.example.learning_platform.controller;

import com.example.learning_platform.dto.AuthResponseDto;
import com.example.learning_platform.dto.ChatMessageDto;
import com.example.learning_platform.dto.UserLoginDto;
import com.example.learning_platform.dto.UserRegistrationDto;
import com.example.learning_platform.repository.UserRepository;
import com.example.learning_platform.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ChatIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String validToken;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername(TEST_USERNAME);
        registrationDto.setPassword(TEST_PASSWORD);

        userService.registerUser(registrationDto);

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername(TEST_USERNAME);
        loginDto.setPassword(TEST_PASSWORD);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponseDto authResponse = objectMapper.readValue(response, AuthResponseDto.class);
        validToken = authResponse.getToken();
    }

    @Test
    void chat_WithValidToken_ReturnsOk() throws Exception {
        ChatMessageDto chatDto = new ChatMessageDto("Hello, how are you?");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validToken)
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(TEST_USERNAME)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Hello, how are you?")));
    }

    @Test
    void chat_WithInvalidToken_ReturnsUnauthorized() throws Exception {
        ChatMessageDto chatDto = new ChatMessageDto("Hello");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalid-token")
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void chat_WithoutToken_ReturnsUnauthorized() throws Exception {
        ChatMessageDto chatDto = new ChatMessageDto("Hello");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void chat_WithExpiredToken_ReturnsUnauthorized() throws Exception {
        ChatMessageDto chatDto = new ChatMessageDto("Hello");

        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleHBpcmVkIiwiaWF0IjoxNjAwMDAwMDAwLCJleHAiOjE2MDAwMDAwMDF9.signature";

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + expiredToken)
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void chat_WithMalformedToken_ReturnsUnauthorized() throws Exception {
        ChatMessageDto chatDto = new ChatMessageDto("Hello");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer malformed.token.here")
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void chat_WithWrongTokenType_ReturnsUnauthorized() throws Exception {
        ChatMessageDto chatDto = new ChatMessageDto("Hello");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Basic " + validToken)
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void chat_WithEmptyToken_ReturnsUnauthorized() throws Exception {
        ChatMessageDto chatDto = new ChatMessageDto("Hello");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer ")
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void chat_MultipleMessagesWithSameToken_ReturnsOk() throws Exception {
        ChatMessageDto firstMessage = new ChatMessageDto("First message");
        ChatMessageDto secondMessage = new ChatMessageDto("Second message");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validToken)
                        .content(objectMapper.writeValueAsString(firstMessage)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validToken)
                        .content(objectMapper.writeValueAsString(secondMessage)))
                .andExpect(status().isOk());
    }

    @Test
    void chat_WithDifferentUserToken_ReturnsOk() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("anotheruser");
        registrationDto.setPassword("password456");

        userService.registerUser(registrationDto);

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("anotheruser");
        loginDto.setPassword("password456");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponseDto authResponse = objectMapper.readValue(response, AuthResponseDto.class);
        String anotherToken = authResponse.getToken();

        ChatMessageDto chatDto = new ChatMessageDto("Hello from another user");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + anotherToken)
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("anotheruser")));
    }

    @Test
    void chat_WithSpecialCharacters_ReturnsOk() throws Exception {
        ChatMessageDto chatDto = new ChatMessageDto("Hello! @#$%^&*()");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validToken)
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isOk());
    }

    @Test
    void chat_WithUnicodeCharacters_ReturnsOk() throws Exception {
        ChatMessageDto chatDto = new ChatMessageDto("Привет! 你好! こんにちは!");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validToken)
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isOk());
    }

    @Test
    void chat_WithLongMessage_ReturnsOk() throws Exception {
        String longMessage = "A".repeat(1000);
        ChatMessageDto chatDto = new ChatMessageDto(longMessage);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validToken)
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isOk());
    }

    @Test
    void chat_WithEmptyMessage_ReturnsOk() throws Exception {
        ChatMessageDto chatDto = new ChatMessageDto("");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validToken)
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isOk());
    }

    @Test
    void login_CanGetTokenAndUseItForChat() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("newuser");
        registrationDto.setPassword("newpassword");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated());

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("newuser");
        loginDto.setPassword("newpassword");

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponseDto authResponse = objectMapper.readValue(loginResponse, AuthResponseDto.class);
        String token = authResponse.getToken();

        assertNotNull(token);
        assertFalse(token.isEmpty());

        ChatMessageDto chatDto = new ChatMessageDto("Test message");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isOk());
    }

    @Test
    void chat_CorsHeaders_ReturnsOk() throws Exception {
        ChatMessageDto chatDto = new ChatMessageDto("Hello");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validToken)
                        .header("Origin", "http://localhost:5173")
                        .content(objectMapper.writeValueAsString(chatDto)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }
}