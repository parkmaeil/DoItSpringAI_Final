package com.spring.ai.chapter11_1.view;

import com.spring.ai.chapter11_1.service.VisionService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Route("vision") // http://localhost:8081/vision으로 접속
public class VisionView extends VerticalLayout {

    private final VisionService visionService;
    private final Image preview = new Image(); // 이미지 미리보기 컴포넌트
    private final TextArea resultArea = new TextArea("AI 분석 결과");

    public VisionView(VisionService visionService) {
        this.visionService = visionService;
        
        // 1. 파일 업로드 컴포넌트 설정 (메모리 버퍼 사용)
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        
        // 2. 업로드 성공 시 이벤트 처리
        upload.addSucceededListener(event -> {
            try {
                byte[] bytes = buffer.getInputStream().readAllBytes();
                
                // 화면에 업로드한 이미지 미리보기 표시
                StreamResource resource = new StreamResource("temp.png", () -> new ByteArrayInputStream(bytes));
                preview.setSrc(resource);
                preview.setWidth("400px");

                // 3. AI 비전 분석 서비스 호출
                String analysis = visionService.analyzeImage(bytes, "이 이미지에 무엇이 있는지 아주 상세하게 설명해줘.");
                resultArea.setValue(analysis);
                
            } catch (IOException e) {
                Notification.show("파일 처리 중 오류 발생!");
            }
        });

        // UI 배치 및 스타일 설정
        resultArea.setWidthFull();
        resultArea.setHeight("250px");
        setAlignItems(Alignment.CENTER);
        
        add(new H2("Spring AI 비전 분석기"), upload, preview, resultArea);
    }
}