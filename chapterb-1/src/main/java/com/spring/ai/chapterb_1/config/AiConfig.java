package com.spring.ai.chapterb_1.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AiConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        // 전역 설정을 포함한 ChatClient 빈 생성
        return builder.build();
    }
}
