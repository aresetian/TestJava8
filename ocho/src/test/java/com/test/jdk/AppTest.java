package com.test.jdk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Unit tests for App using JUnit 5.
 * Tests Java 17 features and basic functionality.
 */
class AppTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    @DisplayName("Should print 'Hello World!' and Java 17 demo to console")
    void testMainMethodOutput() {
        // When
        App.main(new String[]{});
        
        // Then
        String output = outContent.toString();
        assertTrue(output.startsWith("Hello World!" + System.lineSeparator()));
        assertTrue(output.contains("=== Java 17 Features Demo ==="));
        assertTrue(output.contains("JSON Example using Text Blocks:"));
        assertTrue(output.contains("Adults: [Alice (25) from Madrid, Charlie (30) from Valencia]"));
    }

    @Test
    @DisplayName("Should handle empty arguments array")
    void testMainWithEmptyArgs() {
        // When & Then
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }

    @Test
    @DisplayName("Should handle null arguments")
    void testMainWithNullArgs() {
        // When & Then
        assertDoesNotThrow(() -> App.main(null));
    }

    @Test
    @DisplayName("App class should be instantiable")
    void testAppInstantiation() {
        // When & Then
        assertDoesNotThrow(() -> new App());
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }
}
