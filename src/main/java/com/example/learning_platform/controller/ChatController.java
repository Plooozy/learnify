package com.example.learning_platform.controller;

import com.example.learning_platform.dto.ChatMessageDto;
import com.example.learning_platform.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<String> chat(@Valid @RequestBody ChatMessageDto request, Authentication authentication) {
        log.info("Received chat request from user: {}", authentication.getName());
        String response = chatService.sendMessage(request.message(), authentication.getName());
        log.info("Chat response generated for user: {}", authentication.getName());
        return ResponseEntity.ok(response);
    }
}