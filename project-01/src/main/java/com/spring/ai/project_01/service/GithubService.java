package com.spring.ai.project_01.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class GithubService {

    private final RestClient restClient;

    // 생성자에서 GitHub 토큰을 주입받아 RestClient를 초기화합니다.
    public GithubService(@Value("${github.token}") String token) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                // 모든 요청에 'Bearer [토큰]' 인증 헤더를 자동으로 붙여줍니다.
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    /**
     * 1. PR의 변경된 코드(Diff) 가져오기
     * AI가 분석할 수 있도록 학생이 수정한 코드 내역만 쏙 뽑아옵니다.
     */
    public String getPrDiff(String owner, String repo, int prNumber) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/pulls/{prNumber}", owner, repo, prNumber)
                // [핵심] JSON이 아닌 'Diff' 형식으로 데이터를 달라고 요청합니다.
                .header("Accept", "application/vnd.github.v3.diff")
                .retrieve()
                .body(String.class);
    }

    /**
     * 2. PR에 AI 리뷰 댓글 달기
     * AI 조교가 작성한 최종 피드백을 학생의 PR 페이지에 등록합니다.
     */
    public void commentOnPr(String owner, String repo, int prNumber, String comment) {
        restClient.post()
                .uri("/repos/{owner}/{repo}/issues/{prNumber}/comments", owner, repo, prNumber)
                .header("Accept", "application/vnd.github+json")
                // GitHub이 요구하는 JSON 형식 {"body": "내용"}으로 포장하여 전송합니다.
                .body(Map.of("body", comment))
                .retrieve()
                .toBodilessEntity(); // 응답 본문은 생략하고 성공 여부만 확인합니다.
    }
}