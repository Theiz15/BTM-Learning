package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.NotificationType;
import com.learning.btmlearning.dto.request.CertificateAutoIssueRequest;
import com.learning.btmlearning.dto.response.CertificateResponse;
import com.learning.btmlearning.dto.response.CertificateVerificationResponse;
import com.learning.btmlearning.entity.Certificate;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.repository.CertificateRepository;
import com.learning.btmlearning.repository.UserRepository;
import com.learning.btmlearning.service.CertificateService;
import com.learning.btmlearning.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CertificateServiceImpl implements CertificateService {
    CertificateRepository certificateRepository;
    UserRepository userRepository;
    CourseService courseService;
    NotificationServiceImpl notificationService;

    @Override
    @Transactional
    public CertificateResponse autoIssueCertificate(CertificateAutoIssueRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Course course = courseService.findCourse(request.getCourseId());

        if (request.getCompletedLessons() < request.getTotalLessons() || !request.getQuizPassed()) {
            throw new AppException(ErrorCode.CERTIFICATE_ISSUE_CONDITION_NOT_MET);
        }

        if (certificateRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            throw new AppException(ErrorCode.CERTIFICATE_ALREADY_ISSUED);
        }

        Certificate certificate = new Certificate();
        certificate.setUser(user);
        certificate.setCourse(course);
        certificate.setCode(generateCode());

        certificate = certificateRepository.save(certificate);

        notificationService.notifyUser(
                user.getId(),
                "Certificate Awarded",
                "Your certificate for '" + course.getTitle() + "' has been issued successfully. "
                        + "Certificate code: " + certificate.getCode() + ".",
                NotificationType.CERTIFICATE_ISSUED
        );

        return mapToResponse(certificate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificateResponse> getAllCertificates() {
        return certificateRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateVerificationResponse verifyCertificate(String code) {
        String normalizedCode = code == null ? "" : code.trim();
        Certificate certificate = certificateRepository.findByCodeIgnoreCaseOrCertCodeIgnoreCase(normalizedCode, normalizedCode)
                .orElseThrow(() -> new AppException(ErrorCode.CERTIFICATE_NOT_FOUND));

        return CertificateVerificationResponse.builder()
                .valid(true)
                .message("Certificate is valid")
                .certificate(mapToResponse(certificate))
                .build();
    }

    private String generateCode() {
        return "CERT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private CertificateResponse mapToResponse(Certificate certificate) {
        return CertificateResponse.builder()
                .id(certificate.getId())
                .code(certificate.getCode())
                .userId(certificate.getUser() != null ? certificate.getUser().getId() : null)
                .courseId(certificate.getCourse() != null ? certificate.getCourse().getId() : null)
                .issuedAt(certificate.getIssuedAt())
                .build();
    }
}
