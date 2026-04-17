package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Answer;
import com.learning.btmlearning.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionIdOrderByOrderIndexAsc (Long questionId);

    void deleteByQuestionId (Long questionId);

    @Query("""
            SELECT a FROM Answer a 
            JOIN FETCH a.question 
            WHERE a.question IN :questions
            """)
    List<Answer> findByQuestionIn(@Param("questions") List<Question> questions);
}
