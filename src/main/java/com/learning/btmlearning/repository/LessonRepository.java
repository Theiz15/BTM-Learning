package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
	List<Lesson> findBySectionIdOrderByOrderIndexAsc(Long sectionId);

	@Query("""
    SELECT l FROM Lesson l
    JOIN FETCH l.section s
    JOIN FETCH s.course
    WHERE l.id = :id""")
	Optional<Lesson> findByIdWithCourse(Long id);
}
