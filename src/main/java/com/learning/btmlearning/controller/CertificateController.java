package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.request.CertificateAutoIssueRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.CertificateResponse;
import com.learning.btmlearning.dto.response.CertificateVerificationResponse;
import com.learning.btmlearning.service.CertificateService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CertificateController {
    CertificateService certificateService;

    @PostMapping("/auto-issue")
    public ApiResponse<CertificateResponse> autoIssueCertificate(@Valid @RequestBody CertificateAutoIssueRequest request) {
        return ApiResponse.<CertificateResponse>builder()
                .result(certificateService.autoIssueCertificate(request))
                .build();
    }

    @GetMapping("/verify")
    public ApiResponse<CertificateVerificationResponse> verifyCertificate(@RequestParam String code) {
        return ApiResponse.<CertificateVerificationResponse>builder()
                .result(certificateService.verifyCertificate(code))
                .build();
    }

    @GetMapping
    public ApiResponse<List<CertificateResponse>> getAllCertificates() {
        return ApiResponse.<List<CertificateResponse>>builder()
                .result(certificateService.getAllCertificates())
                .build();
    }
}
