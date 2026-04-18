package com.learning.btmlearning.dto.response;

import com.learning.btmlearning.constant.PaymentStatus;
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
}
