package com.spring.ai.chapter05_1.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
public class ChatService {

    @Value("classpath:/prompts/system-message.st")
    private Resource systemMessage;
    @Value("classpath:/prompts/user-message.st")
    private Resource userMessage;

    private final ChatClient chatClient;
    public ChatService(ChatClient chatClient){
        this.chatClient=chatClient;
    }

    public String chat(String query){
        return this.chatClient
                .prompt()
                .system(system->system.text(this.systemMessage))
                .user(user->user.text(this.userMessage)
                        .param("concept", query))
                .call()
                .content();
    }

    public Flux<String> streamChat(String query) {
        return this.chatClient.prompt()
                .system(system -> system.text(this.systemMessage))
                .user(user -> user.text(this.userMessage).param("concept", query))
                .stream()
                .content();
    }
}
