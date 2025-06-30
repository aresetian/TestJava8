package com.springboot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@CacheConfig(cacheNames = "greetings")
public class GreetingService {

    private static final Logger logger = LoggerFactory.getLogger(GreetingService.class);

    private static final Map<String, String> GREETINGS = Map.of(
        "es", "¡Hola Mundo!",
        "en", "Hello World!",
        "fr", "Bonjour le Monde!",
        "de", "Hallo Welt!",
        "it", "Ciao Mondo!",
        "pt", "Olá Mundo!",
        "ru", "Привет мир!",
        "ja", "こんにちは世界!",
        "zh", "你好世界!"
    );

    @Value("${app.greeting.default-language:en}")
    private String defaultLanguage;

    @Cacheable(key = "#language")
    public String getGreeting(String language) {
        logger.debug("Processing greeting request for language: {} (cache miss)", language);
        
        // Simulate expensive operation
        try {
            Thread.sleep(100); // Simulate database or external API call
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String normalizedLanguage = language.toLowerCase();
        String greeting = GREETINGS.getOrDefault(normalizedLanguage, GREETINGS.get(defaultLanguage));
        
        if (!GREETINGS.containsKey(normalizedLanguage)) {
            logger.info("Language '{}' not supported, using default: {}", language, defaultLanguage);
        }
        
        return greeting;
    }

    @Cacheable(cacheNames = "languages", key = "'all'")
    public Map<String, String> getAllLanguages() {
        logger.debug("Returning all available languages, count: {} (cache miss)", GREETINGS.size());
        
        // Simulate expensive operation
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return GREETINGS;
    }

    @Async("greetingTaskExecutor")
    public CompletableFuture<String> getGreetingAsync(String language) {
        logger.debug("Processing async greeting request for language: {} on thread: {}", 
                    language, Thread.currentThread().getName());
        
        String greeting = getGreeting(language);
        return CompletableFuture.completedFuture(greeting);
    }

    @CacheEvict(allEntries = true)
    public void clearCache() {
        logger.info("Greeting cache cleared");
    }

    @CacheEvict(cacheNames = "greetings", key = "#language")
    public void evictGreeting(String language) {
        logger.info("Evicted greeting cache for language: {}", language);
    }

    public boolean isLanguageSupported(String language) {
        return GREETINGS.containsKey(language.toLowerCase());
    }
}