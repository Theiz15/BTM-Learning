package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.SectionRequest;
import com.learning.btmlearning.dto.response.SectionResponse;
import com.learning.btmlearning.entity.Section;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
                LessonMapper.class
        }
)
public interface SectionMapper {
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    Section toSection (SectionRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSection (@MappingTarget Section section, SectionRequest request);

        @Mapping(target = "courseId", source = "course.id")
    SectionResponse toSectionResponse (Section section);
}
