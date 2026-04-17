package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsBySlug(String slug);
}
