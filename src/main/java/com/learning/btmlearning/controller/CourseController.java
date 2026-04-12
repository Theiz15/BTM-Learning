package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.CourseRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.CourseResponse;
import com.learning.btmlearning.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@RequestBody CourseRequest request) {
        CourseResponse result = courseService.createCourse(request);

        ApiResponse<CourseResponse> apiResponse = ApiResponse.<CourseResponse>builder()
                .message("Created course successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
