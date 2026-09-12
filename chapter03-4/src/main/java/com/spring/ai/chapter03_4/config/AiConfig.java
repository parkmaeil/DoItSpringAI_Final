package com.spring.ai.chapter03_4.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

   /*
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        // 1. 이 ChatClient 빈의 기본 옵션을 정의합니다.
        OpenAiChatOptions defaultOptions = OpenAiChatOptions.builder()
                .model("gpt-5-mini") // 설정 파일의 gpt-4o-mini를 덮어씀
                .temperature(1.0) // 설정 파일의 0.8을 덮어씀
                .maxTokens(100)
                .build();

        // builder.build() 대신 .defaultOptions()를 추가
        return builder
                .defaultOptions(defaultOptions)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultOptions(OpenAiChatOptions.builder()
                        .temperature(0.5) // 전역 설정(0.8)을 무시하고 0.5로 변경
                        .build())
                .build();
    }
  */

}
