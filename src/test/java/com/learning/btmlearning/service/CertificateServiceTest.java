package com.learning.btmlearning.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    CertificateRepository certificateRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CourseService courseService;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    CertificateService certificateService;

    @Test
    void autoIssueCertificate_validRequest_issuesCertificateAndNotifies() {
        User user = new User();
        user.setId(1L);

        Course course = new Course();
        course.setId(2L);
        course.setTitle("Java Core");

        CertificateAutoIssueRequest request = CertificateAutoIssueRequest.builder()
                .userId(1L)
                .courseId(2L)
                .completedLessons(10)
                .totalLessons(10)
                .quizPassed(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseService.findCourse(2L)).thenReturn(course);
        when(certificateRepository.existsByUserIdAndCourseId(1L, 2L)).thenReturn(false);
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(invocation -> {
            Certificate saved = invocation.getArgument(0);
            saved.setId(99L);
            saved.setIssuedAt(LocalDateTime.now());
            return saved;
        });

        CertificateResponse response = certificateService.autoIssueCertificate(request);

        assertEquals(99L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(2L, response.getCourseId());
        assertNotNull(response.getIssuedAt());
        assertNotNull(response.getCode());
        assertTrue(response.getCode().startsWith("CERT-"));
        assertEquals(17, response.getCode().length());

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService).notifyUser(
                eq(1L),
                eq("Certificate issued"),
                messageCaptor.capture(),
                eq(NotificationType.CERTIFICATE_ISSUED)
        );
        assertTrue(messageCaptor.getValue().contains("Java Core"));
        assertTrue(messageCaptor.getValue().contains(response.getCode()));
    }

    @Test
    void autoIssueCertificate_userNotFound_throwsUserNotExisted() {
        CertificateAutoIssueRequest request = CertificateAutoIssueRequest.builder()
                .userId(1L)
                .courseId(2L)
                .completedLessons(10)
                .totalLessons(10)
                .quizPassed(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> certificateService.autoIssueCertificate(request));
        assertEquals(ErrorCode.USER_NOT_EXISTED, exception.getErrorCode());
    }

    @Test
    void autoIssueCertificate_incompleteLessons_throwsConditionNotMet() {
        User user = new User();
        user.setId(1L);

        Course course = new Course();
        course.setId(2L);

        CertificateAutoIssueRequest request = CertificateAutoIssueRequest.builder()
                .userId(1L)
                .courseId(2L)
                .completedLessons(9)
                .totalLessons(10)
                .quizPassed(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseService.findCourse(2L)).thenReturn(course);

        AppException exception = assertThrows(AppException.class, () -> certificateService.autoIssueCertificate(request));
        assertEquals(ErrorCode.CERTIFICATE_ISSUE_CONDITION_NOT_MET, exception.getErrorCode());
    }

    @Test
    void autoIssueCertificate_quizNotPassed_throwsConditionNotMet() {
        User user = new User();
        user.setId(1L);

        Course course = new Course();
        course.setId(2L);

        CertificateAutoIssueRequest request = CertificateAutoIssueRequest.builder()
                .userId(1L)
                .courseId(2L)
                .completedLessons(10)
                .totalLessons(10)
                .quizPassed(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseService.findCourse(2L)).thenReturn(course);

        AppException exception = assertThrows(AppException.class, () -> certificateService.autoIssueCertificate(request));
        assertEquals(ErrorCode.CERTIFICATE_ISSUE_CONDITION_NOT_MET, exception.getErrorCode());
    }

    @Test
    void autoIssueCertificate_alreadyIssued_throwsAlreadyIssued() {
        User user = new User();
        user.setId(1L);

        Course course = new Course();
        course.setId(2L);

        CertificateAutoIssueRequest request = CertificateAutoIssueRequest.builder()
                .userId(1L)
                .courseId(2L)
                .completedLessons(10)
                .totalLessons(10)
                .quizPassed(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseService.findCourse(2L)).thenReturn(course);
        when(certificateRepository.existsByUserIdAndCourseId(1L, 2L)).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> certificateService.autoIssueCertificate(request));
        assertEquals(ErrorCode.CERTIFICATE_ALREADY_ISSUED, exception.getErrorCode());
    }

    @Test
    void verifyCertificate_existingCode_returnsValidResponse() {
        User user = new User();
        user.setId(1L);

        Course course = new Course();
        course.setId(2L);

        Certificate certificate = new Certificate();
        certificate.setId(5L);
        certificate.setCode("CERT-ABC123DEF456");
        certificate.setUser(user);
        certificate.setCourse(course);
        certificate.setIssuedAt(LocalDateTime.now());

        when(certificateRepository.findByCode("CERT-ABC123DEF456")).thenReturn(Optional.of(certificate));

        CertificateVerificationResponse response = certificateService.verifyCertificate("CERT-ABC123DEF456");

        assertTrue(response.isValid());
        assertEquals("Certificate is valid", response.getMessage());
        assertNotNull(response.getCertificate());
        assertEquals(5L, response.getCertificate().getId());
    }

    @Test
    void verifyCertificate_unknownCode_throwsCertificateNotFound() {
        when(certificateRepository.findByCode("CERT-NOTFOUND")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> certificateService.verifyCertificate("CERT-NOTFOUND"));
        assertEquals(ErrorCode.CERTIFICATE_NOT_FOUND, exception.getErrorCode());
    }
}
