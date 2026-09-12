package com.spring.ai.chapterb_2.controller;

import com.spring.ai.chapterb_2.dto.ReviewResult;
import com.spring.ai.chapterb_2.service.ParallelReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ParallelReviewService reviewService;

    @PostMapping("/analyze")
    public List<ReviewResult> analyze(@RequestBody String productDescription) {
        return reviewService.analyzeProduct(productDescription);
    }
}