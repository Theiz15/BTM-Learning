package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    Optional<Certificate> findByCodeIgnoreCaseOrCertCodeIgnoreCase(String code, String certCode);
}
