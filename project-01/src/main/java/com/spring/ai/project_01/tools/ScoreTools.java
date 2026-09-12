package com.spring.ai.project_01.tools;

import com.spring.ai.project_01.dto.SaveScoreRequest;
import com.spring.ai.project_01.entity.AssignmentScore;
import com.spring.ai.project_01.repository.ScoreRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class ScoreTools {
    private final ScoreRepository scoreRepository;
    
    public ScoreTools(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @Tool(description = "채점 결과를 DB에 저장하는 도구입니다. 점수와 피드백을 반드시 포함해야 합니다.")
    public String saveScore(SaveScoreRequest request) {
        System.out.println("🛠️ [Tool] AI가 DB 저장을 요청했습니다: " + request.score() + "점");
        
        AssignmentScore entity = new AssignmentScore(
                request.studentName(),
                request.repoName(),
                request.prNumber(),
                request.score(),
                request.feedback()
        );
        
        scoreRepository.save(entity);
        return "✅ DB 저장 완료! (학생: " + request.studentName() + ")";
    }
}