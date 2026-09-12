package com.spring.ai.chapter12_1.controller;

import com.spring.ai.chapter12_1.service.TranscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/transcription")
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService transcriptionService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAudio(@RequestParam("file") MultipartFile file) {

        // ① file.getResource()는 웹으로 들어온 파일을 스프링 AI가 이해할 수 있는
        // Resource 타입으로 변환하는 가장 간단하고 효율적인 방법입니다.
        String resultText = transcriptionService.transcribeMeeting(file.getResource());
        String finalText=transcriptionService.getRefinedMeetingMinutes(resultText);
        return ResponseEntity.ok(Map.of("text", finalText));
    }
}

