package com.springboot.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Value("${app.security.rate-limit.requests-per-second:100}")
    private double requestsPerSecond;

    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(requestsPerSecond);
    }
}