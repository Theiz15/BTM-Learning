package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.LessonRequest;
import com.learning.btmlearning.dto.request.LessonUpdateRequest;
import com.learning.btmlearning.dto.request.UpdateProgressRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.CourseProgressResponse;
import com.learning.btmlearning.dto.response.LessonProgressResponse;
import com.learning.btmlearning.dto.response.LessonResponse;
import com.learning.btmlearning.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LessonController {
    private final LessonService lessonService;

    @PostMapping("/lessons")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(@RequestBody LessonRequest request) {
        LessonResponse result = lessonService.createLesson(request);

        ApiResponse<LessonResponse> apiResponse = ApiResponse.<LessonResponse>builder()
                .message("Lesson is created successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable Long lessonId,
            @RequestBody LessonUpdateRequest request
    ) {
        LessonResponse result = lessonService.updateLesson(request, lessonId);

        ApiResponse<LessonResponse> apiResponse = ApiResponse.<LessonResponse>builder()
                .message("Lesson is updated successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Lesson is deleted successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/progress/lesson/{lessonId}")
    public ResponseEntity<ApiResponse<LessonProgressResponse>> getOrCreateLessonProgress(@PathVariable Long lessonId) {
        LessonProgressResponse result = lessonService.getOrCreate(lessonId);

        ApiResponse<LessonProgressResponse> apiResponse = ApiResponse.<LessonProgressResponse>builder()
                .message("Tracking lesson successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/lesson/update-progress")
    public ResponseEntity<ApiResponse<LessonProgressResponse>> updateProgress(@RequestBody @Valid UpdateProgressRequest request) {
        LessonProgressResponse result = lessonService.updateProgress(request);

        ApiResponse<LessonProgressResponse> apiResponse = ApiResponse.<LessonProgressResponse>builder()
                .message("Lesson progress successfully updated")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/courses/progress/{courseId}")
    public ResponseEntity<ApiResponse<CourseProgressResponse>> getCourseProgress(@PathVariable Long courseId) {

        CourseProgressResponse result = lessonService.getCourseProgress(courseId);

        ApiResponse<CourseProgressResponse> apiResponse = ApiResponse.<CourseProgressResponse>builder()
                .message("CourseProgress successfully retrieved")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
