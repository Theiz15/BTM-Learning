package com.learning.btmlearning.controller;

import com.learning.btmlearning.constant.CourseLevel;
import com.learning.btmlearning.dto.request.CreateCourseRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.AuthResponse;
import com.learning.btmlearning.dto.response.CourseDetailResponse;
import com.learning.btmlearning.dto.response.PagedCourseResponse;
import com.learning.btmlearning.service.ICourseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/courses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseController {

    ICourseService courseService;

    @PostMapping
    public ApiResponse<CourseDetailResponse> createCourse(@RequestBody @Valid CreateCourseRequest request) {
        return ApiResponse.<CourseDetailResponse>builder()
                .message("Create course successful")
                .result(courseService.createCourse(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PagedCourseResponse> getCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CourseLevel level,
            @RequestParam(defaultValue = "popular") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PagedCourseResponse>builder()
                .message("get courses successful")
                .result(courseService.getPublicCourses(keyword, level, sortBy, page, size))
                .build();
    }

    @PatchMapping("/{id}/submit")
    public ApiResponse<Void> submitCourse(@PathVariable Long id) {
        courseService.submitCourse(id);
        return ApiResponse.<Void>builder()
                .message("Submit course successful")
                .build();
    }
}