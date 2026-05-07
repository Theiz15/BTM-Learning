package com.learning.btmlearning.dto.request;

import com.learning.btmlearning.constant.LessonType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LessonUpdateRequest {
    private String title;
    private int orderIndex;
    private LessonType lessonType;
    private int durationSeconds;

    private boolean preview;
    private LocalDateTime createdAt;

    private Long sectionId;
    private Long fileUploadId;
    private Long quizId;
    private List<Long> quizIds;
}
