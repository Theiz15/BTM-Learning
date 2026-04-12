package com.learning.btmlearning.entity;

import com.learning.btmlearning.constant.Difficulty;
import com.learning.btmlearning.constant.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "questions")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private QuestionType questionType;

    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String explanation;

    private int orderIndex;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL)
    private List<QuizQuestion> quizzes;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Answer> answers = new ArrayList<>();
}
