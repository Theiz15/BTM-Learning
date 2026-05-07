package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.Difficulty;
import com.learning.btmlearning.constant.QuestionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiQuizGenerateResponse {
    String title;

    @Builder.Default
    List<GeneratedQuestion> questions = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class GeneratedQuestion {
        String content;
        Difficulty difficulty;
        QuestionType questionType;

        @Builder.Default
        List<GeneratedAnswer> answers = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class GeneratedAnswer {
        String content;
        boolean correct;
        String explanation;
        String referenceAnswer;
    }
}
