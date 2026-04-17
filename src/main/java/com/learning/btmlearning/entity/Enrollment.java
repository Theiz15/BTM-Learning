package com.learning.btmlearning.entity;

import com.learning.btmlearning.constant.EnrollmentStatus;
import com.learning.btmlearning.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "enrollments")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "progress_percent", nullable = false)
    @Builder.Default
    Float progressPercent = 0.0f;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.FREE;

    private EnrollmentStatus status;

    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
}
