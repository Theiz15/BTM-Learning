package com.learning.btmlearning.dto.request;

import com.learning.btmlearning.constant.LessonType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class  LessonRequest {
    private String title;
    private int orderIndex;
    private LessonType lessonType;
    private int durationSeconds;
    private boolean isPreview;
    private LocalDateTime createdAt;

    private Long sectionId;
    private Long fileUploadId;
    private Long quizId;
}
