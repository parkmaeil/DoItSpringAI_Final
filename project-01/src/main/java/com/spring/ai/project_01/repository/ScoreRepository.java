package com.spring.ai.project_01.repository;

import com.spring.ai.project_01.entity.AssignmentScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRepository extends JpaRepository<AssignmentScore, Long> {
    // 특정 학생의 기록을 최신순으로 조회
    List<AssignmentScore> findByStudentNameOrderByGradedAtDesc(String studentName);
}