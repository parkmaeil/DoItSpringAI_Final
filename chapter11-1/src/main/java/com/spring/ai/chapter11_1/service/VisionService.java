package com.spring.ai.chapter11_1.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
public class VisionService {

    private final ChatClient chatClient;

    public VisionService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String analyzeImage(byte[] imageBytes, String userPrompt) {
        // ① 바이트 데이터를 스프링 리소스로 변환
        // AI 모델은 추상화된 Resource 인터페이스를 통해 데이터에 접근합니다.
        // 메모리에 있는 byte[]를 ByteArrayResource로 감싸서 전달합니다.
        Resource imageResource = new ByteArrayResource(imageBytes);

        return chatClient.prompt()
                .user(u -> u.text(userPrompt) // 질문 텍스트 설정
                        // ② 멀티모달 데이터(이미지) 추가
                        // .media() 메서드는 데이터의 유형(MimeType)과 실제 데이터(Resource)를 인자로 받습니다.
                        .media(MimeTypeUtils.IMAGE_PNG, imageResource))
                .call()
                .content();
    }
}