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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping("/questions")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(@RequestBody @Valid QuestionRequest questionRequest) {
        QuestionResponse result = questionService.addQuestion(questionRequest);

        ApiResponse<QuestionResponse> apiResponse = ApiResponse.<QuestionResponse>builder()
                .message("Question created successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Question deleted successfully")
                .result(null)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody @Valid QuestionRequest questionRequest
    ) {
        QuestionResponse result = questionService.updateQuestion(questionRequest, questionId);

        ApiResponse<QuestionResponse> apiResponse = ApiResponse.<QuestionResponse>builder()
                .message("Question updated successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /** Gán hoặc bỏ gán câu hỏi khỏi quiz. quizId=null → bỏ gán */
    @PatchMapping("/questions/{questionId}/assign")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<QuestionResponse>> assignQuestion(
            @PathVariable Long questionId,
            @RequestParam(required = false) Long quizId
    ) {
        QuestionResponse result = questionService.assignQuestionToQuiz(questionId, quizId);

        ApiResponse<QuestionResponse> apiResponse = ApiResponse.<QuestionResponse>builder()
                .message(quizId != null ? "Question assigned to quiz" : "Question unassigned from quiz")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /** Lấy tất cả câu hỏi thuộc một quiz cụ thể */
    @GetMapping("/questions/quiz/{quizId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestionsByQuiz(@PathVariable Long quizId) {
        List<QuestionResponse> result = questionService.getQuestionsByQuizId(quizId);

        ApiResponse<List<QuestionResponse>> apiResponse = ApiResponse.<List<QuestionResponse>>builder()
                .result(result)
                .message("Get questions by quiz successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/questions/unassigned")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Page<QuestionResponse>>> getUnassignedQuestions(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "200") int pageSize
    ) {
        SearchQuestionRequest req = new SearchQuestionRequest();
        req.setUnassigned(true);
        req.setPageNo(pageNo);
        req.setPageSize(pageSize);

        Page<QuestionResponse> questions = questionService.getQuestions(req);

        ApiResponse<Page<QuestionResponse>> apiResponse = ApiResponse.<Page<QuestionResponse>>builder()
                .result(questions)
                .message("Get unassigned questions successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/questions/search")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Page<QuestionResponse>>> searchQuestion(@RequestBody @Valid SearchQuestionRequest request) {
        Page<QuestionResponse> questions = questionService.getQuestions(request);

        ApiResponse<Page<QuestionResponse>> apiResponse = ApiResponse.<Page<QuestionResponse>>builder()
                .result(questions)
                .message("Get questions successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}


