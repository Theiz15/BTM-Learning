package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {
    Optional<Enrollment> findByUserIdAndCourseId(Long user_id, Long course_id);

    boolean existsByUserIdAndCourseId(Long user_id, Long course_id);
}
