package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.EnrollmentRequest;
import com.learning.btmlearning.dto.response.CourseResponse;
import com.learning.btmlearning.dto.response.EnrollmentResponse;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper (
        componentModel = "spring",
        uses = {
                CourseMapper.class
        }
)
public interface EnrollmentMapper {
    Enrollment toEnrollment (EnrollmentRequest request);

    @Mapping(target = "course", source = "course", qualifiedByName = "toCourseResponse")
    @Mapping(target = "userId", source = "user.id")
    EnrollmentResponse toEnrollmentResponse (Enrollment enrollment);

    @Named("toCourseResponse")
    CourseResponse toCourseResponse (Course course);
}
