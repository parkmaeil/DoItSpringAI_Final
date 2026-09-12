package com.spring.ai.chapter07_2.config;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

@Configuration
public class PdfDataLoader {
    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;
    @Value("classpath:data.pdf")
    private Resource pdfResource;

    public PdfDataLoader(VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }

    @PostConstruct
    public void init() throws Exception {
        // 중복 방지를 위한 데이터베이스 조회
        Integer count = jdbcClient.sql("select count(*) from pdf_store").query(Integer.class).single();
        if (count == null || count == 0) {
            // 추출(Extract)
            var pdfReader = new PagePdfDocumentReader(pdfResource,
                    PdfDocumentReaderConfig.builder().withPageTopMargin(0).build());
            List<Document> documents = pdfReader.read();

            // 변환(Transform)
            var splitter = new TokenTextSplitter(300, 400, 10, 5000, true);
            List<Document> transformedDocument = splitter.transform(documents);

            // 로드
            this.vectorStore.add(transformedDocument); // 임베딩 및 DB 저장 실행
            System.out.println("VectorStore에 PDF 적재 완료.");
        }
    }
}
