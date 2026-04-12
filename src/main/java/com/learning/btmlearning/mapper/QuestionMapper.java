package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.QuestionRequest;
import com.learning.btmlearning.dto.response.AnswerResponse;
import com.learning.btmlearning.dto.response.QuestionResponse;
import com.learning.btmlearning.entity.Answer;
import com.learning.btmlearning.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {
                AnswerMapper.class,
        }
)
public interface QuestionMapper {
    @Mapping(target = "quizzes", ignore = true)
    @Mapping(target = "answers", ignore = true)
    Question toQuestion (QuestionRequest request);

    @Mapping(target = "quizzes", ignore = true)
    @Mapping(target = "answers", ignore = true)
    void updateQuestion(@MappingTarget Question question, QuestionRequest request);

    @Mapping(target = "answers", source = "answers", qualifiedByName = "toAnswerResponse")
    QuestionResponse toQuestionResponse (Question question);

    @Named("toAnswerResponse")
    AnswerResponse toAnswerResponse (Answer answer);
}
