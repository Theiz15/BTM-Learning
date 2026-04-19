package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCodeAndIsActiveTrue(String code);

    boolean existsByCode(String code);
}