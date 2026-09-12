package com.spring.ai.project_01.agent;

import com.spring.ai.project_01.tools.ScoreTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class GradingAgent {
    private final ChatClient chatClient;
    private final ScoreTools scoreTools;

    @Value("classpath:prompts/grading-rubric.txt")
    private Resource rubricResource;

    @Value("classpath:prompts/system-message.st")
    private Resource systemPromptResource;

    private static final String USER_PROMPT_TEXT = """
            [메타 정보]
            - 학생: {studentName} / 레포: {repoName} / PR: #{prNumber}
            
            [모범 답안]
            {referenceCode}
            
            [학생의 제출 코드 (Diff)]
            반드시 '+' 라인만 보고 평가하세요.
            {diff}
            """;

    public GradingAgent(ChatClient.Builder chatClient, ScoreTools scoreTools) {
        this.chatClient = chatClient.build();
        this.scoreTools = scoreTools;
    }

    public String gradeAndSave(String diff, String solutionCode, int prNumber, String studentName, String repoName) {
        // 1. 채점 기준표 및 시스템 메시지 준비
        String detailedRubric = loadResourceToString(rubricResource);
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemPromptResource);
        Message systemMessage = systemTemplate.createMessage(Map.of("detailedRubric", detailedRubric));

        // 2. 유저 메시지 준비
        PromptTemplate userTemplate = new PromptTemplate(USER_PROMPT_TEXT);
        Message userMessage = userTemplate.createMessage(Map.of(
                "studentName", studentName, "repoName", repoName,
                "prNumber", prNumber, "diff", diff, "referenceCode", solutionCode
        ));

        // 3. AI 호출 및 도구 실행
        return chatClient.prompt()
                .messages(systemMessage, userMessage)
                .tools(scoreTools) // AI에게 '저장 도구'를 건네줌
                .call()
                .content();
    }

    private String loadResourceToString(Resource resource) {
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("프롬프트 파일 로드 실패: " + resource.getFilename(), e);
        }
    }
}