package com.spring.ai.chapterb_4.dto;

import java.util.List;

// 오케스트레이터의 분석 결과
public record OrchestratorPlan(String analysis, List<TravelSubTask> tasks) {}
