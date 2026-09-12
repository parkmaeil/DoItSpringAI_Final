package com.spring.ai.chapter07_2.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }
    public String chat(String query) {
        // RAG 파이프라인 모듈 조립
        var advisor = RetrievalAugmentationAdvisor.builder()
                // 1단계: 검색 전 단계
                .queryTransformers(
                        RewriteQueryTransformer.builder()
                                .chatClientBuilder(chatClient.mutate().clone())
                                .build(), // 명확한 질문으로 재작성
                        TranslationQueryTransformer.builder()
                                .chatClientBuilder(chatClient.mutate().clone())
                                .targetLanguage("english") // 영어로 번역 검색
                                .build()
                )
                .queryExpander(
                        MultiQueryExpander.builder()
                                .chatClientBuilder(chatClient.mutate().clone())
                                .numberOfQueries(3)
                                .build()  // 3개의 질문으로 확장
                )
                .documentRetriever(
                        VectorStoreDocumentRetriever.builder()
                                .vectorStore(vectorStore)
                                .topK(3)
                                .similarityThreshold(0.2)
                                .build()
                )
                // 3단계: 후처리 단계검색 후 단계
                .documentJoiner(new ConcatenationDocumentJoiner())
                .queryAugmenter(ContextualQueryAugmenter.builder().build())
                .build();

        return this.chatClient.prompt()
                .advisors(advisor)
                .user(query)
                .call()
                .content();
    }
}