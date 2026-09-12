package com.spring.ai.chapter09_1.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {
    private final ChatClient chatClient;

    @Value("${notion.block.id}")
    String notionBlockId;

    // AiConfig에서 만든 ChatClient Bean을 주입받음
    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // 노트 생성 메서드: write_file 도구 호출 유도
  /*  public String createNote(Map<String, String> body) {
        // 제목에서 특수문자 제거 및 타임스탬프 추가해 파일명 생성
        String title = body.getOrDefault("title", "제목없음");
        String content = body.getOrDefault("content", "");
        String safe = title.replaceAll("[^가-힣a-zA-Z0-9_-]", "-");
        String ts = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
        String path = "./workspace/note_" + safe + "_" + ts + ".txt";

        // 시스템 프롬프트 구성
        PromptTemplate systemPromptTemplate = new PromptTemplate("""
                당신은 파일 비서입니다.
                반드시 MCP filesystem 도구를 사용하세요.
                성공 시 정확히 다음 한 문장만 출력하세요: 저장 완료: {path}
                """);
        Message sprompt = systemPromptTemplate.createMessage(Map.of("path", path));

        // 사용자 프롬프트 구성
        PromptTemplate userPromptTemplate = new PromptTemplate("""
                아래 내용을 파일로 저장해 주세요.
                - path: {path}
                - content: {content}
                필요하면 write_file(path, content) 도구를 호출하세요.
                """);
        Message uprompt = userPromptTemplate.createMessage(Map.of("path", path, "content", content));

        // LLM 호출
        Prompt prompt = new Prompt(List.of(sprompt, uprompt));
        return chatClient.prompt(prompt).call().content();
    }*/
    public String createNote(Map<String, String> body) {
        // 1. 파일명 생성 및 경로 설정
        String title = body.getOrDefault("title", "제목없음");
        String content = body.getOrDefault("content", "");
        String safe = title.replaceAll("[^가-힣a-zA-Z0-9_-]", "-");
        String ts = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
        String path = "./workspace/note_" + safe + "_" + ts + ".txt";

        // 2. 시스템 프롬프트: 두 도구 사용 지시
        String sysPromptText = """
                당신은 회의록 에이전트입니다. MCP filesystem과 notion 도구를 사용하여 요청을 처리하세요.
                성공 시 정확히 다음 한 문장만 출력하세요: 저장 완료: {path}
                """;
        PromptTemplate systemPromptTemplate = new PromptTemplate(sysPromptText);
        Message sprompt = systemPromptTemplate.createMessage(Map.of("path", path));

        // 3. 사용자 프롬프트: 파일 저장 및 Notion 추가에 필요한 모든 인자 전달
        String userPromptText = """
                아래 회의록을 파일로 저장하고 노션 페이지에 추가하세요.
                - path: {path}
                - content: {content}
                - block_id: {notionBlockId}
                - title: {title}
                - content: {content}
                필요하면 write_file(path, content) 도구와 notion_append_block_children 도구를 호출하세요.
                """;
        PromptTemplate userPromptTemplate = new PromptTemplate(userPromptText);

        // notionBlockId 값을 @Value 필드에서 가져와 Map에 포함
        Map<String, Object> upVars = Map.of("path", path, "content", content, "title", title, "notionBlockId", notionBlockId);
        Message uprompt = userPromptTemplate.createMessage(upVars);

        // 4. Prompt 객체 생성 및 LLM 호출
        Prompt prompt = new Prompt(List.of(sprompt, uprompt));
        return chatClient.prompt(prompt).call().content();
    }

    // (...생략...)
   // 노트 목록 조회: list_directory 도구 호출 유도
   public String listNotes() {
       // 1. 시스템 메시지: 에이전트의 역할(Persona)과 행동 지침(Guardrail) 정의
       String sys = """
            당신은 MCP(Model Context Protocol) 환경의 파일 관리 전문 비서입니다.
            제공된 filesystem 도구를 사용하여 요청을 수행하며, 
            결과 출력 시 부연 설명이나 인사말 없이 오직 파일 이름만 줄바꿈으로 나열하세요.
            """;

       // 2. 사용자 메시지: 현재 수행할 구체적인 작업(Task) 요청
       String user = "'./workspace' 디렉터리에 존재하는 모든 파일의 이름을 조회하여 나열해 주세요.";

       return chatClient.prompt()
               .system(s -> s.text(sys))
               .user(user)
               .call()
               .content();
   }
    // 노트 내용 읽기: read_file 도구 호출 유도
    public String readNote(String filename) {
        String sys = """
                당신은 MCP filesystem의 read_file 도구만 사용해야 합니다.
                - 루트는 './workspace' 입니다.
                - path는 반드시 아래 문자열과 '완전히 동일'해야 합니다.
                - 도구 호출이 실패하면 다른 설명 없이 "읽기 실패"만 출력하세요.
                """;
        String user = """
                아래 파일을 읽어서 내용만 그대로 출력하세요.
                path: {path}
                """;

        return chatClient.prompt()
                .system(s -> s.text(sys))
                .user(u -> u.text(user).param("path", "./workspace/" + filename))
                .call()
                .content();
    }
}