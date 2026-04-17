package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.EnrollmentRequest;
import com.learning.btmlearning.dto.request.FilterEnrollmentRequest;
import com.learning.btmlearning.dto.response.EnrollmentResponse;
import org.springframework.data.domain.Page;

public interface EnrollmentService {
    EnrollmentResponse enroll (EnrollmentRequest request, Long userId);
    EnrollmentResponse cancelEnroll (EnrollmentRequest request, Long userId, Long enrollmentId);
    EnrollmentResponse getById (Long userId, Long enrollmentId);
    EnrollmentResponse reactivateEnroll (Long enrollmentId, Long userId);
    Page<EnrollmentResponse> getAllEnrollments (FilterEnrollmentRequest request);
}
