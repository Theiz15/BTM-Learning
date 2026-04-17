package com.learning.btmlearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseProgressResponse {
    private Long courseId;
    private String courseTitle;
    private int totalLessons;
    private int completedLessons;
    private int progressPercent;
    private int totalTimeSpentSeconds;
    private List<SectionProgressResponse> sections;

    @Data
    @Builder
    public static class SectionProgressResponse {
        private Long sectionId;
        private String sectionTitle;
        private int completedLessons;
        private int totalLessons;
        private List<LessonProgressResponse> lessons;
    }
}
