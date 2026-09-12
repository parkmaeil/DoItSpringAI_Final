package com.spring.ai.chapter07_1.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final ChatClient chatClient;
   private final VectorStore vectorStore; // 자동 구성된 VectorStore 인터페이스 주입

    @Value("classpath:/prompts/system-message.st")
    private Resource systemMessage;


    public ChatService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore; // MariaDBVectorStore 객체 할당
    }


    public String chat(String query) {
        // 1단계: 검색 조건 설정하기
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(3)
                .similarityThreshold(0.2)
                .query(query)
                .build();

        // 2단계: 유사한 문서 목록 검색하기
        List<Document> documents = this.vectorStore.similaritySearch(searchRequest);

        // 3단계: 데이터 가공
        List<String> contextList = documents.stream()
                .map(Document::getText)
                .toList();

        String contextData = String.join("\n", contextList);

        // 4단계: 최종 답변 생성
        return this.chatClient
                .prompt()
                .system(system -> system.text(this.systemMessage)
                        .param("documents", contextData))
                .user(query)
                .call()
                .content();
    }

    // 08-1  [Do it! 실습] QuestionAnswerAdvisor 적용하기
   /*  public ChatService(ChatClient chatClient) {
    this.chatClient = chatClient;
  }
    public String chat(String query) {
        return this.chatClient.prompt()
                .user(query)
                .call().content();
    }*/
}
