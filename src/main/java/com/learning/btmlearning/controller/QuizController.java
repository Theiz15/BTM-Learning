package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.QuizAttemptRequest;
import com.learning.btmlearning.dto.request.QuizRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.QuizAttemptResponse;
import com.learning.btmlearning.dto.response.QuizResponse;
import com.learning.btmlearning.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class QuizController {
    private final QuizService quizService;

    @PostMapping("/quizzes")
    public ResponseEntity<ApiResponse<QuizResponse>> createQuiz(@RequestBody QuizRequest quizRequest) {
        QuizResponse result = quizService.createQuiz(quizRequest);

        ApiResponse<QuizResponse> apiResponse = ApiResponse.<QuizResponse>builder()
                .message("Create quiz successful")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/quiz/submit")
    public ResponseEntity<ApiResponse<QuizAttemptResponse>> submitQuiz(Long userId, QuizAttemptRequest request){
        QuizAttemptResponse result = quizService.submitQuiz(userId, request);

        ApiResponse<QuizAttemptResponse> apiResponse = ApiResponse.<QuizAttemptResponse>builder()
                .message("Attempt submitted")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuiz (@PathVariable Long quizId) {
        QuizResponse result = quizService.getQuiz(quizId);

        ApiResponse<QuizResponse> apiResponse = ApiResponse.<QuizResponse>builder()
                .message("Quiz retrieved")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/quiz/{quizId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz (@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Quiz deleted")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
