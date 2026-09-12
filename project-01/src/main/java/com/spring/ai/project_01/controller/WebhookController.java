package com.spring.ai.project_01.controller;

import com.spring.ai.project_01.service.PullRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WebhookController {

    private final PullRequestService pullRequestService;

    // 실습용 정답 코드 (추후 DB 연동 시 관리자 기능으로 대체 가능)
    private static final String SOLUTION_CODE = """
            public int add(int a, int b) {
                return a + b;
            }
            """;

    public WebhookController(PullRequestService pullRequestService) {
        this.pullRequestService = pullRequestService;
    }

    @PostMapping("/webhook")
    public void handleGithubEvent(
            @RequestHeader(value = "X-GitHub-Event", defaultValue = "unknown") String eventType,
            @RequestBody Map<String, Object> payload) {

        // [단계 1] 이벤트 유형 필터링: PR 이벤트가 아니면 즉시 종료
        if (!"pull_request".equals(eventType)) {
            return;
        }

        // [단계 2] 액션 필터링: 과제 제출(opened) 또는 코드 수정(synchronize)만 처리
        String action = (String) payload.get("action");
        if (!"opened".equals(action) && !"synchronize".equals(action)) {
            return;
        }

        System.out.println("🚀 [Webhook] Pull Request 감지! 분석을 시작합니다...");

        try {
            // [단계 3] 데이터 안전하게 추출 (Parsing)
            Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");
            Map<String, Object> repo = (Map<String, Object>) payload.get("repository");

            if (pr == null || repo == null) return;

            // 핵심 정보 추출: PR 번호, 학생 ID, 리포지토리명, 교수님(주인) ID
            int prNumber = (Integer) pr.get("number");
            // pull_request.user.login → 학생 ID
            String studentName = (String) ((Map<String, Object>) pr.get("user")).get("login");
            // repository.name → 레포 이름
            String repoName = (String) repo.get("name");
            // repository.owner.login → 교수자(레포 주인) ID
            String repoOwner = (String) ((Map<String, Object>) repo.get("owner")).get("login");

            System.out.printf("🔔 [Info] 과제: %s / 학생: %s / PR 번호: #%d\n", repoName, studentName, prNumber);

            // [단계 4] 핵심 로직 위임: 서비스 계층 호출
            pullRequestService.processPullRequest(
                    repoOwner, // 레포 주인 (교수자 GitHub ID)
                    repoName,  // 레포지토리 이름
                    prNumber, // PR 번호
                    studentName,  // 학생 GitHub ID
                    SOLUTION_CODE // 정답 코드
            );

        } catch (Exception e) {
            System.err.println("❌ [Error] 데이터 파싱 중 오류 발생: " + e.getMessage());
        }
    }
}