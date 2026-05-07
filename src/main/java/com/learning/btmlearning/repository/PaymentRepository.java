package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	@EntityGraph(attributePaths = {"user", "course"})
	@Query("SELECT p FROM Payment p")
	List<Payment> findAllWithDetails();

	@EntityGraph(attributePaths = {"user", "course"})
	@Query("SELECT p FROM Payment p WHERE p.course.instructor.id = :instructorId")
	List<Payment> findAllByInstructorIdWithDetails(@Param("instructorId") Long instructorId);
}
