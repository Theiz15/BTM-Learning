package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.SectionRequest;
import com.learning.btmlearning.dto.response.SectionResponse;

import java.util.List;

public interface SectionService {
    SectionResponse createSection (SectionRequest request);
    SectionResponse updateSection (SectionRequest request, Long sectionId);
    void deleteSection (Long sectionId);
    SectionResponse getSection (Long sectionId);
    List<SectionResponse> getAllSections();
    List<SectionResponse> getSectionsByCourse(Long courseId);
}
