package com.spring.ai.chapter11_1.service;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageGenerationService {

    private final ImageModel imageModel;
    private final RestTemplate restTemplate = new RestTemplate();
    // 스프링 AI가 OpenAiImageModel을 자동으로 주입합니다.
    public ImageGenerationService(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    public String generateImage(String description, String quality) {
        // 옵션 구성
        OpenAiImageOptions options = OpenAiImageOptions.builder()
                .model("dall-e-3")      // 사용할 모델명
                .quality(quality)       // (옵션 설명)
                .style("vivid")         // 이미지 화풍
                .N(1)                   // 생성할 이미지 개수
                .width(1024)            // 이미지 너비
                .height(1024)           // 이미지 높이
                .responseFormat("url")  // 결과 형식
                .build();

        // 프롬프트 생성
        ImagePrompt prompt = new ImagePrompt(description, options);
        // 모델 호출
        ImageResponse response = imageModel.call(prompt);
        // URL 추출
        return response.getResult().getOutput().getUrl();
    }
    // 외부 URL의 이미지를 바이트 배열로 내려받기(409 오류 방지용 URI 사용)
    public byte[] downloadImageBytes(String imageUrl) {
        try {
            return restTemplate.getForObject(java.net.URI.create(imageUrl),
                    byte[].class);
        } catch (Exception e) {
            throw new RuntimeException("이미지 내려받기 실패: " + e.getMessage());
        }
    }
    // (...생략...)
    // 서버 로컬 디스크의 'outputs' 폴더에 이미지 영구 저장
    public String saveImageToLocal(byte[] imageBytes) {
        String directoryPath = "outputs";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // 폴더 자동 생성
        }
        // UUID를 사용하여 파일명 중복을 원천 차단합니다.
        String fileName = "ai-gen-" + UUID.randomUUID() + ".png";
        File targetFile = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(imageBytes);
            System.out.println("서버 저장 완료: " + targetFile.getAbsolutePath());
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("로컬 저장 중 오류 발생", e);
        }
    }
}
