package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.LessonType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LessonResponse {
    private Long id;
    private Long sectionId;
    private Long courseId;
    private String title;
    private int orderIndex;
    private LessonType lessonType;
    private String videoUrl;
    private String documentUrl;
    private int durationSeconds;
    private boolean isPreview;
    private LocalDateTime createdAt;

    private List<QuizResponse> quizzes;
}
