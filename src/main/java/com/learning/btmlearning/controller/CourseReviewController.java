package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.CourseReviewCreationRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.CourseReviewResponse;
import com.learning.btmlearning.service.CourseReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/course-reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseReviewController {
    CourseReviewService courseReviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CourseReviewResponse> createReview(@Valid @RequestBody CourseReviewCreationRequest request) {
        return ApiResponse.<CourseReviewResponse>builder()
                .result(courseReviewService.createReview(request))
                .build();
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<CourseReviewResponse>> getReviewsByCourse(@PathVariable Long courseId) {
        return ApiResponse.<List<CourseReviewResponse>>builder()
                .result(courseReviewService.getReviewsByCourse(courseId))
                .build();
    }
}
