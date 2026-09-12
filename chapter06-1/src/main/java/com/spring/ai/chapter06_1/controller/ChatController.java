package com.spring.ai.chapter06_1.controller;

import com.spring.ai.chapter06_1.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /*
    @GetMapping("/chat")
    public String chat(@RequestParam("query") String query) {
        return chatService.chat(query);
    }*/

    @GetMapping("/chat")
    public ResponseEntity<String> chat(
            @RequestParam String query,
            @RequestHeader("userId") String userId) {
        return ResponseEntity.ok(chatService.chat(query, userId));
    }
}
