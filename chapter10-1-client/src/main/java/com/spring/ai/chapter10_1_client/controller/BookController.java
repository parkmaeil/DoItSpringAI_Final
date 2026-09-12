package com.spring.ai.chapter10_1_client.controller;

import com.spring.ai.chapter10_1_client.dto.Book;
import com.spring.ai.chapter10_1_client.dto.Question;
import com.spring.ai.chapter10_1_client.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {
    private final ChatService chatService;

    // ChatService를 주입받도록 구성
    public BookController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/mcp")
    public List<Book> ask(@RequestBody Question question) {
        // 실제 로직 처리를 ChatService로 위임
        return chatService.ask(question);
    }
}
