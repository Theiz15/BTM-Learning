package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.FileUploadRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.FileUploadResponse;
import com.learning.btmlearning.dto.response.PdfContentResponse;
import com.learning.btmlearning.dto.response.UploadDocumentResponse;
import com.learning.btmlearning.service.FileUploadService;
import com.learning.btmlearning.service.impl.CloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {
    private final FileUploadService fileUploadService;
    private final CloudinaryService cloudinaryService;

    @PostMapping("/files")
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> uploadBath(@ModelAttribute @Valid FileUploadRequest request) {
        List<FileUploadResponse> result = cloudinaryService.uploadFile(request);

        ApiResponse<List<FileUploadResponse>> apiResponse = ApiResponse.<List<FileUploadResponse>>builder()
                .code(1000)
                .message("Upload file successful")
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<ApiResponse<UploadDocumentResponse>> uploadPdf(@ModelAttribute MultipartFile file) throws IOException {
            UploadDocumentResponse result = fileUploadService.uploadPdf(file);
            ApiResponse<UploadDocumentResponse> apiResponse = ApiResponse.<UploadDocumentResponse>builder()
                    .result(result)
                    .message("Upload document successful")
                    .build();

            return ResponseEntity.ok(apiResponse);
    }

    // -----------------------------------------------
    // GET /api/pdf/read/{fileId}
    // Đọc toàn bộ nội dung PDF (text + metadata)
    // -----------------------------------------------
    @GetMapping("/read/{fileId}")
    public ResponseEntity<ApiResponse<PdfContentResponse>> readPdf(@PathVariable String fileId) {
            PdfContentResponse response = fileUploadService.readPdf(fileId);

            ApiResponse<PdfContentResponse> apiResponse = ApiResponse.<PdfContentResponse>builder()
                    .result(response)
                    .message("Read document successful")
                    .build();

            return ResponseEntity.ok(apiResponse);
    }

    // -----------------------------------------------
    // GET /api/pdf/read/{fileId}/page/{pageNumber}
    // Đọc nội dung 1 trang cụ thể
    // -----------------------------------------------
    @GetMapping("/read/{fileId}/page/{pageNumber}")
    public ResponseEntity<?> readPdfPage(
            @PathVariable String fileId,
            @PathVariable int pageNumber) {
        try {
            String pageText = fileUploadService.readPdfPage(fileId, pageNumber);
            return ResponseEntity.ok(Map.of(
                    "fileId", fileId,
                    "page", pageNumber,
                    "content", pageText
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error when reading page {} of file: {}", pageNumber, fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi server: " + e.getMessage()));
        }
    }
}
