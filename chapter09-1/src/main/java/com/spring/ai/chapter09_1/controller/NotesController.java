package com.spring.ai.chapter09_1.controller;

import com.spring.ai.chapter09_1.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class NotesController {
    private final ChatService chatService;

    // ChatService Bean을 주입받아 로직을 위임
    public NotesController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 노트 생성: write_file 도구 호출
    @PostMapping(
            value = "/notes/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String create(@RequestBody Map<String, String> body) {
        // 실제 파일 생성 로직을 서비스에 위임합니다.
        return chatService.createNote(body);
    }

    // 노트 목록 조회: list_directory 도구 호출
    @GetMapping(
            value = "/notes/list",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String list() {
        // 파일 목록 조회 로직을 서비스에 위임
        return chatService.listNotes();
    }

    // 노트 내용 읽기: read_file 도구 호출
    @GetMapping(
            value = "/notes/read",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String read(@RequestParam String filename) {
        // 특정 파일 읽기 로직을 서비스에 위임
        return chatService.readNote(filename);
    }
}
