package com.springboot.controller.v2;

import com.google.common.util.concurrent.RateLimiter;
import com.springboot.exception.TooManyRequestsException;
import com.springboot.metrics.GreetingMetrics;
import com.springboot.service.GreetingService;
import com.springboot.util.SecurityUtils;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v2")
@Validated
@Tag(name = "Greeting API v2", description = "Enhanced multilingual greeting service with additional metadata")
public class GreetingV2Controller {

    private static final Logger logger = LoggerFactory.getLogger(GreetingV2Controller.class);
    private final GreetingService greetingService;
    private final RateLimiter rateLimiter;
    private final GreetingMetrics greetingMetrics;

    @Autowired
    public GreetingV2Controller(GreetingService greetingService, RateLimiter rateLimiter, GreetingMetrics greetingMetrics) {
        this.greetingService = greetingService;
        this.rateLimiter = rateLimiter;
        this.greetingMetrics = greetingMetrics;
    }

    @Operation(
        summary = "Get enhanced greeting with metadata",
        description = """
            Returns a greeting message with additional metadata including timestamp,
            language information, and request tracking.
            """,
        tags = {"Greeting API v2"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Enhanced greeting retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "message": "¡Hola Mundo!",
                        "language": "es",
                        "timestamp": "2024-01-15T10:30:00",
                        "version": "v2",
                        "metadata": {
                            "isSupported": true,
                            "characterCount": 12,
                            "encoding": "UTF-8"
                        }
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid language code format",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "429", 
            description = "Rate limit exceeded",
            content = @Content(mediaType = "application/json")
        )
    })
    @GetMapping("/greeting")
    public ResponseEntity<Map<String, Object>> getEnhancedGreeting(
            @RequestParam(value = "lang", defaultValue = "en")
            @Pattern(regexp = "^[a-z]{2}$", message = "Language code must be exactly 2 lowercase letters")
            @Size(min = 2, max = 2, message = "Language code must be exactly 2 characters")
            @NotBlank(message = "Language code cannot be blank")
            String language,
            HttpServletRequest request) {
        
        Timer.Sample sample = greetingMetrics.startTimer();
        
        try {
            // Rate limiting check
            if (!rateLimiter.tryAcquire()) {
                String clientIP = SecurityUtils.getClientIP(request);
                logger.warn("Rate limit exceeded for IP: {}", clientIP);
                greetingMetrics.incrementRateLimitExceeded(clientIP);
                throw new TooManyRequestsException("Rate limit exceeded. Please try again later.");
            }
            
            // Advanced input sanitization
            String sanitizedLanguage = SecurityUtils.sanitizeInput(language);
            if (!SecurityUtils.isValidLanguageCode(sanitizedLanguage)) {
                logger.warn("Invalid language code received: {} from IP: {}", 
                           language, SecurityUtils.getClientIP(request));
                throw new IllegalArgumentException("Invalid language code format");
            }
            
            String clientIP = SecurityUtils.getClientIP(request);
            logger.info("Enhanced greeting requested for language: {} from IP: {}", sanitizedLanguage, clientIP);
            
            String greeting = greetingService.getGreeting(sanitizedLanguage);
            boolean isSupported = greetingService.isLanguageSupported(sanitizedLanguage);
            
            Map<String, Object> response = Map.of(
                "message", greeting,
                "language", sanitizedLanguage,
                "timestamp", LocalDateTime.now().toString(),
                "version", "v2",
                "metadata", Map.of(
                    "isSupported", isSupported,
                    "characterCount", greeting.length(),
                    "encoding", "UTF-8",
                    "clientIP", clientIP
                )
            );
            
            greetingMetrics.incrementRequests(sanitizedLanguage, "v2");
            return ResponseEntity.ok(response);
            
        } finally {
            greetingMetrics.recordTimer(sample, language);
        }
    }

    @Operation(
        summary = "Get API version information",
        description = "Returns information about the current API version and available endpoints",
        tags = {"Greeting API v2"}
    )
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> apiInfo = Map.of(
            "version", "2.0",
            "description", "Enhanced Greeting API with metadata support",
            "features", new String[]{
                "Enhanced response format",
                "Request metadata",
                "Timestamp tracking",
                "Character count",
                "Language support detection"
            },
            "endpoints", Map.of(
                "greeting", "/api/v2/greeting",
                "info", "/api/v2/info"
            ),
            "compatibility", Map.of(
                "v1", "Available at /",
                "v2", "Current version"
            )
        );
        
        return ResponseEntity.ok(apiInfo);
    }
}