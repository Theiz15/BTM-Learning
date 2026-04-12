package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.QuizRequest;
import com.learning.btmlearning.dto.response.QuizResponse;
import com.learning.btmlearning.entity.Quiz;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "questions", ignore = true)
    Quiz toQuiz (QuizRequest request);

    QuizResponse toQuizResponse (Quiz quiz);
}
