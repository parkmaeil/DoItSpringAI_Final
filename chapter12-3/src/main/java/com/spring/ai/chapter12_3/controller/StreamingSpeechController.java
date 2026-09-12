package com.spring.ai.chapter12_3.controller;

import com.spring.ai.chapter12_3.service.StreamingSpeechService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/speech")
@RequiredArgsConstructor
public class StreamingSpeechController {

    private final StreamingSpeechService streamingService;

    // 브라우저 테스트를 위해 GET 방식으로 매핑합니다.
    // GET 방식은 브라우저 주소창에 직접 URL을 쳐서 테스트하기에 매우 편리하며,
    // 브라우저가 스스로 <audio> 플레이어를 띄워 실시간 재생을 시도하도록 유도합니다.
    @GetMapping(value = "/stream", produces = "audio/mpeg")
    public ResponseEntity<Flux<byte[]>> stream(@RequestParam String text,
                                               @RequestParam(defaultValue = "nova") String voice) {
        
        // ① 리액티브 스트림(Flux) 획득
        Flux<byte[]> flux = streamingService.streamSpeechWithOptions(text, voice); 

        // ② 응답 헤더 설정: 'audio/mpeg'를 통해 오디오 스트리밍임을 명시
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(flux);
    }
}