package com.learning.btmlearning.repository;

import com.learning.btmlearning.constant.CourseStatus;
import com.learning.btmlearning.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course  c JOIN c.instructor i WHERE c.status=:status")
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.status=:status ORDER BY c.avgRating DESC")
    List<Course> findTopRatedCourses(@Param("status") CourseStatus status, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.status='ACTIVE' " +
            "AND c.category.id IN :categoryIds "+
            "AND (:enrolledIds) IS NULL OR c.id NOT IN :enrolledIds "+
            "ORDER BY c.avgRating DESC"
    )
    List<Course> findRecommendedCourses(@Param("categoryIds") List<Long> categoryIds,
                                        @Param("enrolledIds") List<Long> enrolledIds,
                                        Pageable pageable) ;


    @Query("SELECT c FROM Course c WHERE c.instructor.id = :userId AND c.id = :courseId")
    Optional<Course> findByInstructorIdAndCourseId(@Param("userId") Long userId,
                                                   @Param("courseId") Long courseId);
}
