package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.response.AiChatMessageResponse;
import com.learning.btmlearning.entity.AiMessage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AiChatMessageMapper {
//    @Mapping(target = "role", source = "role.name")
    AiChatMessageResponse toResponse(AiMessage aim);

    List<AiChatMessageResponse> toResponseList(List<AiMessage> messages);

    default String map(Enum<?> value) {
        return value != null ? value.name() : null;
    }
}
