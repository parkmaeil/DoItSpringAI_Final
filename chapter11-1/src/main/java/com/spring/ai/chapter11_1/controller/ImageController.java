package com.spring.ai.chapter11_1.controller;

import com.spring.ai.chapter11_1.service.ImageGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageGenerationService imageService;

    public ImageController(ImageGenerationService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generate(@RequestBody ImageRequest request) {
        String url = imageService.generateImage(request.description(), request.quality());
        return ResponseEntity.ok(Map.of("imageUrl", url));
    }
}

// 요청 데이터를 담는 DTO
record ImageRequest(String description, String quality) {}

