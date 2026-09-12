package com.spring.ai.chapterb_1.controller;

import com.spring.ai.chapterb_1.sevice.NewsletterWorkflow;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
public class ChatController {

    private final NewsletterWorkflow newsletterWorkflow;

    @PostMapping("/generate")
    public String generate(@RequestBody String article) {
        // 워크플로우를 호출하여 체이닝된 결과를 반환합니다.
        return newsletterWorkflow.generateNewsletter(article);
    }
}