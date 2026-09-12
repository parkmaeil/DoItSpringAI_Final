package com.spring.ai.chapter08_1.service;

import com.spring.ai.chapter08_1.tools.SimpleDateTimeTool;
import com.spring.ai.chapter08_1.tools.WeatherTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private ChatClient chatClient;

    @Autowired
    private WeatherTool weatherTool;

    public ChatService(ChatClient chatClient){
        this.chatClient=chatClient;
    }

    public String chat(String query){
        return chatClient
                .prompt()
                .tools(new SimpleDateTimeTool(), weatherTool) // LLM 호출 시점에 도구 등록
                .user(query) 
                .call()
                .content();
    }
}
