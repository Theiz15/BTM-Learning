package com.learning.btmlearning.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnrollmentRequest {
    private Long courseId;
    private BigDecimal price = BigDecimal.ZERO;
    private String reason;
}
