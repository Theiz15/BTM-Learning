package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    // Random by difficulty
    @Query(value = """
        SELECT * FROM questions
        WHERE difficulty = :difficulty 
        ORDER BY RAND()
        LIMIT :amount
        """, nativeQuery = true)
    List<Question> findRandomByDifficulty(
            @Param("difficulty") String difficulty,
            @Param("amount") Integer amount
    );

    //Random question
    @Query(value = """
        SELECT * FROM questions
        ORDER BY RAND()
        LIMIT :amount
        """, nativeQuery = true)
    List<Question> findRandom(
            @Param("amount") Integer amount
    );
}
