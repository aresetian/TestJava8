package com.test.jdk;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Java 17 Features Demo Application
 * 
 * Demonstrates modern Java features including:
 * - Text blocks (Java 15+)
 * - Records (Java 14+)
 * - Pattern matching (Java 17+)
 * - Stream API enhancements
 */
public class App {
    
    /**
     * Record to demonstrate Java 14+ Records feature
     */
    public record Person(String name, int age, String city) {
        public boolean isAdult() {
            return age >= 18;
        }
    }
    
    public static void main(String[] args) {
        // Basic Hello World
        System.out.println("Hello World!");
        
        // Demonstrate Java 17 features if no arguments
        if (args == null || args.length == 0) {
            demonstrateJava17Features();
        }
    }
    
    private static void demonstrateJava17Features() {
        // Text blocks (Java 15+)
        var jsonExample = """
                {
                    "application": "Java 17 Demo",
                    "version": "1.0",
                    "features": ["Records", "Text Blocks", "Pattern Matching"]
                }
                """;
        
        System.out.println("=== Java 17 Features Demo ===");
        System.out.println("JSON Example using Text Blocks:");
        System.out.println(jsonExample);
        
        // Records demonstration
        var people = List.of(
            new Person("Alice", 25, "Madrid"),
            new Person("Bob", 17, "Barcelona"),
            new Person("Charlie", 30, "Valencia")
        );
        
        System.out.println("=== Records and Stream API ===");
        var adults = people.stream()
                .filter(Person::isAdult)
                .map(person -> person.name() + " (" + person.age() + ") from " + person.city())
                .collect(Collectors.toList());
        
        System.out.println("Adults: " + adults);
        
        // Pattern matching for instanceof (Java 17+)
        Object obj = "Java 17 is awesome!";
        demonstratePatternMatching(obj);
        
        obj = 42;
        demonstratePatternMatching(obj);
    }
    
    private static void demonstratePatternMatching(Object obj) {
        if (obj instanceof String s) {
            System.out.println("String with length: " + s.length() + " -> " + s);
        } else if (obj instanceof Integer i) {
            System.out.println("Integer value: " + i + " (squared: " + (i * i) + ")");
        } else {
            System.out.println("Unknown type: " + obj.getClass().getSimpleName());
        }
    }
}
