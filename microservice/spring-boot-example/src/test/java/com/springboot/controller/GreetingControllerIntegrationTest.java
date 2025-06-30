package com.springboot.controller;

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
class GreetingControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    void testHomeEndpointWithDefaultLanguage() {
        ResponseEntity<String> response = restTemplate
            .withBasicAuth("greeting-user", "dev-password-123")
            .getForEntity(createURLWithPort("/"), String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Hello World!");
    }

    @Test
    void testHomeEndpointWithSpanishLanguage() {
        ResponseEntity<String> response = restTemplate
            .withBasicAuth("greeting-user", "dev-password-123")
            .getForEntity(createURLWithPort("/?lang=es"), String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("¡Hola Mundo!");
    }

    @Test
    void testHomeEndpointWithJapaneseLanguage() {
        ResponseEntity<String> response = restTemplate
            .withBasicAuth("greeting-user", "dev-password-123")
            .getForEntity(createURLWithPort("/?lang=ja"), String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("こんにちは世界!");
    }

    @Test
    void testLanguagesEndpoint() {
        ResponseEntity<Map> response = restTemplate
            .withBasicAuth("greeting-user", "dev-password-123")
            .getForEntity(createURLWithPort("/languages"), Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, String> languages = response.getBody();
        assertThat(languages).isNotNull();
        assertThat(languages).containsEntry("en", "Hello World!");
        assertThat(languages).containsEntry("es", "¡Hola Mundo!");
        assertThat(languages).hasSize(9);
    }

    @Test
    void testAsyncEndpoint() {
        ResponseEntity<String> response = restTemplate
            .withBasicAuth("greeting-user", "dev-password-123")
            .getForEntity(createURLWithPort("/async?lang=fr"), String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Bonjour le Monde!");
    }
}