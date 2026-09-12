package com.spring.ai.chapter05_1.controller;

import com.spring.ai.chapter05_1.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {
    private final ChatService chatService;
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("query") String query) {
        return chatService.chat(query);
    }

    @GetMapping("/stream-chat")
    public ResponseEntity<Flux<String>> streamChat(@RequestParam("query") String query) {
        return ResponseEntity.ok(chatService.streamChat(query));
    }
}
