package com.spring.ai.chapter12_3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class StreamingSpeechService {

    private final TextToSpeechModel textToSpeechModel;

    public Flux<byte[]> streamSpeechWithOptions(String text, String voice) {
         // 설정 파일에 이미 모델이 정의되어 있지만, 코드에서 다시 지정하는 이유는
        // 실행 시점에 '사용자 선택'에 따라 목소리나 속도를 동적으로 바꾸기 위해서입니다.
        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .model("gpt-4o-mini-tts") // 저지연 속도 최적화 모델 강제
                .voice(OpenAiAudioApi.SpeechRequest.Voice.valueOf(voice.toUpperCase()))
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0)
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(text, options);
        // stream()은 비동기 논블로킹 방식으로 동작합니다.
        // 전체 파일이 완성될 때까지 기다리지 않고 데이터가 준비되는 대로 흘려보냅니다.
        return textToSpeechModel.stream(prompt)
                .map(r -> r.getResult().getOutput()); // 응답 객체에서 순수 오디오 데이터만 추출
    }
}