package com.springboot.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties("app.greeting")
@Validated
@Component
public class GreetingConfigProperties {

    @NotBlank(message = "Default language cannot be blank")
    @Pattern(regexp = "[a-z]{2}", message = "Default language must be a 2-letter lowercase code")
    private String defaultLanguage = "en";

    @NotEmpty(message = "Supported languages list cannot be empty")
    @Size(min = 1, max = 20, message = "Must support between 1 and 20 languages")
    private List<@Pattern(regexp = "[a-z]{2}", message = "Each language must be a 2-letter code") String> supportedLanguages;

    @Valid
    private Security security = new Security();

    // Getters and Setters
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    public void setSupportedLanguages(List<String> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    @Validated
    public static class Security {
        
        @Valid
        private RateLimit rateLimit = new RateLimit();
        
        @Valid
        private Cors cors = new Cors();

        public RateLimit getRateLimit() {
            return rateLimit;
        }

        public void setRateLimit(RateLimit rateLimit) {
            this.rateLimit = rateLimit;
        }

        public Cors getCors() {
            return cors;
        }

        public void setCors(Cors cors) {
            this.cors = cors;
        }

        @Validated
        public static class RateLimit {
            
            @Min(value = 1, message = "Rate limit must be at least 1 request per second")
            @Max(value = 10000, message = "Rate limit cannot exceed 10000 requests per second")
            private int requestsPerSecond = 100;

            public int getRequestsPerSecond() {
                return requestsPerSecond;
            }

            public void setRequestsPerSecond(int requestsPerSecond) {
                this.requestsPerSecond = requestsPerSecond;
            }
        }

        @Validated
        public static class Cors {
            
            @NotEmpty(message = "CORS allowed origins cannot be empty")
            private List<@NotBlank String> allowedOrigins = List.of("http://localhost:*");
            
            @NotEmpty(message = "CORS allowed methods cannot be empty")
            private List<@NotBlank String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
            
            @Min(value = 0, message = "CORS max age cannot be negative")
            @Max(value = 86400, message = "CORS max age cannot exceed 24 hours (86400 seconds)")
            private long maxAge = 3600;

            public List<String> getAllowedOrigins() {
                return allowedOrigins;
            }

            public void setAllowedOrigins(List<String> allowedOrigins) {
                this.allowedOrigins = allowedOrigins;
            }

            public List<String> getAllowedMethods() {
                return allowedMethods;
            }

            public void setAllowedMethods(List<String> allowedMethods) {
                this.allowedMethods = allowedMethods;
            }

            public long getMaxAge() {
                return maxAge;
            }

            public void setMaxAge(long maxAge) {
                this.maxAge = maxAge;
            }
        }
    }
}