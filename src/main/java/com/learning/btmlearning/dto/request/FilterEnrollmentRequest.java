package com.learning.btmlearning.dto.request;

import com.learning.btmlearning.constant.EnrollmentStatus;
import lombok.Data;

@Data
public class FilterEnrollmentRequest {
    private Long userId;
    private Long courseId;
    private EnrollmentStatus status;
    private int pageNo = 0;
    private int pageSize = 10;
}
