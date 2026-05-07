package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.LessonRequest;
import com.learning.btmlearning.dto.request.LessonUpdateRequest;
import com.learning.btmlearning.dto.response.LessonResponse;
import com.learning.btmlearning.entity.Lesson;
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
        LessonResponse toLessonResponse (Lesson lesson);

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        void updateLesson (@MappingTarget Lesson lesson, LessonUpdateRequest request);
}
