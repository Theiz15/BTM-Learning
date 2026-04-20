package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.QuestionRequest;
import com.learning.btmlearning.dto.response.QuestionResponse;
import com.learning.btmlearning.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = "spring",
    uses = {
        AnswerMapper.class
    }
)
public interface QuestionMapper {
    @Mapping(target = "quizzes", ignore = true)
    @Mapping(target = "answers", ignore = true)
    Question toQuestion (QuestionRequest request);

    @Mapping(target = "quizzes", ignore = true)
    @Mapping(target = "answers", ignore = true)
    void updateQuestion(@MappingTarget Question question, QuestionRequest request);

    QuestionResponse toQuestionResponse (Question question);
}
