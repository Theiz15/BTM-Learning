package com.learning.btmlearning.dto.request;

import com.learning.btmlearning.constant.Difficulty;
import com.learning.btmlearning.constant.QuestionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuestionRequest {
    @NotNull(message = "Content must be not null!")
    private String content;

    @NotNull(message = "Question type must be not null!")
    private QuestionType questionType;
    private Difficulty difficulty;
    private int orderIndex;
    private Long quizId;
    private List<AnswerRequest> answers;
}
