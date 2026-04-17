package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.dto.request.SectionRequest;
import com.learning.btmlearning.dto.response.SectionResponse;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.Section;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.SectionMapper;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.repository.SectionRepository;
import com.learning.btmlearning.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {
    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;
    private final CourseRepository courseRepository;

    @Override
    public SectionResponse createSection(SectionRequest request) {
        Section section = sectionMapper.toSection(request);

        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );

        section.setCourse(course);

        return sectionMapper.toSectionResponse(sectionRepository.save(section));
    }

    @Override
    public SectionResponse updateSection(SectionRequest request, Long sectionId) {
        Section section = getSectionById(sectionId);

        sectionMapper.updateSection(section, request);

        return sectionMapper.toSectionResponse(sectionRepository.save(section));
    }

    @Override
    public void deleteSection(Long sectionId) {
        Section section = getSectionById(sectionId);

        sectionRepository.delete(section);
    }

    @Override
    public SectionResponse getSection(Long sectionId) {
        Section section = getSectionById(sectionId);

        return sectionMapper.toSectionResponse(section);
    }

    @Override
    public List<SectionResponse> getAllSections() {
        List<Section> sections = sectionRepository.findAll();

        return sections.stream().map(sectionMapper::toSectionResponse).collect(Collectors.toList());
    }

    private Section getSectionById(Long sectionId) {
        return sectionRepository.findById(sectionId).orElseThrow(
                () -> new AppException(ErrorCode.SECTION_NOT_FOUND)
        );
    }
}
