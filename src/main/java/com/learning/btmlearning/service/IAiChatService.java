package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.response.AiChatMessageResponse;
import com.learning.btmlearning.dto.response.AiChatSessionResponse;

public interface IAiChatService {
    AiChatSessionResponse createSession(Long courseId) ;
    AiChatMessageResponse sendMessage(String sessionToken, String userContent) ;
}
