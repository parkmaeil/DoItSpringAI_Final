package com.spring.ai.project_01.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class AssignmentScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String studentName; // 학생의 GitHub ID
    private String repoName;    // 과제 레포지토리 이름
    private int prNumber;       // Pull Request 번호
    private int score;          // 최종 점수 (0~100)
    
    @Column(length = 1000)
    private String feedback;    // AI의 한 줄 요약 피드백
    
    private LocalDateTime gradedAt; // 채점 일시

    public AssignmentScore(String studentName, String repoName, int prNumber, int score, String feedback) {
        this.studentName = studentName;
        this.repoName = repoName;
        this.prNumber = prNumber;
        this.score = score;
        this.feedback = feedback;
        this.gradedAt = LocalDateTime.now();
    }
}