package com.spring.ai.project_01.service;

import com.spring.ai.project_01.agent.CodeReviewParallelWorkflow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // 생성자 주입을 위한 롬복 애너테이션
public class PullRequestService {

    private final GithubService githubService;
    private final CodeReviewParallelWorkflow workflow;

    /**
     * PR 처리의 전체 흐름을 관장하는 메인 비즈니스 로직입니다.
     * 
     * @param repoOwner    레포지토리 주인 (교수님 ID)
     * @param repoName     레포지토리 이름 (과제명)
     * @param prNumber     PR 번호
     * @param studentName  학생 ID
     * @param solutionCode 정답 코드
     */
    public void processPullRequest(String repoOwner, String repoName, int prNumber, 
                                   String studentName, String solutionCode) {
        
        // [단계 1] GitHub API 호출: 학생이 수정한 코드 변경 내역(Diff) 가져오기
        System.out.println("🔍 [Service] GitHub에서 코드 변경 내역을 가져오는 중...");
        String diff = githubService.getPrDiff(repoOwner, repoName, prNumber);

        // [단계 2] AI 워크플로우 실행: 리뷰 에이전트와 채점 에이전트에게 작업 위임
        System.out.println("🤖 [Service] AI 에이전트 워크플로우 가동...");
        String finalComment = workflow.execute(diff, solutionCode, prNumber, studentName, repoName);

        // [단계 3] 결과 피드백: AI가 작성한 최종 리뷰를 GitHub PR 댓글로 등록
        System.out.println("📝 [Service] GitHub에 최종 리뷰 댓글을 등록하는 중...");
        githubService.commentOnPr(repoOwner, repoName, prNumber, finalComment);

        System.out.println("✅ [Service] 모든 처리가 완료되었습니다.");
    }
}