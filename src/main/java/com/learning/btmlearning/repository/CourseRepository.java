package com.learning.btmlearning.repository;

import com.learning.btmlearning.constant.CourseLevel;
import com.learning.btmlearning.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> existsBySlug(String slug);

    @Query("SELECT c FROM Course c WHERE " +
            "c.status = 'ACTIVE' AND " +
            "(:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:level IS NULL OR c.level = :level)")
    Page<Course> searchPublicCourses(@Param("keyword") String keyword,
                                     @Param("level") CourseLevel level,
                                     Pageable pageable);

}
