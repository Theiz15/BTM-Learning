package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.CreateCourseRequest;
import com.learning.btmlearning.dto.response.CourseDetailResponse;
import com.learning.btmlearning.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {
    @Mapping(target = "category",ignore = true)
    Course toCourse(CreateCourseRequest request);

    CourseDetailResponse toCourseDetailResponse(Course course);
}
