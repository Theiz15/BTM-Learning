package com.learning.btmlearning.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerRequest {
    @NotBlank(message = "Content must be not null")
    private String content;

    @JsonProperty("correct")
    private boolean correct = true;
    private int orderIndex;
    private String referenceAnswer;
    private String explanation;

    private Long questionId;
}
