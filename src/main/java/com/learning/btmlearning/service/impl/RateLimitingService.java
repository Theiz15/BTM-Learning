package com.learning.btmlearning.service.impl;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {
    Map<Long, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(Long userId) {
        return buckets.computeIfAbsent(userId, this::createNewBucket);
    }

    private Bucket createNewBucket(Long userId) {
        Bandwidth limit = Bandwidth.classic(20, Refill.intervally(20, Duration.ofHours(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
