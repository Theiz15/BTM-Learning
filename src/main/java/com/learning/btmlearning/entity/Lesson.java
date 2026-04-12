package com.learning.btmlearning.entity;

import com.learning.btmlearning.constant.LessonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "lessons")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private int orderIndex;
    private LessonType lessonType;
    private String videoUrl;
    private String documentUrl;
    private int durationSeconds;
    private boolean isPreview;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Quiz quiz;
}
