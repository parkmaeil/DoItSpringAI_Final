package com.spring.ai.chapterb_4.controller;

import com.spring.ai.chapterb_4.dto.FinalItinerary;
import com.spring.ai.chapterb_4.service.TravelAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelController {

    private final TravelAgentService travelService;

    @PostMapping("/plan") // GetMapping에서 PostMapping으로 변경
    public FinalItinerary getPlan(@RequestBody TravelRequest request) {
        // request 객체에서 필요한 필드들을 합쳐서 서비스에 전달
        String fullQuery = String.format("목적지: %s, 인원: %s, 컨셉: %s, 추가요구: %s",
                request.destination(), request.travelers(), request.concept(), request.remarks());
        return travelService.planTrip(fullQuery);
    }
    // 입력용 DTO 추가
    public record TravelRequest(String destination, String travelers, String concept, String remarks) {}
}