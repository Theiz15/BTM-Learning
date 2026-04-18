package com.learning.btmlearning.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizResponse {
    private Integer id;
    private String title;
    private Integer timeLimit;
    private boolean shuffleQuestions;
    private boolean shuffleAnswers;
    private Integer totalQuestions;
    private Integer totalScore;
    private LocalDateTime createdAt;
    private List<QuizQuestionResponse> questions;
}
