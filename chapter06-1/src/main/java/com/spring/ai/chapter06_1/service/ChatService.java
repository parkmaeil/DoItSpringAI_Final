package com.spring.ai.chapter06_1.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatClient chatClient;
    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

/*  public String chat(String query) {
        // 간단한 사용자 요청을 보내고 응답 내용을 반환합니다.
        return this.chatClient
                .prompt()
                .user(query)
                .call()
                .content();
    }*/

    public String chat(String query, String userId) {
        return this.chatClient
                .prompt()
                .advisors(advisorSpec ->
                        advisorSpec.param(ChatMemory.CONVERSATION_ID, userId))
                .user(query)
                .call()
                .content();
    }

}
