package com.learning.btmlearning.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerRequest {
    @NotBlank(message = "Content must be not null")
    private String content;
    private boolean isCorrect = false;
    private int orderIndex;

    private Long questionId;
}
