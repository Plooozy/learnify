package com.example.learning_platform.service;

import com.example.learning_platform.dto.GeminiRequest;
import com.example.learning_platform.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final RestTemplate restTemplate;

    @Value("${google.ai.api.key}")
    private String apiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    @Override
    public String sendMessage(String message, String username) {
        try {
            log.info("Processing message from user: {}", username);

            if (apiKey == null || apiKey.isEmpty() || apiKey.contains("${")) {
                return buildDemoResponse(username, message);
            }

            GeminiRequest request = new GeminiRequest(
                    List.of(new GeminiRequest.Content(
                            List.of(new GeminiRequest.Content.Part(message)))));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = GEMINI_API_URL + "?key=" + apiKey;
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, GeminiResponse.class);

            GeminiResponse body = response.getBody();
            if (body == null
                    || body.candidates() == null
                    || body.candidates().isEmpty()
                    || body.candidates().get(0) == null
                    || body.candidates().get(0).content() == null
                    || body.candidates().get(0).content().parts() == null
                    || body.candidates().get(0).content().parts().isEmpty()
                    || body.candidates().get(0).content().parts().get(0) == null
                    || body.candidates().get(0).content().parts().get(0).text() == null) {
                return buildDemoResponse(username, message);
            }

            return body.candidates().get(0).content().parts().get(0).text();

        } catch (Exception e) {
            log.error("Error calling Gemini API: ", e);
            return buildDemoResponse(username, message);
        }
    }

    private String buildDemoResponse(String username, String message) {
        return "Привет, " + username + "! Вы написали: " + message + ". Это демонстрационный ответ.";
    }
}