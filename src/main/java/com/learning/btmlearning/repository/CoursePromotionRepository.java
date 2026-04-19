package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.CoursePromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursePromotionRepository extends JpaRepository<CoursePromotion, Long> {
}
