package com.learning.btmlearning.dto.request;

import com.learning.btmlearning.constant.Difficulty;
import com.learning.btmlearning.constant.QuestionType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiQuizGenerateRequest {
    @NotBlank(message = "Topic is required")
    String topic;

    String context;

    @Builder.Default
    @Min(value = 1, message = "Question count must be at least 1")
    @Max(value = 20, message = "Question count must be at most 20")
    Integer questionCount = 5;

    Difficulty difficulty;

    QuestionType questionType;

    @Builder.Default
    Boolean includeExplanations = true;
}
