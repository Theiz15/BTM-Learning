package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.AnswerRequest;
import com.learning.btmlearning.dto.response.AnswerResponse;
import com.learning.btmlearning.entity.Answer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring"
)
public interface AnswerMapper {
    Answer toAnswer(AnswerRequest answerRequest);
    AnswerResponse toAnswerResponse(Answer answer);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAnswer(@MappingTarget Answer answer, AnswerRequest answerRequest);
}
