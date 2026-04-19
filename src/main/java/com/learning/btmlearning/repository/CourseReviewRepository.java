package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    boolean existsByCourseIdAndUserId(Long courseId, Long userId);
    List<CourseReview> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    @Query("select coalesce(avg(cr.rating), 0), count(cr) from CourseReview cr where cr.course.id = :courseId")
    Object[] calculateCourseRatingSummary(@Param("courseId") Long courseId);
}
