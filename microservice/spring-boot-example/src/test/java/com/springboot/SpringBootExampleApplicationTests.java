package com.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "server.port=0",
    "logging.level.com.springboot=OFF",
    "logging.level.org.springframework=WARN",
    "logging.level.org.hibernate=WARN",
    "logging.level.net.bytebuddy=OFF"
})
class SpringBootExampleApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // If this test passes, it means all beans are properly configured and wired
    }
}