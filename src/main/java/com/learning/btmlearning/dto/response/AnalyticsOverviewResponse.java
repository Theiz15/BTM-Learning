package com.learning.btmlearning.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnalyticsOverviewResponse {
    Summary summary;

    @Builder.Default
    List<MonthlyRevenuePoint> monthlyRevenue = new ArrayList<>();

    @Builder.Default
    List<TopCourseRevenue> topCourses = new ArrayList<>();

    @Builder.Default
    List<RecentTransaction> recentTransactions = new ArrayList<>();

    @Builder.Default
    List<PendingCourseItem> pendingCourses = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Summary {
        long totalCourses;
        long publishedCourses;
        long pendingCourses;
        long totalEnrollments;
        long completedEnrollments;
        long totalLearners;
        BigDecimal totalRevenue;
        BigDecimal thisMonthRevenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MonthlyRevenuePoint {
        String month;
        BigDecimal revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TopCourseRevenue {
        Long courseId;
        String courseTitle;
        long enrollments;
        BigDecimal revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RecentTransaction {
        Long paymentId;
        String studentName;
        String studentEmail;
        Long courseId;
        String courseTitle;
        BigDecimal amount;
        String status;
        LocalDateTime paidAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PendingCourseItem {
        Long courseId;
        String title;
        String categoryName;
        String instructorName;
        LocalDateTime submittedAt;
    }
}
