package com.spring.ai.project_01.controller;

import com.spring.ai.project_01.entity.AssignmentScore;
import com.spring.ai.project_01.repository.ScoreRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scores") // 모든 요청은 /api/scores로 시작합니다.
public class ScoreController {

    private final ScoreRepository scoreRepository;

    // 생성자 주입을 통해 Repository를 가져옵니다.
    public ScoreController(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    /**
     * 특정 학생의 전체 성적 이력을 조회합니다.
     * 예시 URL: GET http://localhost:8081/api/scores/parkmaeil
     */
    @GetMapping("/{studentName}")
    public ResponseEntity<List<AssignmentScore>> getStudentScores(@PathVariable String studentName) {
        System.out.println("🔍 성적 조회 요청 수신: " + studentName);

        // 1. Repository에서 해당 학생의 데이터를 최신순으로 가져옵니다.
        List<AssignmentScore> scores = scoreRepository.findByStudentNameOrderByGradedAtDesc(studentName);

        // 2. 만약 조회된 결과가 없다면? 
        // 204 No Content를 반환하여 데이터가 없음을 명확히 알립니다.
        if (scores.isEmpty()) {
            System.out.println("⚠️ 기록을 찾을 수 없습니다: " + studentName);
            return ResponseEntity.noContent().build();
        }

        // 3. 데이터가 존재하면 200 OK와 함께 JSON 리스트를 반환합니다.
        return ResponseEntity.ok(scores);
    }
}