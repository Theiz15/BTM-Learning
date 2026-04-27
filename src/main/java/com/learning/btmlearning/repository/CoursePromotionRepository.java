package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.CoursePromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoursePromotionRepository extends JpaRepository<CoursePromotion, Long> {
	Optional<CoursePromotion> findFirstByCourseIdOrderByStartDateDescIdDesc(Long courseId);
}
