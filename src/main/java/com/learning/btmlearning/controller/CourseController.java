package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.CourseCreationRequest;
import com.learning.btmlearning.dto.request.CourseThumbnailUpdateRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.CourseRatingSummaryResponse;
import com.learning.btmlearning.dto.response.CourseResponse;
import com.learning.btmlearning.service.CourseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseController {
    CourseService courseService;

    @GetMapping
    public ApiResponse<List<CourseResponse>> getAllCourses() {
        return ApiResponse.<List<CourseResponse>>builder()
                .result(courseService.getAllCourses())
                .build();
    }

    @PostMapping
    public ApiResponse<CourseResponse> createCourse(@Valid @RequestBody CourseCreationRequest request) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.createCourse(request))
                .build();
    }

    @PostMapping(value = "/{courseId}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CourseResponse> uploadThumbnail(@PathVariable Long courseId,
                                                       @RequestPart("file") MultipartFile file) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.uploadThumbnail(courseId, file))
                .build();
    }

    @PatchMapping("/{courseId}/thumbnail")
    public ApiResponse<CourseResponse> updateThumbnail(@PathVariable Long courseId,
                                                       @Valid @RequestBody CourseThumbnailUpdateRequest request) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.updateThumbnail(courseId, request.getThumbnailUrl()))
                .build();
    }

    @GetMapping("/{courseId}/rating")
    public ApiResponse<CourseRatingSummaryResponse> getCourseRating(@PathVariable Long courseId) {
        return ApiResponse.<CourseRatingSummaryResponse>builder()
                .result(courseService.getCourseRating(courseId))
                .build();
    }
}
