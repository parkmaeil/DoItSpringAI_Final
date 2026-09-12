package com.spring.ai.project_01.agent;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class CodeReviewParallelWorkflow {

    private final ReviewAgent reviewAgent;
    private final GradingAgent gradingAgent;

    public CodeReviewParallelWorkflow(ReviewAgent reviewAgent, GradingAgent gradingAgent) {
        this.reviewAgent = reviewAgent;
        this.gradingAgent = gradingAgent;
    }

    public String execute(String diff, String solutionCode, int prNumber, String studentName, String repoName) {
        long startTime = System.currentTimeMillis(); // 성능 측정 시작

        // [단계 1] 리뷰 에이전트 실행 (비동기 - 별도 스레드에서 시작)
        CompletableFuture<String> reviewFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("📝 [Async] 리뷰 에이전트가 분석을 시작했습니다...");
            return reviewAgent.generateFeedback(diff, solutionCode);
        });

        // [단계 2] 채점 에이전트 실행 (비동기 - 또 다른 스레드에서 시작)
        CompletableFuture<String> gradingFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("⚖️ [Async] 채점 에이전트가 채점 중입니다...");
            return gradingAgent.gradeAndSave(diff, solutionCode, prNumber, studentName, repoName);
        });

        // [단계 3] 두 작업이 모두 끝날 때까지 대기 (Join)
        // 두 에이전트 중 더 늦게 끝나는 작업에 맞춰 기다립니다.
        CompletableFuture.allOf(reviewFuture, gradingFuture).join();

        // [단계 4] 결과 데이터 취합
        String reviewResult = reviewFuture.join();   // 생성된 리뷰 텍스트
        String gradingLog = gradingFuture.join();    // 채점 및 DB 저장 로그
        
        long endTime = System.currentTimeMillis();
        System.out.println("⏱️ [Performance] 전체 처리 시간: " + (endTime - startTime) + "ms");
        System.out.println("🔍 [System Log] " + gradingLog);

        // [단계 5] 최종 리턴: GitHub 댓글에는 독자(학생)를 위한 '리뷰 내용'만 반환
        return String.format("""
                ## 🤖 AI 코드 리뷰 도착!
                %s
                """, reviewResult);
    }
}