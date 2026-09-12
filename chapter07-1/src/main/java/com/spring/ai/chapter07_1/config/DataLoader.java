package com.spring.ai.chapter07_1.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataLoader {
    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Value("classpath:data.txt")
    private Resource resource; // 적재할 파일 리소스

    // VectorStore 빈을 인터페이스 타입으로 자동 주입받음
    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;   // DB 중복 체크용

    public DataLoader(VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore; // MariaDBVectorStore가 주입됨
        this.jdbcClient = jdbcClient;
    }

    @PostConstruct
    public void init() {
        try {
            // 테이블이 비어있을 때만 실행해 중복 방지
            Integer count = jdbcClient.sql("SELECT COUNT(*) FROM vector_store")
                    .query(Integer.class).single();

            if (count == 0) {
                log.info("지식 베이스 구축을 시작합니다...");

                // 다음 단계에서 구현
                // 파일 읽기
                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        resource.getInputStream(), StandardCharsets.UTF_8))) {

                    List<Document> documents = br.lines()
                            .map(Document::new)
                            .collect(Collectors.toList());

                    // TokenTextSplitter의 규칙 설정
                    TokenTextSplitter splitter = new TokenTextSplitter(800, 200, 10, 5000, true);

                    // 분할 및 임베딩, 저장
                    for (Document doc : documents) {
                        List<Document> chunks = splitter.split(doc);
                        vectorStore.accept(chunks); // 벡터 변환 및 DB 저장 일괄 처리
                        log.info(chunks.size() + "개의 청크가 저장되었습니다.");
                        Thread.sleep(200);
                    }
                    log.info("지식 베이스 구축 완료.");
                }

            } else {
                log.info("데이터가 이미 존재합니다. (현재 지식: " + count + "개)");
            }
        } catch (Exception e) {
            log.error("데이터 로딩 중 오류 발생", e);
        }
    }
}

