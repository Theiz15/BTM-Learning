package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.SectionRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.SectionResponse;
import com.learning.btmlearning.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SectionController {
    private final SectionService sectionService;

    @PostMapping("/sections")
    public ResponseEntity<ApiResponse<SectionResponse>> createSection(@RequestBody @Valid SectionRequest request) {
        SectionResponse result = sectionService.createSection(request);

        ApiResponse<SectionResponse> apiResponse = ApiResponse.<SectionResponse>builder()
                .message("Section was created successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/section/{sectionId}")
    public ResponseEntity<ApiResponse<SectionResponse>> updateSection (@RequestBody @Valid SectionRequest request, @PathVariable Long sectionId) {
        SectionResponse result = sectionService.updateSection(request, sectionId);

        ApiResponse<SectionResponse> apiResponse = ApiResponse.<SectionResponse>builder()
                .message("Section was updated successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/section/{sectionId}")
    public ResponseEntity<ApiResponse<Void>> deleteSection (@PathVariable Long sectionId) {
        sectionService.deleteSection(sectionId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Section was deleted successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/section/{sectionId}")
    public ResponseEntity<ApiResponse<SectionResponse>> getSection (@PathVariable Long sectionId) {
        SectionResponse result = sectionService.getSection(sectionId);

        ApiResponse<SectionResponse> apiResponse = ApiResponse.<SectionResponse>builder()
                .message("Section was get successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/section")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getAllSections() {
        List<SectionResponse> result = sectionService.getAllSections();

        ApiResponse<List<SectionResponse>> apiResponse = ApiResponse.<List<SectionResponse>>builder()
                .message("All sections was get successfully")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
