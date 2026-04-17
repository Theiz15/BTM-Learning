package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    @Query("""
            SELECT qq FROM QuizQuestion qq 
            JOIN FETCH qq.question q 
            JOIN FETCH qq.quiz z 
            WHERE z.id = :quizId 
            ORDER BY qq.sortOrder ASC
            """)
    List<QuizQuestion> findAllByQuizIdWithDetails(@Param("quizId") Long quizId);
}

// N+1 né