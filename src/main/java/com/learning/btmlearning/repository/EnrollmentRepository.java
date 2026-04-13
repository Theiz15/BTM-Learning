package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Enrollment;
import org.springframework.data.repository.CrudRepository;

public interface EnrollmentRepository extends CrudRepository<Enrollment,Long> {
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}
