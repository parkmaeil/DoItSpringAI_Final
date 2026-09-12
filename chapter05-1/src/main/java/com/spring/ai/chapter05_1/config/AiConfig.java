package com.spring.ai.chapter05_1.config;

import com.spring.ai.chapter05_1.advisor.TokenPrintAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AiConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(
                        //  new SafeGuardAdvisor(List.of("games")))
                        // new SafeGuardAdvisor(List.of("games", "gambling", "illegal")))
                        new TokenPrintAdvisor(), // 사용자 정의 어드바이저
                        new SimpleLoggerAdvisor(), // 기본 로깅 어드바이저
                        new SafeGuardAdvisor(List.of("games")) // 보안 어드바이저
                )
                .build();
    }
}
