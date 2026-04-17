package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.LessonRequest;
import com.learning.btmlearning.dto.request.UpdateProgressRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.CourseProgressResponse;
import com.learning.btmlearning.dto.response.LessonProgressResponse;
import com.learning.btmlearning.dto.response.LessonResponse;
import com.learning.btmlearning.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LessonController {
    private final LessonService lessonService;

    @PostMapping("/lessons")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(@RequestBody LessonRequest request) {
        LessonResponse result = lessonService.createLesson(request);

        ApiResponse<LessonResponse> apiResponse = ApiResponse.<LessonResponse>builder()
                .message("Lesson is created successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/progress/lesson/{lessonId}")
    public ResponseEntity<ApiResponse<LessonProgressResponse>> getOrCreateLessonProgress(@PathVariable Long lessonId, Long userId) {
        LessonProgressResponse result = lessonService.getOrCreate(userId, lessonId);

        ApiResponse<LessonProgressResponse> apiResponse = ApiResponse.<LessonProgressResponse>builder()
                .message("Tracking lesson successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/lesson/update-progress")
    public ResponseEntity<ApiResponse<LessonProgressResponse>> updateProgress(Long userId, @RequestBody @Valid UpdateProgressRequest request) {
        LessonProgressResponse result = lessonService.updateProgress(userId, request);

        ApiResponse<LessonProgressResponse> apiResponse = ApiResponse.<LessonProgressResponse>builder()
                .message("Lesson progress successfully updated")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/courses/progress/{courseId}")
    public ResponseEntity<ApiResponse<CourseProgressResponse>> getCourseProgress(
            Long userId,
            @PathVariable Long courseId) {

        CourseProgressResponse result = lessonService.getCourseProgress(userId, courseId);

        ApiResponse<CourseProgressResponse> apiResponse = ApiResponse.<CourseProgressResponse>builder()
                .message("CourseProgress successfully retrieved")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
