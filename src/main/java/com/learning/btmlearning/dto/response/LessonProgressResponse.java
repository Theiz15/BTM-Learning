package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.ProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressResponse {
    private Long lessonId;
    private String lessonTitle;
    private String lessonType;
    private ProgressStatus status;
    private int watchPercent;
    private int timeSpentSeconds;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime completedAt;
    private Double quizScore;
}
