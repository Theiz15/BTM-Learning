package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionIdOrderByOrderIndexAsc (Long questionId);

    void deleteByQuestionId (Long questionId);
}
