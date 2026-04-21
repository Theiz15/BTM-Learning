package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.CourseDiscountRequest;
import com.learning.btmlearning.dto.request.CourseRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.CourseResponse;
import com.learning.btmlearning.dto.response.CourseSummaryResponse;
import com.learning.btmlearning.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/courses")
        @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
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
        @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(@PathVariable Long courseId, @RequestBody CourseRequest request) {
        CourseResponse result = courseService.updateCourse(request, courseId);

        ApiResponse<CourseResponse> apiResponse = ApiResponse.<CourseResponse>builder()
                .message("Updated course successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/course/{courseId}")
        @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
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

    @GetMapping("/pending")
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getPendingCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updateAt").descending());

        Page<CourseResponse> result = courseService.getPendingCourses(pageable);

        ApiResponse<Page<CourseResponse>> apiResponse = ApiResponse.<Page<CourseResponse>>builder()
                .message("Get course successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{id}/approve")
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> approveCourse(@PathVariable Long id) {
        courseService.approveCourse(id);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Approve course successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{id}/reject")
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> rejectCourse(@PathVariable Long id) {
        courseService.rejectCourse(id);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Reject course successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
    @PatchMapping("/courses/{courseId}/discount")
        @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ApiResponse<CourseSummaryResponse> setCourseDiscount(
            @PathVariable Long courseId,
            @RequestBody CourseDiscountRequest request) {
        return ApiResponse.<CourseSummaryResponse>builder()
                .message("Apply voucher successfully")
                .result(courseService.updateCourseDiscount(courseId, request))
                .build();
    }


}
