package com.learning.btmlearning.dto.response;

import lombok.Data;

@Data
public class LessonProgressResponse {
    private Long lessonId;

    private int watchPercent;
    private int timeSpentSeconds;
    private double quizScore;
}
