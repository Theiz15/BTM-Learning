package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.CertificateAutoIssueRequest;
import com.learning.btmlearning.dto.response.CertificateResponse;
import com.learning.btmlearning.dto.response.CertificateVerificationResponse;

import java.util.List;

public interface CertificateService {
    CertificateResponse autoIssueCertificate(CertificateAutoIssueRequest request);

    List<CertificateResponse> getAllCertificates() ;

    CertificateVerificationResponse verifyCertificate(String code);

}
