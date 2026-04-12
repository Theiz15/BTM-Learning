package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.Difficulty;
import com.learning.btmlearning.constant.QuestionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionResponse {
    private Long id;
    private Integer topicId;
    private String topicName;
    private QuestionType type;
    private Difficulty difficulty;
    private String content;
    private String explanation;
    private List<AnswerResponse> answers;
    private LocalDateTime createdAt;
}
