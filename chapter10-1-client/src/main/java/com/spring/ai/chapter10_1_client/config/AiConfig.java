package com.spring.ai.chapter10_1_client.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ToolCallbackProvider tools) { // MCP 도구 자동 주입
        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultToolCallbacks(tools) // MCP 도구를 기본값으로 등록
                .build();
    }
}
