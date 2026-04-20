package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.CourseStatus;
import com.learning.btmlearning.constant.EnrollmentStatus;
import com.learning.btmlearning.constant.NotificationType;
import com.learning.btmlearning.constant.PaymentStatus;
import com.learning.btmlearning.dto.request.EnrollmentRequest;
import com.learning.btmlearning.dto.request.FilterEnrollmentRequest;
import com.learning.btmlearning.dto.response.EnrollmentResponse;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.Enrollment;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.EnrollmentMapper;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.repository.EnrollmentRepository;
import com.learning.btmlearning.service.EnrollmentService;
import com.learning.btmlearning.utils.SecurityUtil;
import com.learning.btmlearning.service.NotificationService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final CourseRepository courseRepository;
    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    @CacheEvict(value = "recommendations", key = "#result.userId")
    public EnrollmentResponse enroll(EnrollmentRequest request) {
        Enrollment enrollment = enrollmentMapper.toEnrollment(request);

        User user = securityUtil.getCurrentUser();

        checkNotEnrolled(user.getId(), request.getCourseId());

        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(
                () -> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );

        if (course.getStatus() != CourseStatus.PUBLISHED && course.getStatus() != CourseStatus.ACTIVE) {
            throw new RuntimeException("Course is not published");
        }

        if (course.getPrice() != null && course.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("This course is not free. please use VNPAY's getway.");
        }

        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setPaymentStatus(PaymentStatus.FREE);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        try {
            notificationService.notifyUser(
                    user.getId(),
                    "Enrollment Confirmed",
                    "You have successfully enrolled in the course '" + course.getTitle() + "'.",
                    NotificationType.ENROLLMENT_CONFIRMED
            );
        } catch (Exception e) {
            log.error("Failed to send notification", e);
        }

        return enrollmentMapper.toEnrollmentResponse(savedEnrollment);
    }

    @Override
    public EnrollmentResponse cancelEnroll(EnrollmentRequest request, Long enrollmentId) {
        User user = securityUtil.getCurrentUser();

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(
                () -> new RuntimeException("Enrollment not exist")
        );

        checkOwner(user.getId(), enrollment);

        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            throw new RuntimeException("Enrollment is already completed");
        }

        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            throw new RuntimeException("Enrollment is already cancelled");
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);

        return enrollmentMapper.toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public EnrollmentResponse getById(Long enrollmentId) {
        User user = securityUtil.getCurrentUser();

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(
                () -> new RuntimeException("Enrollment not exist")
        );
        checkOwner(user.getId(), enrollment);

        return enrollmentMapper.toEnrollmentResponse(enrollment);
    }

    @Override
    public EnrollmentResponse reactivateEnroll(Long enrollmentId) {
        User user = securityUtil.getCurrentUser();

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(
                () -> new RuntimeException("Enrollment not exist")
        );

        checkOwner(user.getId(), enrollment);

        if (enrollment.getStatus() != EnrollmentStatus.CANCELLED) {
            throw new IllegalStateException("Chỉ có thể kích hoạt lại enrollment đã huỷ");
        }

        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        return enrollmentMapper.toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public Page<EnrollmentResponse> getAllEnrollments(FilterEnrollmentRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNo(), request.getPageSize());
        Specification<Enrollment> spec = buildSpec(request);

        Page<Enrollment> enrollments = enrollmentRepository.findAll(spec, pageable);

        return enrollments.map(enrollmentMapper::toEnrollmentResponse);
    }

    private void checkNotEnrolled (Long userId, Long courseId) {
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new AppException(ErrorCode.ENROLLMENT_EXIST);
        }
    }

    private void checkOwner(Long userId, Enrollment enrollment) {
        if (!enrollment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not permission to enroll");
        }
    }

    private Specification<Enrollment> buildSpec(FilterEnrollmentRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Enrollment, Course> courseJoin = root.join("course");

            if (request.getCourseId() != null) {
                predicates.add(
                        cb.equal(courseJoin.get("id"), request.getCourseId())
                );
            }

            if (request.getUserId() != null) {
                predicates.add(
                        cb.equal(root.get("user").get("id"), request.getUserId())
                );
            }

            if (request.getStatus() != null) {
                predicates.add(
                        cb.equal(root.get("status"), request.getStatus())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
