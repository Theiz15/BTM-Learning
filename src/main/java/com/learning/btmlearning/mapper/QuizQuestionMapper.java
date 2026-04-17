package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.response.QuizQuestionResponse;
import com.learning.btmlearning.entity.QuizQuestion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizQuestionMapper {
    QuizQuestionResponse toQuizQuestionResponse(QuizQuestion quizQuestion);
}
