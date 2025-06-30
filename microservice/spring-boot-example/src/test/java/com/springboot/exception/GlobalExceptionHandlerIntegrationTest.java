package com.springboot.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
class GlobalExceptionHandlerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    void testValidationErrorHandling() {
        ResponseEntity<Map> response = restTemplate
            .withBasicAuth("greeting-user", "dev-password-123")
            .getForEntity(createURLWithPort("/?lang=invalid"), Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.get("error")).isEqualTo("Validation Failed");
        assertThat(errorResponse.get("status")).isEqualTo(400);
    }

    @Test
    void testValidationErrorWithNumbersInLanguage() {
        ResponseEntity<Map> response = restTemplate
            .withBasicAuth("greeting-user", "dev-password-123")
            .getForEntity(createURLWithPort("/?lang=e1"), Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.get("error")).isEqualTo("Validation Failed");
    }

    @Test
    void testValidationErrorWithLongLanguageCode() {
        ResponseEntity<Map> response = restTemplate
            .withBasicAuth("greeting-user", "dev-password-123")
            .getForEntity(createURLWithPort("/?lang=eng"), Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.get("error")).isEqualTo("Validation Failed");
    }
}