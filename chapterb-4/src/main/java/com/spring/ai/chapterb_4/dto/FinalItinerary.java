package com.spring.ai.chapterb_4.dto;

import java.util.List;

// 최종 사용자에게 전달될 통합 보고서
public record FinalItinerary(String analysis, List<String> workerdetails) {}
