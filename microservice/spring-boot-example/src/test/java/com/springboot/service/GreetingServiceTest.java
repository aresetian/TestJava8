package com.springboot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "app.greeting.default-language=en",
    "logging.level.com.springboot=OFF"
})
class GreetingServiceTest {

    @Autowired
    private GreetingService greetingService;

    @Test
    void testGetGreetingInEnglish() {
        String result = greetingService.getGreeting("en");
        assertEquals("Hello World!", result);
    }

    @Test
    void testGetGreetingInSpanish() {
        String result = greetingService.getGreeting("es");
        assertEquals("¡Hola Mundo!", result);
    }

    @Test
    void testGetGreetingInFrench() {
        String result = greetingService.getGreeting("fr");
        assertEquals("Bonjour le Monde!", result);
    }

    @Test
    void testGetGreetingWithUppercaseLanguage() {
        String result = greetingService.getGreeting("ES");
        assertEquals("¡Hola Mundo!", result);
    }

    @Test
    void testGetGreetingWithInvalidLanguage() {
        String result = greetingService.getGreeting("invalid");
        assertEquals("Hello World!", result); // Should default to English
    }

    @Test
    void testGetAllLanguages() {
        Map<String, String> languages = greetingService.getAllLanguages();
        
        assertNotNull(languages);
        assertFalse(languages.isEmpty());
        assertTrue(languages.containsKey("en"));
        assertTrue(languages.containsKey("es"));
        assertTrue(languages.containsKey("fr"));
        assertEquals("Hello World!", languages.get("en"));
        assertEquals("¡Hola Mundo!", languages.get("es"));
    }

    @Test
    void testLanguageMapIsImmutable() {
        Map<String, String> languages = greetingService.getAllLanguages();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            languages.put("test", "test");
        });
    }
}