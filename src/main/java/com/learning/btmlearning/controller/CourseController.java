package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.CourseRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.CourseResponse;
import com.learning.btmlearning.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        List<CourseResponse> result = courseService.getAllCourses();

        ApiResponse<List<CourseResponse>> apiResponse = ApiResponse.<List<CourseResponse>>builder()
                .message("Get list of courses successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(@PathVariable Long courseId, @RequestBody CourseRequest request) {
        CourseResponse result = courseService.updateCourse(request, courseId);

        ApiResponse<CourseResponse> apiResponse = ApiResponse.<CourseResponse>builder()
                .message("Updated course successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Deleted course successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(@PathVariable Long courseId) {
        CourseResponse result = courseService.getCourseById(courseId);

        ApiResponse<CourseResponse> apiResponse = ApiResponse.<CourseResponse>builder()
                .message("Get course successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
