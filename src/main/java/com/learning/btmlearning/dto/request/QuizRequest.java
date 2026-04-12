package com.learning.btmlearning.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuizRequest {
    private String title;
    private int timeLimitMin;
    private int passScore;
    private boolean isAiGenerated;
    private boolean shuffleQuestions = false;
    private boolean shuffleAnswers = false;
    private List<ManualQuestionItem> manualQuestions;
    private List<RandomQuestionConfig> randomConfigs;

    private Long lessonId;

    @Data
    public static class ManualQuestionItem {
        @NotNull
        private Long questionId;
        private int score = 1;
        private int sortOrder = 0;
    }

    @Data
    public static class RandomQuestionConfig {
        private String difficulty;
        private int amount = 10;
        private int score = 1;
    }
}
