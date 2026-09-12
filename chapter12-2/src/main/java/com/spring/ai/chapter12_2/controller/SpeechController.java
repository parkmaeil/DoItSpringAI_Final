package com.spring.ai.chapter12_2.controller;

import com.spring.ai.chapter12_2.service.SpeechService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/speech")
@RequiredArgsConstructor
public class SpeechController {

    private final SpeechService speechService;

    @PostMapping(value = "/synthesize", produces = "audio/mpeg")
    public ResponseEntity<byte[]> synthesize(@RequestBody SynthesisRequest request) {
        
        // ① 비즈니스 로직 호출: 컨트롤러는 요청을 전달하고 응답을 포맷팅하는 데 집중합니다.
        byte[] audioData = speechService.synthesizeCustom(
                request.text(), 
                request.voice(), 
                request.speed()
        );

        // ② 응답 헤더 설정: 브라우저가 이 데이터를 '재생 가능한 파일'로 인식하게 만듭니다.
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"speech.mp3\"")
                .body(audioData);
    }

    public record SynthesisRequest(String text, String voice, double speed) {}
}