package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.CertificateAutoIssueRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.CertificateResponse;
import com.learning.btmlearning.dto.response.CertificateVerificationResponse;
import com.learning.btmlearning.service.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/certificates")
@RequiredArgsConstructor
public class CertificateController {
    private final CertificateService certificateService;

    @PostMapping("/auto-issue")
    public ResponseEntity<ApiResponse<CertificateResponse>> autoIssueCertificate(@Valid @RequestBody CertificateAutoIssueRequest request) {
        ApiResponse<CertificateResponse> apiResponse = ApiResponse.<CertificateResponse>builder()
                .result(certificateService.autoIssueCertificate(request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<CertificateVerificationResponse>> verifyCertificate(@RequestParam String code) {
        ApiResponse<CertificateVerificationResponse> apiResponse = ApiResponse.<CertificateVerificationResponse>builder()
                .result(certificateService.verifyCertificate(code))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CertificateResponse>>> getAllCertificates() {
        ApiResponse<List<CertificateResponse>> apiResponse = ApiResponse.<List<CertificateResponse>>builder()
                .result(certificateService.getAllCertificates())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
