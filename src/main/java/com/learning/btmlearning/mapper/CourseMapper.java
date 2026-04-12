package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.CourseRequest;
import com.learning.btmlearning.dto.response.CourseResponse;
import com.learning.btmlearning.dto.response.SectionResponse;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.Section;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
                SectionMapper.class
        }
)
public interface CourseMapper {
    Course toCourse (CourseRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCourse (@MappingTarget Course course, CourseRequest request);

    @Mapping(source = "sections", target = "sections", qualifiedByName = "toSectionResponse")
    CourseResponse toCourseResponse (Course course);

    @Named("toSectionResponse")
    SectionResponse toSectionResponse (Section section);
}
