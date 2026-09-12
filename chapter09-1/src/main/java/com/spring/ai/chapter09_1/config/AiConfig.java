package com.spring.ai.chapter09_1.config;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AiConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 List<McpSyncClient> mcpSyncClients) {
        return builder
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}

