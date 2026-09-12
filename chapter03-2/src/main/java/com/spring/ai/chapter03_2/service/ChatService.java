package com.spring.ai.chapter03_2.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient openAiClient;
    private final ChatClient ollamaClient;

    public ChatService(
            @Qualifier("openAiChatClient") ChatClient openAiClient,
            @Qualifier("ollamaChatClient") ChatClient ollamaClient
    ) {
        this.openAiClient = openAiClient;
        this.ollamaClient = ollamaClient;
    }

    public String getOpenAiResponse(String query) {
        return openAiClient.prompt()
                .user(query)
                .call()
                .content(); // 유창한 API를 사용하여 답변 텍스트만 추출합니다.
    }

    public String getOllamaResponse(String query) {
        return ollamaClient.prompt()
                .user(query)
                .call()
                .content();
    }
}
