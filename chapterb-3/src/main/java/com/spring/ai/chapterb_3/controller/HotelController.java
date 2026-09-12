package com.spring.ai.chapterb_3.controller;

import com.spring.ai.chapterb_3.service.HotelRouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelController {

    private final HotelRouterService routerService;

    @GetMapping("/ask")
    public String ask(@RequestParam String q) {
        return routerService.routeAndResolve(q);
    }
}