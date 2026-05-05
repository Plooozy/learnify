package com.example.learning_platform.service;

import com.example.learning_platform.dto.GeminiRequest;
import com.example.learning_platform.dto.GeminiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    void sendMessage_Success() {
        String testMessage = "Hello, how are you?";
        String testUsername = "testuser";
        String testApiKey = "test-api-key";

        ReflectionTestUtils.setField(chatService, "apiKey", testApiKey);

        GeminiResponse mockResponse = new GeminiResponse(
            List.of(new GeminiResponse.Candidate(
                new GeminiResponse.Candidate.Content(
                    List.of(new GeminiResponse.Candidate.Content.Part("I'm doing well, thank you!"))
                )
            ))
        );

        ResponseEntity<GeminiResponse> responseEntity = new ResponseEntity<>(
            mockResponse,
            HttpStatus.OK
        );

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(GeminiResponse.class)
        )).thenReturn(responseEntity);

        String result = chatService.sendMessage(testMessage, testUsername);

        assertNotNull(result);
        assertEquals("I'm doing well, thank you!", result);
        verify(restTemplate, times(1)).exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(GeminiResponse.class)
        );
    }

    @Test
    void sendMessage_ApiKeyNotConfigured() {
        String testMessage = "Hello";
        String testUsername = "testuser";

        ReflectionTestUtils.setField(chatService, "apiKey", "${GEMINI_API_KEY}");

        String result = chatService.sendMessage(testMessage, testUsername);

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("демонстрационный ответ"));
        verify(restTemplate, never()).exchange(
            anyString(),
            any(HttpMethod.class),
            any(HttpEntity.class),
            eq(GeminiResponse.class)
        );
    }

    @Test
    void sendMessage_ApiKeyIsNull() {
        String testMessage = "Hello";
        String testUsername = "testuser";

        ReflectionTestUtils.setField(chatService, "apiKey", null);

        String result = chatService.sendMessage(testMessage, testUsername);

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("демонстрационный ответ"));
        verify(restTemplate, never()).exchange(
            anyString(),
            any(HttpMethod.class),
            any(HttpEntity.class),
            eq(GeminiResponse.class)
        );
    }

    @Test
    void sendMessage_ApiKeyIsEmpty() {
        String testMessage = "Hello";
        String testUsername = "testuser";

        ReflectionTestUtils.setField(chatService, "apiKey", "");

        String result = chatService.sendMessage(testMessage, testUsername);

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("демонстрационный ответ"));
        verify(restTemplate, never()).exchange(
            anyString(),
            any(HttpMethod.class),
            any(HttpEntity.class),
            eq(GeminiResponse.class)
        );
    }

    @Test
    void sendMessage_ApiReturns404() {
        String testMessage = "Hello";
        String testUsername = "testuser";
        String testApiKey = "test-api-key";

        ReflectionTestUtils.setField(chatService, "apiKey", testApiKey);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(GeminiResponse.class)
        )).thenThrow(new HttpClientException(HttpStatus.NOT_FOUND));

        String result = chatService.sendMessage(testMessage, testUsername);

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("демонстрационный ответ"));
    }

    @Test
    void sendMessage_ApiReturns500() {
        String testMessage = "Hello";
        String testUsername = "testuser";
        String testApiKey = "test-api-key";

        ReflectionTestUtils.setField(chatService, "apiKey", testApiKey);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(GeminiResponse.class)
        )).thenThrow(new HttpClientException(HttpStatus.INTERNAL_SERVER_ERROR));

        String result = chatService.sendMessage(testMessage, testUsername);

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("демонстрационный ответ"));
    }

    @Test
    void sendMessage_NetworkError() {
        String testMessage = "Hello";
        String testUsername = "testuser";
        String testApiKey = "test-api-key";

        ReflectionTestUtils.setField(chatService, "apiKey", testApiKey);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(GeminiResponse.class)
        )).thenThrow(new RestClientException("Network error"));

        String result = chatService.sendMessage(testMessage, testUsername);

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("демонстрационный ответ"));
    }

    @Test
    void sendMessage_EmptyResponseFromApi() {
        String testMessage = "Hello";
        String testUsername = "testuser";
        String testApiKey = "test-api-key";

        ReflectionTestUtils.setField(chatService, "apiKey", testApiKey);

        GeminiResponse emptyResponse = new GeminiResponse(List.of());

        ResponseEntity<GeminiResponse> responseEntity = new ResponseEntity<>(
            emptyResponse,
            HttpStatus.OK
        );

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(GeminiResponse.class)
        )).thenReturn(responseEntity);

        String result = chatService.sendMessage(testMessage, testUsername);

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("демонстрационный ответ"));
    }

    @Test
    void sendMessage_NullResponseBody() {
        String testMessage = "Hello";
        String testUsername = "testuser";
        String testApiKey = "test-api-key";

        ReflectionTestUtils.setField(chatService, "apiKey", testApiKey);

        ResponseEntity<GeminiResponse> responseEntity = new ResponseEntity<>(
            null,
            HttpStatus.OK
        );

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(GeminiResponse.class)
        )).thenReturn(responseEntity);

        String result = chatService.sendMessage(testMessage, testUsername);

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("демонстрационный ответ"));
    }

    @Test
    void sendMessage_EmptyMessage() {
        String testMessage = "";
        String testUsername = "testuser";
        String testApiKey = "test-api-key";

        ReflectionTestUtils.setField(chatService, "apiKey", testApiKey);

        GeminiResponse mockResponse = new GeminiResponse(
            List.of(new GeminiResponse.Candidate(
                new GeminiResponse.Candidate.Content(
                    List.of(new GeminiResponse.Candidate.Content.Part("Response"))
                )
            ))
        );

        ResponseEntity<GeminiResponse> responseEntity = new ResponseEntity<>(
            mockResponse,
            HttpStatus.OK
        );

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(GeminiResponse.class)
        )).thenReturn(responseEntity);

        String result = chatService.sendMessage(testMessage, testUsername);

        assertNotNull(result);
        assertEquals("Response", result);
    }

    @Test
    void sendMessage_LongMessage() {
        String testMessage = "A".repeat(1000);
        String testUsername = "testuser";
        String testApiKey = "test-api-key";

        ReflectionTestUtils.setField(chatService, "apiKey", testApiKey);

        GeminiResponse mockResponse = new GeminiResponse(
            List.of(new GeminiResponse.Candidate(
                new GeminiResponse.Candidate.Content(
                    List.of(new GeminiResponse.Candidate.Content.Part("Long response"))
                )
            ))
        );

        ResponseEntity<GeminiResponse> responseEntity = new ResponseEntity<>(
            mockResponse,
            HttpStatus.OK
        );

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(GeminiResponse.class)
        )).thenReturn(responseEntity);

        String result = chatService.sendMessage(testMessage, testUsername);

        assertNotNull(result);
        assertEquals("Long response", result);
    }

    private static class HttpClientException extends RuntimeException {
        private final HttpStatus status;

        public HttpClientException(HttpStatus status) {
            super("HTTP error: " + status);
            this.status = status;
        }

        public HttpStatus getStatus() {
            return status;
        }
    }
}