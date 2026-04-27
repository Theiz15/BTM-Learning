package com.learning.btmlearning.service;

import com.learning.btmlearning.constant.EnrollmentStatus;
import com.learning.btmlearning.constant.NotificationType;
import com.learning.btmlearning.dto.request.CourseReviewCreationRequest;
import com.learning.btmlearning.dto.response.CourseReviewResponse;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.CourseReview;
import com.learning.btmlearning.entity.Enrollment;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.repository.CourseReviewRepository;
import com.learning.btmlearning.repository.EnrollmentRepository;
import com.learning.btmlearning.utils.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseReviewService {
    CourseReviewRepository courseReviewRepository;
    CourseService courseService;
    EnrollmentRepository enrollmentRepository;
    NotificationService notificationService;
    SecurityUtil securityUtil;

    public CourseReviewResponse createReview(CourseReviewCreationRequest request) {
        Course course = courseService.findCourse(request.getCourseId());
        User user = securityUtil.getCurrentUser();

        Enrollment enrollment = enrollmentRepository
                .findByUserIdAndCourseId(user.getId(), course.getId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_REVIEW_REQUIRES_COMPLETION));

        boolean hasCompletedCourse = enrollment.getStatus() == EnrollmentStatus.COMPLETED
                || enrollment.getCompletedAt() != null
                || (enrollment.getProgressPercent() != null && enrollment.getProgressPercent() >= 100f);

        if (!hasCompletedCourse) {
            throw new AppException(ErrorCode.COURSE_REVIEW_REQUIRES_COMPLETION);
        }

        if (courseReviewRepository.existsByCourseIdAndUserId(course.getId(), user.getId())) {
            throw new AppException(ErrorCode.COURSE_REVIEW_ALREADY_EXISTS);
        }

        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = courseReviewRepository.save(review);
        updateCourseRatingSummary(course.getId());

        notificationService.notifyUser(
                user.getId(),
                "Review submitted",
                "Your review for course '" + course.getTitle() + "' has been recorded.",
                NotificationType.REVIEW_RECEIVED
        );

        return mapToResponse(review);
    }

    public List<CourseReviewResponse> getReviewsByCourse(Long courseId) {
        courseService.findCourse(courseId);
        return courseReviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void updateCourseRatingSummary(Long courseId) {
        CourseReviewRepository.RatingSummary summary = courseReviewRepository.calculateCourseRatingSummary(courseId);

        if (summary != null) {
            double avg = (summary.getAvgRating() != null) ? summary.getAvgRating() : 0.0;
            long count = (summary.getRatingCount() != null) ? summary.getRatingCount() : 0L;

            courseService.updateCourseRating(courseId, avg, count);
        }
    }

    private CourseReviewResponse mapToResponse(CourseReview review) {
        return CourseReviewResponse.builder()
                .id(review.getId())
                .courseId(review.getCourse().getId())
                .userId(review.getUser().getId())
            .userFullName(review.getUser().getFullName())
            .userAvatarUrl(review.getUser().getAvatarUrl())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
