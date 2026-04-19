package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.AiChatMessageRequest;
import com.learning.btmlearning.dto.request.CreateAiSessionRequest;
import com.learning.btmlearning.dto.response.*;
import com.learning.btmlearning.entity.AiChatSession;
import com.learning.btmlearning.service.impl.AiChatService;
import com.learning.btmlearning.service.impl.RecommendationService;
import com.learning.btmlearning.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/ai/chat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiChatController {
    AiChatService aiChatService;
    RecommendationService recommendationService;
    SecurityUtil securityUtil;

    @PostMapping("/sessions")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<AiChatSessionResponse> createSession(@RequestBody(required = false) CreateAiSessionRequest request) {
        Long courseId = (request != null) ? request.getCourseId() : null;

        return ApiResponse.<AiChatSessionResponse>builder()
                .message("Created session")
                .result(aiChatService.createSession(courseId))
                .build();
    }

    @PostMapping("/sessions/{sessionToken}/messages")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<AiChatMessageResponse> sendMessage(
            @PathVariable String sessionToken,
            @RequestBody @Valid AiChatMessageRequest request
    ) {

        return ApiResponse.<AiChatMessageResponse>builder()
                .result(aiChatService.sendMessage(sessionToken, request.getContent()))
                .build();
    }

    @PostMapping("/sessions/{sessionToken}/history")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<AiChatMessageResponse>> getSessionHistory(
            @PathVariable String sessionToken
    ) {

        return ApiResponse.<List<AiChatMessageResponse>>builder()
                .result(aiChatService.getSessionHistory(sessionToken))
                .build();
    }


    @PostMapping(value = "/sessions/{sessionToken}/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    public SseEmitter streamMessage(
            @PathVariable String sessionToken,
            @RequestBody @Valid AiChatMessageRequest request
    ) {
        return aiChatService.streamMessage(sessionToken, request.getContent());
    }

    @GetMapping("/recommend")
//    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<CourseSummaryResponse>> getRecommendations() {
        Long userId = securityUtil.getCurrentUser().getId();
        return ApiResponse.<List<CourseSummaryResponse>>builder()
                .result(recommendationService.getRecommendations(userId))
                .build();
    }
}
