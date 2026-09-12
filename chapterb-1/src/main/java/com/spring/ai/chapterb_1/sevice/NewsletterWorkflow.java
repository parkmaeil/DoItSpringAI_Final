package com.spring.ai.chapterb_1.sevice;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsletterWorkflow {

    private final ChatClient chatClient;

    /**
     * 프롬프트 체이닝 워크플로우: 요약 -> 감성 분석 -> 제목 생성
     */
    public String generateNewsletter(String rawArticle) {
        log.info("Step 1: 기사 요약 시작...");
        
        // [Step 1] 요약 (Extraction)
        String summary = chatClient.prompt()
                .system("당신은 전문 편집자입니다. 제공된 기사를 3문장 이내로 핵심 요약하세요.")
                .user(rawArticle)
                .call()
                .content();
        log.info("요약 완료: {}", summary);

        log.info("Step 2: 감성 분석 시작...");
        
        // [Step 2] 감성 분석 (Analysis)
        String sentiment = chatClient.prompt()
                .system("당신은 심리 분석가입니다. 요약된 내용을 읽고 기사의 분위기를 [긍정], [부정], [중립] 중 하나로 분류하세요. 단어 하나만 대답하세요.")
                .user(summary)
                .call()
                .content();
        log.info("분석 완료: {}", sentiment);

        log.info("Step 3: 제목 생성 시작...");
        
        // [Step 3] 최종 결과물 생성 (Formatting)
        // 1단계의 요약과 2단계의 분석 결과를 조합하여 최종 프롬프트를 만듭니다.
        return chatClient.prompt()
                .system("당신은 마케팅 전문가입니다. 요약 내용과 분위기를 반영하여 클릭률이 높은 뉴스레터 제목을 하나만 생성하세요.")
                .user(String.format("기사 요약: %s\n분석된 분위기: %s", summary, sentiment))
                .call()
                .content();
    }
}