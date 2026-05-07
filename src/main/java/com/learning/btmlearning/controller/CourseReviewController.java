package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.CourseReviewCreationRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.CourseReviewResponse;
import com.learning.btmlearning.service.CourseReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/course-reviews")
@RequiredArgsConstructor
public class CourseReviewController {
    private final CourseReviewService courseReviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CourseReviewResponse>> createReview(@Valid @RequestBody CourseReviewCreationRequest request) {
        ApiResponse<CourseReviewResponse> apiResponse = ApiResponse.<CourseReviewResponse>builder()
                .result(courseReviewService.createReview(request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<CourseReviewResponse>>> getReviewsByCourse(@PathVariable Long courseId) {
        ApiResponse<List<CourseReviewResponse>> apiResponse = ApiResponse.<List<CourseReviewResponse>>builder()
                .result(courseReviewService.getReviewsByCourse(courseId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
