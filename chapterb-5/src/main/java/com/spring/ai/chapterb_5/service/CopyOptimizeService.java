package com.spring.ai.chapterb_5.service;

import com.spring.ai.chapterb_5.dto.EvalResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CopyOptimizeService {

    private final ChatClient chatClient;
    private static final int MAX_RETRIES = 3; // 최대 3번까지만 수정 요청

    public String optimizeCopy(String productInfo) {
        String currentCopy = "초안 생성 전";
        String feedback = "첫 번째 초안을 작성해 주세요.";
        
        for (int i = 1; i <= MAX_RETRIES; i++) {
            log.info("--- [시도 {}회차] 문구 생성 및 수정 중 ---", i);

            // 1. 생성 단계 (Generator): 피드백을 반영하여 문구 작성
            currentCopy = chatClient.prompt()
                    .system("당신은 전설적인 카피라이터입니다. 아래 피드백을 반영하여 최고의 광고 문구를 작성하세요.")
                    .user(String.format("상품정보: %s\n최근 피드백: %s", productInfo, feedback))
                    .call()
                    .content();

            log.info("생성된 문구: {}", currentCopy);

            // 2. 평가 단계 (Evaluator): 작성된 문구 비평
            EvalResult eval = chatClient.prompt()
                    .system("""
                        당신은 까다로운 마케팅 디렉터입니다. 광고 문구를 평가하세요.
                        - 기준: 20자 이내, 강력한 동사 사용, 혜택 명시.
                        - 모든 기준을 만족하면 status를 'PASS'로, 아니면 'IMPROVE'로 보내세요.
                        - 반드시 JSON 형식으로 응답하세요.
                        """)
                    .user(currentCopy)
                    .call()
                    .entity(EvalResult.class);

            if ("PASS".equals(eval.status())) {
                log.info("최종 승인 완료! (사유: {})", eval.feedback());
                return currentCopy;
            }

            feedback = eval.feedback();
            log.warn("수정 요청 발생: {}", feedback);
        }

        log.info("최대 수정 횟수에 도달하여 최종본을 반환합니다.");
        return currentCopy;
    }
}