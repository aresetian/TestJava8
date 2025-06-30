package com.springboot.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.springboot.exception.TooManyRequestsException;
import com.springboot.metrics.GreetingMetrics;
import com.springboot.service.GreetingService;
import com.springboot.util.SecurityUtils;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@Validated
@Tag(name = "Greeting API v1", description = "Multilingual greeting service with enterprise-grade features")
public class GreetingController {

    private static final Logger logger = LoggerFactory.getLogger(GreetingController.class);
    private final GreetingService greetingService;
    private final RateLimiter rateLimiter;
    private final GreetingMetrics greetingMetrics;

    @Autowired
    public GreetingController(GreetingService greetingService, RateLimiter rateLimiter, GreetingMetrics greetingMetrics) {
        this.greetingService = greetingService;
        this.rateLimiter = rateLimiter;
        this.greetingMetrics = greetingMetrics;
    }

    @Operation(
        summary = "Get greeting in specified language",
        description = """
            Returns a greeting message in the requested language. 
            Supports 9 languages with automatic fallback to English for unsupported languages.
            Includes rate limiting, input validation, and caching for optimal performance.
            """,
        tags = {"Greeting API"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Greeting retrieved successfully",
            content = @Content(
                mediaType = "text/plain",
                examples = {
                    @ExampleObject(name = "English", value = "Hello World!"),
                    @ExampleObject(name = "Spanish", value = "¡Hola Mundo!"),
                    @ExampleObject(name = "French", value = "Bonjour le Monde!")
                }
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
    @GetMapping("/")
    public ResponseEntity<String> home(
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
            logger.info("Greeting requested for language: {} from IP: {}", sanitizedLanguage, clientIP);
            
            String greeting = greetingService.getGreeting(sanitizedLanguage);
            logger.debug("Returning greeting: {}", greeting);
            
            greetingMetrics.incrementRequests(sanitizedLanguage, "sync");
            return ResponseEntity.ok(greeting);
            
        } finally {
            greetingMetrics.recordTimer(sample, language);
        }
    }
    
    @Operation(
        summary = "Get all available languages",
        description = """
            Returns a map of all supported language codes and their corresponding greeting messages.
            Useful for discovering available languages and their exact greeting format.
            """,
        tags = {"Greeting API"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Languages retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "en": "Hello World!",
                      "es": "¡Hola Mundo!",
                      "fr": "Bonjour le Monde!",
                      "de": "Hallo Welt!",
                      "it": "Ciao Mondo!",
                      "pt": "Olá Mundo!",
                      "ru": "Привет мир!",
                      "ja": "こんにちは世界!",
                      "zh": "你好世界!"
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "429", 
            description = "Rate limit exceeded",
            content = @Content(mediaType = "application/json")
        )
    })
    @GetMapping("/languages")
    public ResponseEntity<Map<String, String>> getAvailableLanguages(HttpServletRequest request) {
        // Rate limiting check
        if (!rateLimiter.tryAcquire()) {
            String clientIP = SecurityUtils.getClientIP(request);
            logger.warn("Rate limit exceeded for IP: {}", clientIP);
            throw new TooManyRequestsException("Rate limit exceeded. Please try again later.");
        }
        
        String clientIP = SecurityUtils.getClientIP(request);
        logger.info("Available languages requested from IP: {}", clientIP);
        
        Map<String, String> languages = greetingService.getAllLanguages();
        return ResponseEntity.ok(languages);
    }

    @Operation(
        summary = "Get greeting asynchronously",
        description = """
            Returns a greeting message asynchronously for improved performance with high-load scenarios.
            Uses dedicated thread pool for non-blocking processing.
            """,
        tags = {"Greeting API"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Async greeting retrieved successfully",
            content = @Content(mediaType = "text/plain")
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
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Async processing error",
            content = @Content(mediaType = "text/plain")
        )
    })
    @GetMapping("/async")
    public CompletableFuture<ResponseEntity<String>> getGreetingAsync(
            @RequestParam(value = "lang", defaultValue = "en")
            @Pattern(regexp = "^[a-z]{2}$", message = "Language code must be exactly 2 lowercase letters")
            @Size(min = 2, max = 2, message = "Language code must be exactly 2 characters")
            @NotBlank(message = "Language code cannot be blank")
            String language,
            HttpServletRequest request) {
        
        // Rate limiting check
        if (!rateLimiter.tryAcquire()) {
            String clientIP = SecurityUtils.getClientIP(request);
            logger.warn("Rate limit exceeded for IP: {}", clientIP);
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
        logger.info("Async greeting requested for language: {} from IP: {}", sanitizedLanguage, clientIP);
        
        return greetingService.getGreetingAsync(sanitizedLanguage)
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    logger.error("Async greeting failed for language: {}", sanitizedLanguage, throwable);
                    return ResponseEntity.status(500).body("Error processing async request");
                });
    }
}
