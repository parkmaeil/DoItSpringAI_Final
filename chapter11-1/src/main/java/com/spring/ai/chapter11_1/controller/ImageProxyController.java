package com.spring.ai.chapter11_1.controller;

import com.spring.ai.chapter11_1.service.ImageGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ImageProxyController {

    private final ImageGenerationService imageService;

    @PostMapping("/download")
    public ResponseEntity<Resource> serveImage(@RequestBody Map<String, String> payload) {
        String url = payload.get("url");

        // 외부 URL에서 이미지 바이트 내려받기
        byte[] imageBytes = imageService.downloadImageBytes(url);

        // 서버 로컬 디스크에 자동 저장
        String savedFileName = imageService.saveImageToLocal(imageBytes);

        // 클라이언트에 반환할 리소스 생성
        ByteArrayResource resource = new ByteArrayResource(imageBytes);

        // 응답 헤더에 파일 이름을 포함해, 브라우저가 해당 파일을 내려받도록 설정
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(imageBytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + savedFileName + "\"")
                .body(resource);
    }
}

