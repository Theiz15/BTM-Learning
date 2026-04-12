package com.learning.btmlearning.dto.response;

import lombok.Data;

@Data
public class AnswerResponse {
    private Long id;
    private String content;
    private boolean isCorrect;
    private int orderIndex;
}
