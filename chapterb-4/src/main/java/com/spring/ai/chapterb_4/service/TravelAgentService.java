package com.spring.ai.chapterb_4.service;

import com.spring.ai.chapterb_4.dto.FinalItinerary;
import com.spring.ai.chapterb_4.dto.OrchestratorPlan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelAgentService {

    private final ChatClient chatClient;

    public FinalItinerary planTrip(String userRequest) {
        log.info("--- [Phase 1] 오케스트레이터: 전략 수립 ---");

        // Step 1: 오케스트레이터가 작업을 쪼개기 (Planning)
        OrchestratorPlan plan = chatClient.prompt()
                .system("""
                    당신은 여행 설계 팀장입니다. 사용자의 요청을 분석하고 즉시 실행 가능한 하위 작업을 정의하세요.
                    **[지침]**
                    1. 사용자와 대화(질문)를 시도하지 마세요. 정보가 부족해도 '가장 대중적인 기준'으로 즉시 계획을 세웁니다.
                    2. 모든 워커에게 "인사말 없이 본론만 핵심 위주로 3줄 이내 답변"하도록 description에 명시하세요.
                    3. 작업 결과는 반드시 JSON('analysis', 'tasks')으로 반환하세요.
                """)
                .user(userRequest)
                .call()
                .entity(OrchestratorPlan.class);

        log.info("팀장의 분석: {}", plan.analysis());
        log.info("정의된 하위 작업 개수: {}개", plan.tasks().size());

       // Step 2: 워커 실행 로직에 병렬성 추가 (작가님 주석대로 반영)
        List<String> workerResults = plan.tasks().parallelStream() // parallelStream으로 성능 극대화
                .map(task -> {
                    log.info("워커 가동 중: [{}] {}", task.type(), task.description());
                    return chatClient.prompt()
                            .system(String.format("""
                        당신은 %s 전문가입니다. 
                        지침: 인사말 생략, 3문장 이내 요약, 불필요한 예시 금지.
                        """, task.type()))
                            .user(task.description())
                            .call()
                            .content();
                })
                .toList();

        log.info("--- [Phase 3] 결과 합성 완료 ---");
        return new FinalItinerary(plan.analysis(), workerResults);
    }
}