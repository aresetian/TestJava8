package com.springboot.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.Info;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom health checks and info contributors for the greeting microservice
 */
@Component
public class HealthConfig implements HealthIndicator, InfoContributor {

    private final RateLimiter rateLimiter;
    private final AtomicLong requestCount = new AtomicLong(0);
    private final long startTime = System.currentTimeMillis();

    @Autowired
    public HealthConfig(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public Health health() {
        boolean isHealthy = checkApplicationHealth();
        
        if (isHealthy) {
            return Health.up()
                    .withDetail("rate-limiter", "Available permits: " + rateLimiter.toString())
                    .withDetail("uptime-seconds", (System.currentTimeMillis() - startTime) / 1000)
                    .withDetail("total-requests", requestCount.get())
                    .withDetail("memory-usage", getMemoryUsage())
                    .build();
        } else {
            return Health.down()
                    .withDetail("reason", "Application health check failed")
                    .build();
        }
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("greeting-service", Map.of(
                "version", "1.0.0",
                "description", "Multilingual greeting microservice",
                "supported-languages", 9,
                "features", new String[]{
                    "Rate Limiting",
                    "Caching", 
                    "Security",
                    "Async Processing",
                    "Metrics",
                    "Multi-language Support"
                }
        ));
        
        builder.withDetail("runtime", Map.of(
                "java-version", System.getProperty("java.version"),
                "java-vendor", System.getProperty("java.vendor"),
                "os-name", System.getProperty("os.name"),
                "processors", Runtime.getRuntime().availableProcessors()
        ));
    }

    private boolean checkApplicationHealth() {
        // Check if rate limiter is functioning
        boolean rateLimiterHealthy = rateLimiter != null;
        
        // Check memory usage (fail if over 90%)
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        double memoryUsage = (double) usedMemory / maxMemory;
        boolean memoryHealthy = memoryUsage < 0.9;
        
        return rateLimiterHealthy && memoryHealthy;
    }

    private Map<String, Object> getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        
        return Map.of(
                "used-mb", usedMemory / (1024 * 1024),
                "max-mb", maxMemory / (1024 * 1024),
                "usage-percent", Math.round((double) usedMemory / maxMemory * 100)
        );
    }

    public void incrementRequestCount() {
        requestCount.incrementAndGet();
    }
}