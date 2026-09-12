package com.spring.ai.chapter10_1_client.service;

import com.spring.ai.chapter10_1_client.dto.Book;
import com.spring.ai.chapter10_1_client.dto.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final ChatClient chatClient;

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public List<Book> ask(Question question) {
        return chatClient.prompt()
            // [프롬프트 엔지니어링] 결과가 없을 때의 예외 상황을 LLM에게 명확히 지시합니다.
            .user(question.question() + " 만약 해당하는 책이 없다면 빈 배열인 []을 반환해주세요.")
            .call()
            // 응답을 List<Book>이라는 복합 객체 형태로 자동 파싱합니다.
            .entity(new ParameterizedTypeReference<List<Book>>() { });
    }
}