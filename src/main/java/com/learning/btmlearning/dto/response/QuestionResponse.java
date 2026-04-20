package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.Difficulty;
import com.learning.btmlearning.constant.QuestionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionResponse {
    private Long id;
    private QuestionType questionType;
    private Difficulty difficulty;
    private String content;
    private LocalDateTime createdAt;
    private List<AnswerResponse> answers;
}
