package com.learning.btmlearning.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.btmlearning.constant.Difficulty;
import com.learning.btmlearning.constant.QuestionType;
import com.learning.btmlearning.dto.request.AiQuizGenerateRequest;
import com.learning.btmlearning.dto.response.AiQuizGenerateResponse;
import com.learning.btmlearning.dto.response.OpenAiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiQuizGenerationService {
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    public AiQuizGenerateResponse generate(AiQuizGenerateRequest request) {
        Difficulty defaultDifficulty = request.getDifficulty() == null ? Difficulty.MEDIUM : request.getDifficulty();
        QuestionType defaultType = request.getQuestionType() == null ? QuestionType.SINGLE_CHOICE : request.getQuestionType();

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(systemMessage());
        messages.add(userMessage(buildPrompt(request, defaultDifficulty, defaultType)));

        OpenAiChatResponse aiResponse = openAiService.chat(messages);
        if (aiResponse == null || aiResponse.getChoices() == null || aiResponse.getChoices().isEmpty()) {
            throw new RuntimeException("AI service returned empty response");
        }

        String aiContent = aiResponse.getChoices().get(0).getMessage().getContent();

        return parseAiResponse(aiContent, request, defaultDifficulty, defaultType);
    }

    private AiQuizGenerateResponse parseAiResponse(
            String aiContent,
            AiQuizGenerateRequest request,
            Difficulty defaultDifficulty,
            QuestionType defaultType
    ) {
        try {
            String json = extractJson(aiContent);
            JsonNode root = objectMapper.readTree(json);
            JsonNode questionsNode = root.path("questions");

            if (!questionsNode.isArray() || questionsNode.isEmpty()) {
                throw new IllegalStateException("AI response does not contain valid questions");
            }

            List<AiQuizGenerateResponse.GeneratedQuestion> questions = new ArrayList<>();
            int expectedCount = request.getQuestionCount() == null ? 5 : request.getQuestionCount();

            for (JsonNode node : questionsNode) {
                if (questions.size() >= expectedCount) {
                    break;
                }

                String content = node.path("content").asText("").trim();
                if (content.isEmpty()) {
                    continue;
                }

                Difficulty difficulty = parseDifficulty(node.path("difficulty").asText(null), defaultDifficulty);
                QuestionType questionType = parseQuestionType(node.path("questionType").asText(null), defaultType);

                List<AiQuizGenerateResponse.GeneratedAnswer> answers = new ArrayList<>();
                JsonNode answersNode = node.path("answers");

                if (answersNode.isArray()) {
                    for (JsonNode answerNode : answersNode) {
                        String answerContent = answerNode.path("content").asText("").trim();
                        if (answerContent.isEmpty()) {
                            continue;
                        }

                        String explanation = request.getIncludeExplanations() == Boolean.FALSE
                                ? null
                                : answerNode.path("explanation").asText(null);

                        String referenceAnswer = answerNode.path("referenceAnswer").asText(null);

                        answers.add(AiQuizGenerateResponse.GeneratedAnswer.builder()
                                .content(answerContent)
                                .correct(answerNode.path("correct").asBoolean(false))
                                .explanation(explanation)
                                .referenceAnswer(referenceAnswer)
                                .build());
                    }
                }

                if (answers.isEmpty() && (questionType == QuestionType.ESSAY || questionType == QuestionType.SHORT_ANSWER)) {
                    answers.add(AiQuizGenerateResponse.GeneratedAnswer.builder()
                            .content("Mở rộng câu trả lời theo lập luận cá nhân")
                            .correct(true)
                            .referenceAnswer("Nêu được các ý chính liên quan đến chủ đề")
                            .build());
                }

                questions.add(AiQuizGenerateResponse.GeneratedQuestion.builder()
                        .content(content)
                        .difficulty(difficulty)
                        .questionType(questionType)
                        .answers(answers)
                        .build());
            }

            if (questions.isEmpty()) {
                throw new IllegalStateException("AI response could not be parsed into usable questions");
            }

            String title = root.path("title").asText("").trim();
            if (title.isEmpty()) {
                title = "AI Quiz - " + request.getTopic();
            }

            return AiQuizGenerateResponse.builder()
                    .title(title)
                    .questions(questions)
                    .build();
        } catch (Exception ex) {
            log.error(">>> Failed to parse AI quiz generation response", ex);
            throw new RuntimeException("AI response format is invalid. Please try again.");
        }
    }

    private String buildPrompt(AiQuizGenerateRequest request, Difficulty defaultDifficulty, QuestionType defaultType) {
        String context = request.getContext() == null ? "" : request.getContext().trim();
        int questionCount = request.getQuestionCount() == null ? 5 : request.getQuestionCount();

        return "Generate a quiz in strict JSON format only (no markdown, no explanation text).\n"
                + "Topic: " + request.getTopic() + "\n"
                + "Context: " + (context.isEmpty() ? "N/A" : context) + "\n"
                + "Question count: " + questionCount + "\n"
                + "Default difficulty: " + defaultDifficulty.name() + "\n"
                + "Default question type: " + defaultType.name() + "\n"
                + "Output schema:\n"
                + "{\n"
                + "  \"title\": \"string\",\n"
                + "  \"questions\": [\n"
                + "    {\n"
                + "      \"content\": \"string\",\n"
                + "      \"difficulty\": \"EASY|MEDIUM|HARD\",\n"
                + "      \"questionType\": \"SINGLE_CHOICE|MULTIPLE_CHOICE|TRUE_FALSE|SHORT_ANSWER|ESSAY\",\n"
                + "      \"answers\": [\n"
                + "        {\n"
                + "          \"content\": \"string\",\n"
                + "          \"correct\": true|false,\n"
                + "          \"explanation\": \"string\",\n"
                + "          \"referenceAnswer\": \"string\"\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
                + "}\n"
                + "Rules:"
                + "\n- For SINGLE_CHOICE and TRUE_FALSE: exactly 1 correct answer."
                + "\n- For MULTIPLE_CHOICE: at least 2 correct answers."
                + "\n- For ESSAY/SHORT_ANSWER: provide one answer with referenceAnswer."
                + "\n- Keep language in Vietnamese."
                + "\nReturn JSON only.";
    }

    private Map<String, String> systemMessage() {
        Map<String, String> message = new HashMap<>();
        message.put("role", "system");
        message.put("content", "You are an expert instructional designer that outputs valid JSON only.");
        return message;
    }

    private Map<String, String> userMessage(String prompt) {
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        return message;
    }

    private String extractJson(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalStateException("Empty AI content");
        }

        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            int firstLineEnd = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstLineEnd > -1 && lastFence > firstLineEnd) {
                return trimmed.substring(firstLineEnd + 1, lastFence).trim();
            }
        }

        int objectStart = trimmed.indexOf('{');
        int objectEnd = trimmed.lastIndexOf('}');
        if (objectStart >= 0 && objectEnd > objectStart) {
            return trimmed.substring(objectStart, objectEnd + 1);
        }

        return trimmed;
    }

    private Difficulty parseDifficulty(String raw, Difficulty fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }

        try {
            return Difficulty.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private QuestionType parseQuestionType(String raw, QuestionType fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }

        try {
            return QuestionType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return fallback;
        }
    }
}
