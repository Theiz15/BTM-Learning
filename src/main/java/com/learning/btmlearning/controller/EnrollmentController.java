package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.EnrollmentRequest;
import com.learning.btmlearning.dto.request.FilterEnrollmentRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.EnrollmentResponse;
import com.learning.btmlearning.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping("/enrollments")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(@RequestBody EnrollmentRequest request, Long userId) {
        EnrollmentResponse result = enrollmentService.enroll(request, userId);

        ApiResponse<EnrollmentResponse> apiResponse = ApiResponse.<EnrollmentResponse>builder()
                .result(result)
                .message("Successfully enrolled")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> cancelEnroll (@RequestBody EnrollmentRequest request, Long userId, @PathVariable Long enrollmentId) {
        EnrollmentResponse result = enrollmentService.cancelEnroll(request, userId, enrollmentId);

        ApiResponse<EnrollmentResponse> apiResponse = ApiResponse.<EnrollmentResponse>builder()
                .message("Successfully cancelled")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getById (Long userId, @PathVariable Long enrollmentId) {
        EnrollmentResponse result = enrollmentService.getById(userId, enrollmentId);

        ApiResponse<EnrollmentResponse> apiResponse = ApiResponse.<EnrollmentResponse>builder()
                .message("Get enrollment by id successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/reactive/enroll/{enrollmentId}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> reactivateEnroll (@PathVariable Long enrollmentId, Long userId) {
        EnrollmentResponse result = enrollmentService.reactivateEnroll(userId, enrollmentId);

        ApiResponse<EnrollmentResponse> apiResponse = ApiResponse.<EnrollmentResponse>builder()
                .message("Reactivate enrollment successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/search/enrollment")
    public ResponseEntity<ApiResponse<Page<EnrollmentResponse>>> getAllEnrollments (FilterEnrollmentRequest request) {
        Page<EnrollmentResponse> result = enrollmentService.getAllEnrollments(request);

        ApiResponse<Page<EnrollmentResponse>> apiResponse = ApiResponse.<Page<EnrollmentResponse>>builder()
                .message("Filter enrollments successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
