package com.learning.btmlearning.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProgressRequest {
    private Long lessonId;
    private Long progressId;
    private Long quizId;
    private Integer watchedPercent;
    @NotNull(message = "Not null")
    private int watchedSeconds = 0;
    private Double quizScore;
    private Double scrollPercent;
}
