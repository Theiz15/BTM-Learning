package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.LessonRequest;
import com.learning.btmlearning.dto.request.LessonUpdateRequest;
import com.learning.btmlearning.dto.response.LessonResponse;
import com.learning.btmlearning.dto.response.QuizResponse;
import com.learning.btmlearning.entity.Lesson;
import com.learning.btmlearning.entity.Quiz;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
                QuizMapper.class
        }
)
public interface LessonMapper {
        @Mapping(target = "section", ignore = true)
        @Mapping(target = "quizzes", ignore = true)
        Lesson toLesson (LessonRequest request);

        @Mapping(target = "sectionId", source = "section.id")
        @Mapping(target = "courseId", source = "course.id")
        @Mapping(target = "quizzes", source = "quizzes", qualifiedByName = "toQuizResponse")
        LessonResponse toLessonResponse (Lesson lesson);

        @Named("toQuizResponse")
        QuizResponse toQuizResponse (Quiz quiz);

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        void updateLesson (@MappingTarget Lesson lesson, LessonUpdateRequest request);
}
