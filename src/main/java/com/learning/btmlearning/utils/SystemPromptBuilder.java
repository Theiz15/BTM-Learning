package com.learning.btmlearning.utils;

import com.learning.btmlearning.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class SystemPromptBuilder {

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
}