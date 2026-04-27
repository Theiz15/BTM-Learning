package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findFirstByLessonIdOrderByIdDesc(Long lessonId);
    Optional<Quiz> findFirstByLessonIsNullAndTitleIgnoreCaseOrderByIdDesc(String title);
}
