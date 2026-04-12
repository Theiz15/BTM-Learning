package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.LessonType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LessonResponse {
    private String title;
    private int orderIndex;
    private LessonType lessonType;
    private String videoUrl;
    private String documentUrl;
    private int durationSeconds;
    private boolean isPreview;
    private LocalDateTime createdAt;

    private QuizResponse quizResponse;
}
