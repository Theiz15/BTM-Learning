package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.constant.CourseStatus;
import com.learning.btmlearning.constant.EnrollmentStatus;
import com.learning.btmlearning.constant.PaymentStatus;
import com.learning.btmlearning.constant.UserRole;
import com.learning.btmlearning.dto.response.AnalyticsOverviewResponse;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.Enrollment;
import com.learning.btmlearning.entity.Payment;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.repository.EnrollmentRepository;
import com.learning.btmlearning.repository.PaymentRepository;
import com.learning.btmlearning.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final SecurityUtil securityUtil;

    public AnalyticsOverviewResponse getOverview(int months, int topLimit, int recentLimit) {
        int safeMonths = Math.max(1, Math.min(months, 24));
        int safeTopLimit = Math.max(1, Math.min(topLimit, 20));
        int safeRecentLimit = Math.max(1, Math.min(recentLimit, 50));

        User currentUser = securityUtil.getCurrentUser();
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        List<Course> courses = isAdmin
                ? courseRepository.findAll()
                : courseRepository.findAllByInstructorId(currentUser.getId());

        List<Enrollment> enrollments = isAdmin
                ? enrollmentRepository.findAll()
                : enrollmentRepository.findAllByInstructorId(currentUser.getId());

        List<Payment> payments = isAdmin
                ? paymentRepository.findAllWithDetails()
                : paymentRepository.findAllByInstructorIdWithDetails(currentUser.getId());

        List<Course> pendingCourses = isAdmin
                ? courseRepository.findByStatusOrderByCreateAtDesc(CourseStatus.PENDING)
                : courseRepository.findByInstructorIdAndStatusOrderByCreateAtDesc(currentUser.getId(), CourseStatus.PENDING);

        BigDecimal totalRevenue = payments.stream()
                .filter(this::isSuccessfulPayment)
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        YearMonth currentMonth = YearMonth.now();
        BigDecimal thisMonthRevenue = payments.stream()
                .filter(this::isSuccessfulPayment)
                .filter(payment -> YearMonth.from(resolvePaymentDateTime(payment)).equals(currentMonth))
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long publishedCourses = courses.stream()
                .filter(course -> course.getStatus() == CourseStatus.ACTIVE || course.getStatus() == CourseStatus.PUBLISHED)
                .count();

        long pendingCourseCount = courses.stream()
                .filter(course -> course.getStatus() == CourseStatus.PENDING)
                .count();

        long completedEnrollments = enrollments.stream()
                .filter(enrollment -> enrollment.getStatus() == EnrollmentStatus.COMPLETED)
                .count();

        long totalLearners = enrollments.stream()
                .map(Enrollment::getUser)
                .filter(user -> user != null)
                .map(User::getId)
                .filter(id -> id != null)
                .distinct()
                .count();

        AnalyticsOverviewResponse.Summary summary = AnalyticsOverviewResponse.Summary.builder()
                .totalCourses(courses.size())
                .publishedCourses(publishedCourses)
                .pendingCourses(pendingCourseCount)
                .totalEnrollments(enrollments.size())
                .completedEnrollments(completedEnrollments)
                .totalLearners(totalLearners)
                .totalRevenue(totalRevenue)
                .thisMonthRevenue(thisMonthRevenue)
                .build();

        List<AnalyticsOverviewResponse.MonthlyRevenuePoint> monthlyRevenue = buildMonthlyRevenue(payments, safeMonths);
        List<AnalyticsOverviewResponse.TopCourseRevenue> topCourses = buildTopCourses(courses, enrollments, payments, safeTopLimit);
        List<AnalyticsOverviewResponse.RecentTransaction> recentTransactions = buildRecentTransactions(payments, safeRecentLimit);
        List<AnalyticsOverviewResponse.PendingCourseItem> pendingItems = buildPendingCourses(pendingCourses);

        return AnalyticsOverviewResponse.builder()
                .summary(summary)
                .monthlyRevenue(monthlyRevenue)
                .topCourses(topCourses)
                .recentTransactions(recentTransactions)
                .pendingCourses(pendingItems)
                .build();
    }

    private List<AnalyticsOverviewResponse.MonthlyRevenuePoint> buildMonthlyRevenue(List<Payment> payments, int months) {
        YearMonth now = YearMonth.now();
        Map<YearMonth, BigDecimal> buckets = new LinkedHashMap<>();

        for (int i = months - 1; i >= 0; i--) {
            buckets.put(now.minusMonths(i), BigDecimal.ZERO);
        }

        for (Payment payment : payments) {
            if (!isSuccessfulPayment(payment)) {
                continue;
            }

            YearMonth bucket = YearMonth.from(resolvePaymentDateTime(payment));
            if (!buckets.containsKey(bucket)) {
                continue;
            }

            BigDecimal amount = Optional.ofNullable(payment.getAmount()).orElse(BigDecimal.ZERO);
            buckets.put(bucket, buckets.get(bucket).add(amount));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        List<AnalyticsOverviewResponse.MonthlyRevenuePoint> result = new ArrayList<>();

        for (Map.Entry<YearMonth, BigDecimal> entry : buckets.entrySet()) {
            result.add(AnalyticsOverviewResponse.MonthlyRevenuePoint.builder()
                    .month(entry.getKey().format(formatter))
                    .revenue(entry.getValue())
                    .build());
        }

        return result;
    }

    private List<AnalyticsOverviewResponse.TopCourseRevenue> buildTopCourses(
            List<Course> courses,
            List<Enrollment> enrollments,
            List<Payment> payments,
            int topLimit
    ) {
        Map<Long, String> courseTitleMap = courses.stream()
                .filter(course -> course != null)
                .filter(course -> course.getId() != null)
                .collect(Collectors.toMap(
                        Course::getId,
                        course -> Optional.ofNullable(course.getTitle()).orElse("Unknown Course"),
                        (left, right) -> left
                ));

        Map<Long, Long> enrollmentCountMap = enrollments.stream()
                .filter(enrollment -> enrollment != null)
                .filter(enrollment -> enrollment.getCourse() != null && enrollment.getCourse().getId() != null)
                .collect(Collectors.groupingBy(
                        enrollment -> enrollment.getCourse().getId(),
                        Collectors.counting()
                ));

        Map<Long, BigDecimal> revenueByCourseMap = new LinkedHashMap<>();
        for (Payment payment : payments) {
            if (!isSuccessfulPayment(payment)) {
                continue;
            }

            Course course = payment.getCourse();
            if (course == null || course.getId() == null) {
                continue;
            }

            Long courseId = course.getId();
            String courseTitle = course.getTitle();
            courseTitleMap.putIfAbsent(courseId, courseTitle);

            BigDecimal amount = Optional.ofNullable(payment.getAmount()).orElse(BigDecimal.ZERO);
            revenueByCourseMap.put(courseId, revenueByCourseMap.getOrDefault(courseId, BigDecimal.ZERO).add(amount));
        }

        return revenueByCourseMap.entrySet().stream()
                .sorted((left, right) -> right.getValue().compareTo(left.getValue()))
                .limit(topLimit)
                .map(entry -> AnalyticsOverviewResponse.TopCourseRevenue.builder()
                        .courseId(entry.getKey())
                        .courseTitle(courseTitleMap.getOrDefault(entry.getKey(), "Unknown Course"))
                        .enrollments(enrollmentCountMap.getOrDefault(entry.getKey(), 0L))
                        .revenue(entry.getValue())
                        .build())
                .toList();
    }

    private List<AnalyticsOverviewResponse.RecentTransaction> buildRecentTransactions(List<Payment> payments, int recentLimit) {
        return payments.stream()
                .sorted(Comparator.comparing(this::resolvePaymentDateTime).reversed())
                .limit(recentLimit)
                .map(payment -> AnalyticsOverviewResponse.RecentTransaction.builder()
                        .paymentId(payment.getId())
                        .studentName(resolveUserName(payment.getUser()))
                        .studentEmail(payment.getUser() == null ? null : payment.getUser().getEmail())
                        .courseId(payment.getCourse() == null ? null : payment.getCourse().getId())
                        .courseTitle(payment.getCourse() == null ? null : payment.getCourse().getTitle())
                        .amount(Optional.ofNullable(payment.getAmount()).orElse(BigDecimal.ZERO))
                        .status(payment.getStatus() == null ? "UNKNOWN" : payment.getStatus().name())
                        .paidAt(resolvePaymentDateTime(payment))
                        .build())
                .toList();
    }

    private List<AnalyticsOverviewResponse.PendingCourseItem> buildPendingCourses(List<Course> pendingCourses) {
        return pendingCourses.stream()
                .sorted(Comparator.comparing(
                        (Course course) -> Optional.ofNullable(course.getUpdateAt()).orElse(course.getCreateAt()),
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed())
                .limit(10)
                .map(course -> AnalyticsOverviewResponse.PendingCourseItem.builder()
                        .courseId(course.getId())
                        .title(course.getTitle())
                        .categoryName(course.getCategory() == null ? null : course.getCategory().getName())
                        .instructorName(course.getInstructor() == null ? null : course.getInstructor().getFullName())
                        .submittedAt(Optional.ofNullable(course.getUpdateAt()).orElse(course.getCreateAt()))
                        .build())
                .toList();
    }

    private LocalDateTime resolvePaymentDateTime(Payment payment) {
        if (payment.getPaidAt() != null) {
            return payment.getPaidAt();
        }

        if (payment.getCreatedAt() != null) {
            return payment.getCreatedAt();
        }

        return LocalDateTime.MIN;
    }

    private boolean isSuccessfulPayment(Payment payment) {
                return payment != null && payment.getStatus() == PaymentStatus.SUCCESS;
    }

    private String resolveUserName(User user) {
        if (user == null) {
            return "Unknown User";
        }

        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            return user.getFullName();
        }

        return user.getEmail();
    }
}
