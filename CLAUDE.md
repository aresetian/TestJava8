# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This repository contains two Java projects demonstrating different Java capabilities:

1. **microservice/spring-boot-example** - A Spring Boot 3.2.11 microservice with enterprise-grade features (Java 21)
2. **ocho** - A Java 17 application demonstrating modern Java features (Records, Text Blocks, Pattern Matching)

## Build and Run Commands

### Spring Boot Microservice (microservice/spring-boot-example/)
```bash
# Navigate to the Spring Boot project
cd microservice/spring-boot-example

# Compile and run using Maven exec plugin
mvn exec:java

# Alternative: Run using Spring Boot Maven plugin (recommended)
mvn spring-boot:run

# Run tests
mvn test

# Package the application
mvn package
```

### Java 17 Features Demo Project (ocho/)
```bash
# Navigate to the Java 17 project
cd ocho

# Compile and run (demonstrates modern Java features)
mvn exec:java -Dexec.mainClass="com.test.jdk.App"

# Run tests (JUnit 5)
mvn test

# Package
mvn package
```

## Architecture Notes

### Spring Boot Microservice
- Single controller class (`SampleController.java`) that serves as both the REST controller and main application entry point
- Uses `@RestController` and `@EnableAutoConfiguration` annotations
- Exposes a single endpoint at "/" returning "Hello World!"
- Configured for Java 1.8 compilation target
- Maven exec plugin configured with main class `com.springboot.SampleController`

### Basic Java Project
- Simple "Hello World" application in `App.java`
- Basic JUnit 3.8.1 test setup in `AppTest.java`
- Standard Maven directory structure

## Java Version
Both projects are configured for Java 1.8 (Java 8) as the source and target compilation version.

## Dependencies
- **Spring Boot project**: Spring Boot Starter Web, JUnit (via parent POM)
- **Basic Java project**: JUnit 3.8.1 for testing

## Project Structure
The repository uses a multi-module structure with two independent Maven projects, each with their own `pom.xml` and standard Maven directory layout.