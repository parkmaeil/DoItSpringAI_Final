package com.spring.ai.chapterb_3.dto;

/**
 * selection: 선택된 팀 이름 (RESERVATION, FACILITY, DINING)
 * reasoning: 해당 팀을 선택한 이유
 */
public record RoutingDecision(String selection, String reasoning) {}