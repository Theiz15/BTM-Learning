package com.learning.btmlearning.service;

import com.learning.btmlearning.constant.NotificationType;
import com.learning.btmlearning.dto.request.CourseReviewCreationRequest;
import com.learning.btmlearning.dto.response.CourseReviewResponse;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.CourseReview;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.repository.CourseReviewRepository;
import com.learning.btmlearning.repository.UserRepository;
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
    UserRepository userRepository;
    NotificationService notificationService;

    public CourseReviewResponse createReview(CourseReviewCreationRequest request) {
        Course course = courseService.findCourse(request.getCourseId());
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

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
        Object[] summary = courseReviewRepository.calculateCourseRatingSummary(courseId);
        double averageRating = ((Number) summary[0]).doubleValue();
        long ratingCount = ((Number) summary[1]).longValue();
        courseService.updateCourseRating(courseId, averageRating, ratingCount);
    }

    private CourseReviewResponse mapToResponse(CourseReview review) {
        return CourseReviewResponse.builder()
                .id(review.getId())
                .courseId(review.getCourse().getId())
                .userId(review.getUser().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
