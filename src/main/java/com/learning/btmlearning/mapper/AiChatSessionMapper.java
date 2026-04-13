package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.response.AiChatSessionResponse;
import com.learning.btmlearning.entity.AiChatSession;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AiChatSessionMapper {

    AiChatSessionResponse toResponse(AiChatSession session);

    List<AiChatSessionResponse> toResponseList(List<AiChatSession> sessions);
}