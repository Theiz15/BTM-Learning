package com.learning.btmlearning.dto.response;

import lombok.Data;

@Data
public class QuizQuestionResponse {
    private Long id;

    private QuestionResponse question;
    private Integer sortOrder = 0;
    private Integer score = -1;
}
