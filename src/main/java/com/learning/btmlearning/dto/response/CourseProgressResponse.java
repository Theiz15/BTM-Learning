package com.learning.btmlearning.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class CourseProgressResponse {
    private Long Id;
    private String courseTitle;
    private int totalLessons;
    private int completedLessons;
    private int progressPercent;
    private int totalTimeSpentSeconds;
    private List<SectionProgressResponse> sections;

    @Data
    @Builder
    public static class SectionProgressResponse {
        private int sectionId;
        private String sectionTitle;
        private int completedLessons;
        private int totalLessons;
        private List<LessonProgressResponse> lessons;
    }
}
