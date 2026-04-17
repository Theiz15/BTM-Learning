package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
