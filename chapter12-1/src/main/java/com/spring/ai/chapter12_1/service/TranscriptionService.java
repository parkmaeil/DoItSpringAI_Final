package com.spring.ai.chapter12_1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranscriptionService {

    private final TranscriptionModel transcriptionModel;
    @Autowired
    private  ChatClient chatClient;

    public String transcribeMeeting(Resource audioFile) {
        // 한국어 인식률 최적화를 위한 상세 옵션 설정
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .model("whisper-1")
                // ① 언어 코드(ISO-639-1) 'ko'를 명시하여 인식 속도와 정확도를 향상시킵니다.
                .language("ko")
                // 핵심: 모델에게 도메인 지식(전문 용어, 올바른 맞춤법)을 미리 전달합니다.
                // .prompt("회의록입니다. SNS, 클릭률, 숏폼, 틱톡, 릴스, 기획안, 팀장님과 같은 단어를 정확하게 표기해 주세요.")
                // ② 무작위성을 0으로 설정하여 일관되고 정확한 텍스트 결과를 유도합니다.
                .temperature(0f)
                .build();

        // 모델 호출 및 결과 반환
        return transcriptionModel.transcribe(audioFile, options);
    }

    public String getRefinedMeetingMinutes(String rawText) {
        return chatClient.prompt()
                .system("""
                너는 전문 속기사야. 다음 지침을 엄격히 따라줘:
                1. 음성 인식 결과의 오타와 문맥을 교정한다.           
                2. 어떠한 설명이나 '수정안 드립니다' 같은 인사말도 하지 마라.
                3. 오직 교정된 최종 결과 텍스트만 반환하라.
                """)
                .user("다음 텍스트를 문맥에 맞게 수정해줘: " + rawText)
                .call()
                .content();
    }
}
