package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    @Query("SELECT COALESCE(SUM(lp.watchedSeconds), 0) FROM LessonProgress lp WHERE lp.enrollment.id = :enrollmentId")
    Integer sumTimeSpentByEnrollment(@Param("enrollmentId") Long enrollmentId);

    List<LessonProgress> findByEnrollmentId(Long enrollmentId);
    Optional<LessonProgress> findByEnrollmentIdAndLessonId (Long enrollmentId, Long lessonId);

    @Query("""
        SELECT COUNT(lp) FROM LessonProgress lp
        WHERE lp.enrollment.id = :enrollmentId
          AND lp.status = 'COMPLETED'
        """)
    Integer countCompletedByEnrollment(@Param("enrollmentId") Long enrollmentId);
}
