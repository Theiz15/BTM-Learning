package com.learning.btmlearning.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.btmlearning.dto.response.OpenAiChatResponse;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.service.impl.OpenAiService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemPromptBuilder {
    OpenAiService openAiService;
    ObjectMapper objectMapper;

    public String build(Course course) {
        if (course != null) {
            return String.format(
                    "Bạn là trợ lý học tập AI chuyên nghiệp của nền tảng BTM-Learning. " +
                            "Nhiệm vụ của bạn là giải đáp thắc mắc cho học viên về khóa học '%s'. " +
                            "Hãy trả lời chính xác, ngắn gọn, thân thiện và hoàn toàn bằng TIẾNG VIỆT. " +
                            "Nếu học viên hỏi lạc đề khỏi lập trình hoặc nội dung khóa học, hãy khéo léo từ chối.",
                    course.getTitle()
            );
        }

        return "Bạn là trợ lý học tập AI của nền tảng BTM-Learning. " +
                "Hãy trả lời các câu hỏi của học viên một cách ngắn gọn, súc tích và hoàn toàn bằng TIẾNG VIỆT.";
    }

    public List<Long> getAiSuggestedCategories(List<Long> learnedCats) {
        String prompt = String.format(
                "User đã học các Category IDs: %s. " +
                        "Dựa trên tư duy lộ trình học IT, hãy gợi ý 3 Category IDs tiếp theo phù hợp nhất. " +
                        "CHỈ TRẢ VỀ ĐÚNG ĐỊNH DẠNG JSON SAU, không giải thích gì thêm: {\"categoryIds\": [id1, id2, id3]}",
                learnedCats.toString()
        );

        Map<String, String> systemMsg = Map.of("role", "system", "content", "Bạn là hệ thống gợi ý AI.");
        Map<String, String> userMsg = Map.of("role", "user", "content", prompt);

        OpenAiChatResponse response = openAiService.chat(List.of(systemMsg, userMsg));
        String jsonContent = response.getChoices().get(0).getMessage().getContent();

        jsonContent = jsonContent.replaceAll("```json", "").replaceAll("```", "").trim();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonContent);
            JsonNode categoryNode = rootNode.get("categoryIds");
            List<Long> result = new ArrayList<>();
            if (categoryNode.isArray()) {
                for (JsonNode node : categoryNode) {
                    result.add(node.asLong());
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi Parse JSON từ AI");
        }
    }
}