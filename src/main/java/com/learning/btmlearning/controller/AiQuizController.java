package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.AiQuizGenerateRequest;
import com.learning.btmlearning.dto.response.AiQuizGenerateResponse;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.service.impl.AiQuizGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/ai/quiz")
@RequiredArgsConstructor
public class AiQuizController {
    private final AiQuizGenerationService aiQuizGenerationService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public ApiResponse<AiQuizGenerateResponse> generate(@RequestBody @Valid AiQuizGenerateRequest request) {
        return ApiResponse.<AiQuizGenerateResponse>builder()
                .message("AI quiz generated successfully")
                .result(aiQuizGenerationService.generate(request))
                .build();
    }
}
