package com.spring.ai.chapterb_2.dto;

/**
 * persona: 분석가 전문가 (Tech, Economy, Design)
 * content: 분석 내용
 */
public record ReviewResult(String persona, String content) {}