package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.LessonRequest;
import com.learning.btmlearning.dto.response.LessonResponse;
import com.learning.btmlearning.dto.response.QuizResponse;
import com.learning.btmlearning.entity.Lesson;
import com.learning.btmlearning.entity.Quiz;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {
                QuizMapper.class
        }
)
public interface LessonMapper {
        @Mapping(target = "section", ignore = true)
        @Mapping(target = "quiz", ignore = true)
        Lesson toLesson (LessonRequest request);

        @Mapping(target = "quizResponse", source = "quiz", qualifiedByName = "toQuizResponse")
        LessonResponse toLessonResponse (Lesson lesson);

        @Named("toQuizResponse")
        QuizResponse toQuizResponse (Quiz quiz);
}
