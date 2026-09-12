package com.spring.ai.chapter03_4.service;

import com.spring.ai.chapter03_4.entity.Tutorial;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
      private final ChatClient chatClient;
      public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
      }
      public String getResponse(String query) {
        return chatClient.prompt()
                .user(query)
                .call()
                .content();
      }

     public String chatWithSystemRole(String query) {
      return chatClient.prompt()
            // ① AI에게 스포츠 전문가라는 정체성과 답변 지침을 부여합니다.
            .system("스포츠 전문가로서 답해주세요.")
            .user(query)
            .call()
            .content();
     }

    public String simpleChat(String query) {
    Prompt prompt = new Prompt(query);
    return chatClient.prompt(prompt)
            .call()
            .content();
    }

    public Tutorial getEntity(String query){
        Prompt prompt = new Prompt(query);
        return chatClient.prompt(prompt)
                .call()
                .entity(Tutorial.class); // 핵심: 응답을 Tutorial 객체로 자동 변환
    }

    public List<String> getStringList(String query) {
        return chatClient.prompt()
                .user(query)
                .call()
                // List<String> 타입으로 변환하도록 명시합니다.
                .entity(new ParameterizedTypeReference<List<String>>() {});
    }

    public List<Tutorial> getTutorialList(String query) {
        return chatClient.prompt()
                .user(query)
                .call()
                // 생성한 Tutorial 객체의 리스트 타입으로 자동 변환합니다.
                .entity(new ParameterizedTypeReference<List<Tutorial>>() {});
    }

    public String getMetaData(String query) {
        Prompt prompt = new Prompt(query);
        var metaData=chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getMetadata();
        System.out.println(metaData);
        return metaData.toString();
    }

    public String getDetailedContent(String query) {
    Prompt prompt = new Prompt(query);
    var content = chatClient.prompt(prompt)
            .call()
            // ① 응답의 총집합체인 ChatResponse 객체를 얻습니다.
            .chatResponse()
            // ② 여러 응답 후보(Generation) 중 첫 번째 결과를 선택합니다.
            .getResult()
            // ③ 응답 결과 내에 담긴 AssistantMessage 객체를 가져옵니다.
            .getOutput()
            // ④ 메시지 객체에서 비로소 최종 응답 텍스트를 추출합니다.
            .getText();

    System.out.println("Extracted Content: " + content);
    return content;
   }

    public String creativeChat(String query) {
        OpenAiChatOptions requestOptions = OpenAiChatOptions.builder()
                .model("gpt-4o-mini")
                .temperature(0.3)
                .build();
        Prompt prompt = new Prompt(query, requestOptions);
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    public String getPriorityTestResponse(String query) {
        // 가장 높은 우선순위: 0.1 (매우 일관된 답변 유도)
        OpenAiChatOptions requestOptions = OpenAiChatOptions.builder()
                .temperature(0.1)
                .build();

        return chatClient.prompt(new Prompt(query, requestOptions))
                .call()
                .content();
    }
}
