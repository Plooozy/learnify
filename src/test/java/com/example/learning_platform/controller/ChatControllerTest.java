package com.example.learning_platform.controller;

import com.example.learning_platform.dto.ChatMessageDto;
import com.example.learning_platform.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    @Test
    @WithMockUser(username = "testuser")
    void chat_AuthorizedUser_ReturnsOk() throws Exception {
        ChatMessageDto dto = new ChatMessageDto("Hello, how are you?");
        String expectedResponse = "I'm doing well, thank you!";

        when(chatService.sendMessage(anyString(), anyString())).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_AuthorizedUser_EmptyMessage_ReturnsOk() throws Exception {
        ChatMessageDto dto = new ChatMessageDto("");
        String expectedResponse = "Empty response";

        when(chatService.sendMessage(anyString(), anyString())).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_AuthorizedUser_LongMessage_ReturnsOk() throws Exception {
        String longMessage = "A".repeat(1000);
        ChatMessageDto dto = new ChatMessageDto(longMessage);
        String expectedResponse = "Long response received";

        when(chatService.sendMessage(anyString(), anyString())).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_AuthorizedUser_ServiceReturnsError_ReturnsOk() throws Exception {
        ChatMessageDto dto = new ChatMessageDto("Hello");
        String expectedResponse = "Error occurred";

        when(chatService.sendMessage(anyString(), anyString())).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_AuthorizedUser_SpecialCharacters_ReturnsOk() throws Exception {
        ChatMessageDto dto = new ChatMessageDto("Hello! @#$%^&*()");
        String expectedResponse = "Special characters handled";

        when(chatService.sendMessage(anyString(), anyString())).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_AuthorizedUser_UnicodeCharacters_ReturnsOk() throws Exception {
        ChatMessageDto dto = new ChatMessageDto("Привет! 你好! こんにちは!");
        String expectedResponse = "Unicode handled";

        when(chatService.sendMessage(anyString(), anyString())).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void chat_UnauthorizedUser_ReturnsUnauthorized() throws Exception {
        ChatMessageDto dto = new ChatMessageDto("Hello");

        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_InvalidContentType_ReturnsUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Hello"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_InvalidJson_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "adminuser")
    void chat_DifferentAuthorizedUser_ReturnsOk() throws Exception {
        ChatMessageDto dto = new ChatMessageDto("Hello admin");
        String expectedResponse = "Admin response";

        when(chatService.sendMessage(anyString(), anyString())).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_MultipleRequests_ReturnsOk() throws Exception {
        ChatMessageDto dto1 = new ChatMessageDto("First message");
        ChatMessageDto dto2 = new ChatMessageDto("Second message");

        when(chatService.sendMessage(anyString(), anyString()))
                .thenReturn("First response")
                .thenReturn("Second response");

        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isOk())
                .andExpect(content().string("First response"));

        mockMvc.perform(post("/api/chat")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isOk())
                .andExpect(content().string("Second response"));
    }
}