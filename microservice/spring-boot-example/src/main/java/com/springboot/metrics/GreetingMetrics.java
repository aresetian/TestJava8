package com.springboot.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GreetingMetrics {

    private final Counter greetingRequests;
    private final Counter rateLimitExceeded;
    private final Timer greetingTimer;
    private final MeterRegistry meterRegistry;

    @Autowired
    public GreetingMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        this.greetingRequests = Counter.builder("greeting_requests_total")
            .description("Total greeting requests")
            .tag("version", "v1")
            .register(meterRegistry);
            
        this.rateLimitExceeded = Counter.builder("greeting_rate_limit_exceeded_total")
            .description("Total rate limit exceeded events")
            .register(meterRegistry);
            
        this.greetingTimer = Timer.builder("greeting_duration_seconds")
            .description("Greeting processing time")
            .register(meterRegistry);
    }

    public void incrementRequests(String language, String endpoint) {
        Counter.builder("greeting_requests_total")
            .tag("language", language)
            .tag("endpoint", endpoint)
            .register(meterRegistry)
            .increment();
    }

    public void incrementRateLimitExceeded(String clientIP) {
        Counter.builder("greeting_rate_limit_exceeded_total")
            .tag("client_ip", clientIP)
            .register(meterRegistry)
            .increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordTimer(Timer.Sample sample, String language) {
        sample.stop(Timer.builder("greeting_duration_seconds")
            .tag("language", language)
            .register(meterRegistry));
    }
}