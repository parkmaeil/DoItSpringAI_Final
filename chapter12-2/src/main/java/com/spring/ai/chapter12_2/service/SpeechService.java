package com.spring.ai.chapter12_2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpeechService {

    private final TextToSpeechModel textToSpeechModel;

    // ① 기본 옵션으로 MP3 생성 (간편 호출)
    public byte[] synthesize(String text) {
        return textToSpeechModel.call(text);
    }

    // ② 상세 옵션을 적용한 커스텀 음성 생성
    public byte[] synthesizeCustom(String text, String voice, double speed) {
        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .model("gpt-4o-mini-tts")
                // 문자열로 받은 voice 값을 오픈AI API의 Enum 상수로 변환합니다.
                .voice(OpenAiAudioApi.SpeechRequest.Voice.valueOf(voice.toUpperCase()))
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(speed)
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(text, options);
        TextToSpeechResponse response = textToSpeechModel.call(prompt);
        
        return response.getResult().getOutput();
    }
}