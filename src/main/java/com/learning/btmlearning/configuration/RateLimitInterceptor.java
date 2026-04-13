package com.learning.btmlearning.configuration;

import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.service.impl.RateLimitingService;
import com.learning.btmlearning.utils.SecurityUtil;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE ,makeFinal = true)
public class RateLimitInterceptor implements HandlerInterceptor {

    RateLimitingService rateLimitingService;
    SecurityUtil securityUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            return true;
        }

        try {
            Long userId = securityUtil.getCurrentUser().getId();

            Bucket bucket = rateLimitingService.resolveBucket(userId);

            if (bucket.tryConsume(1)) {
                return true;
            }

            throw new AppException(ErrorCode.UNAUTHENTICATED);

        } catch (Exception e) {
            return true;
        }
    }
}