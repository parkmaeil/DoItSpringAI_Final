package com.spring.ai.chapterb_5.dto;

/**
 * status: PASS(합격), IMPROVE(수정 필요)
 * feedback: 수정해야 할 구체적인 이유와 지침
 */
public record EvalResult(String status, String feedback) {}