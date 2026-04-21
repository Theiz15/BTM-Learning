package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.QuizRequest;
import com.learning.btmlearning.dto.response.QuizQuestionResponse;
import com.learning.btmlearning.dto.response.QuizResponse;
import com.learning.btmlearning.entity.Quiz;
import com.learning.btmlearning.entity.QuizQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {QuizQuestionMapper.class}
)
public interface QuizMapper {
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "questions", ignore = true)
    Quiz toQuiz (QuizRequest request);

    @Mapping(target = "timeLimit", source = "timeLimitMin")
    @Mapping(target = "passScore", source = "passScore")
    @Mapping(target = "questions", source = "questions", qualifiedByName = "toQuizQuestionResponse")
    @Mapping(target = "lessonId", source = "lesson.id")
    QuizResponse toQuizResponse (Quiz quiz);

    @Named("toQuizQuestionResponse")
    QuizQuestionResponse toQuizQuestionResponse (QuizQuestion quizQuestion);
}
