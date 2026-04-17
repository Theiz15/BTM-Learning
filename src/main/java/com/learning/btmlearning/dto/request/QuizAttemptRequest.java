package com.learning.btmlearning.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class QuizAttemptRequest {
    private Long quizId;
    private List<AnswerAttemptRequest> answers;
}
