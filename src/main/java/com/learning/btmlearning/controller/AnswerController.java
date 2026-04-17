package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.AnswerRequest;
import com.learning.btmlearning.dto.response.AnswerResponse;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping("/answers")
    public ResponseEntity<ApiResponse<AnswerResponse>> createAnswer(@RequestBody @Valid AnswerRequest request) {
        AnswerResponse result = answerService.createAnswer(request);

        ApiResponse<AnswerResponse> apiResponse = ApiResponse.<AnswerResponse>builder()
                .message("Answer is created successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/answers")
    public ResponseEntity<ApiResponse<List<AnswerResponse>>> getAllAnswers() {
        List<AnswerResponse> result = answerService.getAllAnswers();

        ApiResponse<List<AnswerResponse>> apiResponse = ApiResponse.<List<AnswerResponse>>builder()
                .message("Get list answers")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/answers/{answerId}")
    public ResponseEntity<ApiResponse<AnswerResponse>> updateAnswer(@PathVariable Long answerId, @RequestBody @Valid AnswerRequest request) {
        AnswerResponse result = answerService.updateAnswer(request, answerId);

        ApiResponse<AnswerResponse> apiResponse = ApiResponse.<AnswerResponse>builder()
                .message("Answer is updated successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<ApiResponse<Void>> deleteAnswer(@PathVariable Long answerId) {
        answerService.deleteAnswer(answerId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Answer is deleted successfully")
                .result(null)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
