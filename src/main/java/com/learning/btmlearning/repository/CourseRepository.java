package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE c.instructor.id = :userId AND c.id = :courseId")
    Optional<Course> findByInstructorIdAndCourseId(@Param("userId") Long userId,
                                           @Param("courseId") Long courseId);
}
