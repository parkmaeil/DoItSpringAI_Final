package com.spring.ai.chapterb_3.service;

import com.spring.ai.chapterb_3.dto.RoutingDecision;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelRouterService {

    private final ChatClient chatClient;

    // 각 팀별 전문 프롬프트 정의
    private final Map<String, String> teamPrompts = Map.of(
        "RESERVATION", "당신은 예약 전문가입니다. 객실 타입, 가격, 취소 규정을 상세히 안내하세요.",
        "FACILITY", "당신은 시설 관리자입니다. 수영장, 헬스장, 주차장 이용 시간과 위치를 안내하세요.",
        "DINING", "당신은 셰프이자 식당 매니저입니다. 조식 시간, 메뉴 구성, 예약 방법을 친절히 안내하세요."
    );

    public String routeAndResolve(String userQuery) {
        // Step 1: 질문 분류 (Classification)
        log.info("Step 1: 질문 분류 중...");
        RoutingDecision decision = chatClient.prompt()
                .system("""
                    사용자의 질문을 분석하여 다음 팀 중 하나로 배정하세요: RESERVATION, FACILITY, DINING.
                    반드시 JSON 형식으로 응답하며 'selection'과 'reasoning' 필드를 포함하세요.
                    """)
                .user(userQuery)
                .call()
                .entity(RoutingDecision.class);

        log.info("분류 결과: {} (사유: {})", decision.selection(), decision.reasoning());

        // Step 2: 전문 프롬프트 적용 및 답변 생성 (Execution)
        String expertPrompt = teamPrompts.getOrDefault(decision.selection(), "당신은 호텔 직원입니다.");
        
        log.info("Step 2: 전문 에이전트 가동 중...");
        return chatClient.prompt()
                .system(expertPrompt)
                .user(userQuery)
                .call()
                .content();
    }
}