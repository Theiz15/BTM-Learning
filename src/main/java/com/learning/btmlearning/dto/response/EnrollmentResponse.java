package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.PaymentStatus;
import com.learning.btmlearning.entity.Enrollment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EnrollmentResponse {
    private Long id;
    private PaymentStatus paymentStatus;
    private BigDecimal price;
    private LocalDateTime enrolledAt;
    private LocalDateTime expiredAt;
    private LocalDateTime completedAt;
    private CourseResponse course;
    private Long userId;
}
