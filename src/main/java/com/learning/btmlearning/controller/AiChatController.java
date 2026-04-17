package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.AiChatMessageRequest;
import com.learning.btmlearning.dto.request.CreateAiSessionRequest;
import com.learning.btmlearning.dto.response.AiChatMessageResponse;
import com.learning.btmlearning.dto.response.AiChatSessionResponse;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.entity.AiChatSession;
import com.learning.btmlearning.service.impl.AiChatService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/ai/chat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiChatController {
    AiChatService aiChatService;

    // 1. Tạo Phiên Chat mới
    @PostMapping("/sessions")
//    @PreAuthorize("isAuthenticated()") // Bắt buộc phải đăng nhập
    public ApiResponse<AiChatSessionResponse> createSession(@RequestBody(required = false) CreateAiSessionRequest request) {
        Long courseId = (request != null) ? request.getCourseId() : null;

        return ApiResponse.<AiChatSessionResponse>builder()
                .message("Created session")
                .result(aiChatService.createSession(courseId))
                .build();
    }

    // 2. Gửi tin nhắn và nhận câu trả lời
    @PostMapping("/sessions/{sessionToken}/messages")
//    @PreAuthorize("isAuthenticated()")
    public ApiResponse<AiChatMessageResponse> sendMessage(
            @PathVariable String sessionToken,
            @RequestBody @Valid AiChatMessageRequest request
    ) {

        return ApiResponse.<AiChatMessageResponse>builder()
                .result(aiChatService.sendMessage(sessionToken, request.getContent()))
                .build();
    }

    @PostMapping("/sessions/{sessionToken}/history")
//    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<AiChatMessageResponse>> getSessionHistory(
            @PathVariable String sessionToken
    ) {

        return ApiResponse.<List<AiChatMessageResponse>>builder()
                .result(aiChatService.getSessionHistory(sessionToken))
                .build();
    }


    @PostMapping(value = "/sessions/{sessionToken}/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    @PreAuthorize("isAuthenticated()")
    public SseEmitter streamMessage(
            @PathVariable String sessionToken,
            @RequestBody @Valid AiChatMessageRequest request
    ) {
        return aiChatService.streamMessage(sessionToken, request.getContent());
    }
}
