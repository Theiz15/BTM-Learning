package com.learning.btmlearning.repository;

import com.learning.btmlearning.constant.CourseStatus;
import com.learning.btmlearning.entity.Course;
import org.springframework.data.jpa.repository.EntityGraph;
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
    @Query("SELECT c FROM Course c WHERE c.status=:status")
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

    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId")
    long countByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId AND c.status IN :statuses")
    long countByInstructorIdAndStatuses(@Param("instructorId") Long instructorId,
                                                                            @Param("statuses") List<CourseStatus> statuses);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId AND c.status = :status")
    long countByInstructorIdAndStatus(@Param("instructorId") Long instructorId, @Param("status") CourseStatus status);

    @EntityGraph(attributePaths = {"instructor", "category"})
    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId")
    List<Course> findAllByInstructorId(@Param("instructorId") Long instructorId);

    @EntityGraph(attributePaths = {"instructor", "category"})
    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId AND c.status = :status ORDER BY c.createAt DESC")
    List<Course> findByInstructorIdAndStatusOrderByCreateAtDesc(@Param("instructorId") Long instructorId, @Param("status") CourseStatus status);

    @EntityGraph(attributePaths = {"instructor", "category"})
    List<Course> findByStatusOrderByCreateAtDesc(CourseStatus status);

    Boolean existsBySlug(String slug);
    Boolean existsBySlugAndIdNot(String slug, Long id);
}
