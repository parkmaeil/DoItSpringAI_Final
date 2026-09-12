package com.spring.ai.chapterb_2.service;

import com.spring.ai.chapterb_2.dto.ReviewResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParallelReviewService {

    private final ChatClient chatClient;
    // 한 번에 3개의 API 호출을 동시에 수행할 스레드 풀 생성
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public List<ReviewResult> analyzeProduct(String productInfo) {
        // 분석할 전문가 페르소나 리스트
        List<String> personas = List.of("IT 기술 전문가", "경제 전문가", "산업 디자이너");

        log.info("병렬 분석 시작: {}개 분야", personas.size());

        // 각 페르소나별로 비동기 작업(CompletableFuture) 생성
        List<CompletableFuture<ReviewResult>> futures = personas.stream()
                .map(persona -> CompletableFuture.supplyAsync(() -> {
                    log.info("{} 분석 가동 중...", persona);
                    
                    String analysis = chatClient.prompt()
                            .system(String.format("당신은 %s입니다. 상품을 분석하되, 핵심만 3문장 이내로 짧게 요약해서 답변하세요.", persona))
                            .user(productInfo)
                            .call()
                            .content();
                    
                    return new ReviewResult(persona, analysis);
                }, executor))
                .toList();

        // 모든 비동기 작업이 완료될 때까지 대기하고 결과를 리스트로 수집
        List<ReviewResult> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        log.info("모든 병렬 분석 완료.");
        return results;
    }
}