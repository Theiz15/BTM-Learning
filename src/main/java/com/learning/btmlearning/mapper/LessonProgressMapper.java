package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.response.LessonProgressResponse;
import com.learning.btmlearning.entity.LessonProgress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LessonProgressMapper {
    LessonProgressResponse toLessonProgressResponse(LessonProgress lessonProgress);
}
