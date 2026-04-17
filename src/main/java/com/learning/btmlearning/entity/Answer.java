package com.learning.btmlearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "answers")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private boolean correct;
    private int orderIndex;
    private String explanation;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
    private String referenceAnswer;
}
