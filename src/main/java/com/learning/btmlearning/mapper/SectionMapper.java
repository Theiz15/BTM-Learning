package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.LessonRequest;
import com.learning.btmlearning.dto.request.SectionRequest;
import com.learning.btmlearning.dto.response.LessonResponse;
import com.learning.btmlearning.dto.response.SectionResponse;
import com.learning.btmlearning.entity.Lesson;
import com.learning.btmlearning.entity.Section;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
                LessonRequest.class
        }
)
public interface SectionMapper {
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    Section toSection (SectionRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSection (@MappingTarget Section section, SectionRequest request);

    @Mapping(target = "lessons", source = "lessons", qualifiedByName = "toLessonResponse")
    SectionResponse toSectionResponse (Section section);

    @Named("toLessonResponse")
    LessonResponse toLessonResponse (Lesson lesson);
}
