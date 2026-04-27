package com.learning.btmlearning.controller;

import com.learning.btmlearning.dto.response.AnalyticsOverviewResponse;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.service.impl.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public ApiResponse<AnalyticsOverviewResponse> getOverview(
            @RequestParam(defaultValue = "12") int months,
            @RequestParam(defaultValue = "5") int top,
            @RequestParam(defaultValue = "10") int recent
    ) {
        return ApiResponse.<AnalyticsOverviewResponse>builder()
                .message("Analytics retrieved successfully")
                .result(analyticsService.getOverview(months, top, recent))
                .build();
    }
}
