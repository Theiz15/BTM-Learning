package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.FileUploadRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.FileUploadResponse;
import com.learning.btmlearning.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileUploadController {
    private final FileUploadService fileUploadService;

    @PostMapping("/files")
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> uploadBath(@ModelAttribute @Valid FileUploadRequest request) {
        List<FileUploadResponse> result = fileUploadService.uploadFile(request);

        ApiResponse<List<FileUploadResponse>> apiResponse = ApiResponse.<List<FileUploadResponse>>builder()
                .code(1000)
                .message("Upload file successful")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
