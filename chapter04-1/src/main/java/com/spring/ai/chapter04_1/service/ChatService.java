package com.spring.ai.chapter04_1.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Value("classpath:prompts/system-message.st")
    private Resource systemMessageResource;
    @Value("classpath:prompts/user-message.st")
    private Resource userMessageResource;

    private final ChatClient chatClient;

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getExpertResponse(String query) {
        String queryStrTemplate = "당신은 코딩 및 프로그래밍 전문가입니다. " +
                "항상 자바로 프로그램을 작성하세요. " +
                "이제 이 질문에 답변하세요: {query}";
        return chatClient.prompt()
                .user(u -> u.text(queryStrTemplate).param("query", query))
                .call()
                .content();
    }

    public String getExplicitTemplateResponse(String subject, String example) {
        PromptTemplate strTemplate = PromptTemplate.builder()
                .template("{subject} 주제에서, {example} 예시를 들어주세요.")
                .build();
        String renderedMessage = strTemplate.render(Map.of("subject", subject, "example", example));
        Prompt prompt = new Prompt(renderedMessage);
        return this.chatClient.prompt(prompt).call().content();
    }

    public String getRoleBasedTemplateResponse(String subject, String example) {
        String systemText
                = "당신은 {subject} 전문가입니다. 항상 전문적인 관점에서 답변해 주세요.";
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemText);
        Message systemMessage = systemTemplate.createMessage(Map.of("subject", subject));

        String userText = "그것과 관련하여 {example}을(를) 예제와 함께 설명해주세요.";
        PromptTemplate userTemplate = new PromptTemplate(userText);
        Message userMessage = userTemplate.createMessage(Map.of("example", example));

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return this.chatClient.prompt(prompt).call().content();
    }

    public String getExternalTemplateResponse(String concept) {
        return chatClient.prompt()
                .system(systemMessageResource)
                .user(u -> u.text(userMessageResource)
                        .param("concept", concept)
                )
                .call()
                .content();
    }
}
