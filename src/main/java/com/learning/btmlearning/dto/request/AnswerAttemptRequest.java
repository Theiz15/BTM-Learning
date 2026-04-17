package com.learning.btmlearning.dto.request;

import lombok.Data;

@Data
public class AnswerAttemptRequest {
    private Long questionId;
    private Long selectedAnswerId;
    private String essayAnswer;
}
