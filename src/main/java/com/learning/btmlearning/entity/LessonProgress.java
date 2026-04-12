package com.learning.btmlearning.entity;

import com.learning.btmlearning.constant.ProgressStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Enumerated(EnumType.STRING)
    private ProgressStatus status = ProgressStatus.NOT_STARTED;

    @Column(name = "watch_percent")
    private int watchPercent = 0;

    @Column(name = "time_spent_seconds")
    private int timeSpentSeconds = 0;

    @Column(name = "quiz_score")
    private Double quizScore;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
