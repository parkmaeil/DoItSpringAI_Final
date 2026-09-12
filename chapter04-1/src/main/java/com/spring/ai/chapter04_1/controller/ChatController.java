package com.spring.ai.chapter04_1.controller;

import com.spring.ai.chapter04_1.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private final ChatService chatService;
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat/template")
    public ResponseEntity<String> chatWithTemplate(@RequestParam String query) {
        String response = chatService.getExpertResponse(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chat/explicit")
    public ResponseEntity<String> chatWithExplicitTemplate(
            // subject 파라미터를 받습니다.
            @RequestParam(defaultValue = "Spring Framework") String subject,
            // example 파라미터를 받습니다.
            @RequestParam(defaultValue = "Spring @Controller example") String example
    ) {
        // Service에 하드코딩된 값이 아닌, 전달받은 파라미터를 넘깁니다.
        String response = chatService.getExplicitTemplateResponse(
                subject,
                example
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chat/role-dynamic")
    public ResponseEntity<String> chatWithRoleTemplate(
            @RequestParam(defaultValue = "Java") String subject,
            @RequestParam(defaultValue = "람다 스트림") String example
    ) {
        String response = chatService.getRoleBasedTemplateResponse(subject, example);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chat/external")
    public ResponseEntity<String> chatWithExternalTemplate(
            @RequestParam(defaultValue = "Spring Framework validation") String concept) {
        String response = chatService.getExternalTemplateResponse(concept);
        return ResponseEntity.ok(response);
    }

}