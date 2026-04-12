package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.LessonRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.LessonResponse;
import com.learning.btmlearning.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
