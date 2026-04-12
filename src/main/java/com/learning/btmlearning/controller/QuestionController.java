package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.QuestionRequest;
import com.learning.btmlearning.dto.request.SearchQuestionRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.QuestionResponse;
import com.learning.btmlearning.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(@RequestBody @Valid QuestionRequest questionRequest) {
        QuestionResponse result = questionService.addQuestion(questionRequest);

        ApiResponse<QuestionResponse> apiResponse = ApiResponse.<QuestionResponse>builder()
                .message("Question created successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Question deleted successfully")
                .result(null)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/questions/search")
    public ResponseEntity<ApiResponse<Page<QuestionResponse>>> searchQuestion(@RequestBody @Valid SearchQuestionRequest request) {
        Page<QuestionResponse> questions = questionService.getQuestions(request);

        ApiResponse<Page<QuestionResponse>> apiResponse = ApiResponse.<Page<QuestionResponse>>builder()
                .result(questions)
                .message("Get questions successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
