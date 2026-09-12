package com.spring.ai.chapter06_1.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    /*  @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        // 현재는 메모리 어드바이저 없이 기본 로거만 등록합니다.
        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }*/

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository) // DB 저장소 연결
                .maxMessages(10) // 최근 10개만 메모리로 사용하도록 설정
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {

        MessageChatMemoryAdvisor memoryAdvisor =
                MessageChatMemoryAdvisor.builder(chatMemory).build();

        return builder
                .defaultAdvisors(memoryAdvisor, new SimpleLoggerAdvisor())
                .defaultSystem("You are a helpful coding assistant.")
                .build();
    }

}

