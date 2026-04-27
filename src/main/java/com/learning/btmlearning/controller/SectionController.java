package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.SectionRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.SectionResponse;
import com.learning.btmlearning.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
public class SectionController {
    private final SectionService sectionService;

    @PostMapping("/sections")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ApiResponse<SectionResponse> createSection(@RequestBody SectionRequest request) {
        return ApiResponse.<SectionResponse>builder()
                .message("Section created successfully")
                .result(sectionService.createSection(request))
                .build();
    }

    @PutMapping("/sections/{sectionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ApiResponse<SectionResponse> updateSection(
            @PathVariable Long sectionId,
            @RequestBody SectionRequest request) {
        return ApiResponse.<SectionResponse>builder()
                .message("Section updated successfully")
                .result(sectionService.updateSection(request, sectionId))
                .build();
    }

    @DeleteMapping("/sections/{sectionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ApiResponse<Void> deleteSection(@PathVariable Long sectionId) {
        sectionService.deleteSection(sectionId);

        return ApiResponse.<Void>builder()
                .message("Section deleted successfully")
                .build();
    }

    @GetMapping("/sections/{sectionId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<SectionResponse> getSection(@PathVariable Long sectionId) {
        return ApiResponse.<SectionResponse>builder()
                .message("Get section successfully")
                .result(sectionService.getSection(sectionId))
                .build();
    }

    @GetMapping("/sections")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<SectionResponse>> getAllSections() {
        return ApiResponse.<List<SectionResponse>>builder()
                .message("Get sections successfully")
                .result(sectionService.getAllSections())
                .build();
    }

    @GetMapping("/courses/{courseId}/sections")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<SectionResponse>> getSectionsByCourse(@PathVariable Long courseId) {
        return ApiResponse.<List<SectionResponse>>builder()
                .message("Get sections by course successfully")
                .result(sectionService.getSectionsByCourse(courseId))
                .build();
    }
}
