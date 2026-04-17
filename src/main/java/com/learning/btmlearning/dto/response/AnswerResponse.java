package com.learning.btmlearning.dto.response;

import lombok.Data;

@Data
public class AnswerResponse {
    private Long id;
    private String content;
    private boolean correct;
    private int orderIndex;
    private String explanation;
    private String referenceAnswer;
}
