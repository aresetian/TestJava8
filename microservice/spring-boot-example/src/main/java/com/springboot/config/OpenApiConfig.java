package com.springboot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Greeting Microservice API")
                .version("1.0.0")
                .description("""
                    Enterprise-grade multilingual greeting microservice.
                    
                    Features:
                    - Support for 9 languages (ES, EN, FR, DE, IT, PT, RU, JA, ZH)
                    - Rate limiting and security headers
                    - Input validation and sanitization
                    - Caching for improved performance
                    - Async processing capabilities
                    - Comprehensive monitoring and health checks
                    """)
                .contact(new Contact()
                    .name("Development Team")
                    .email("dev@company.com")
                    .url("https://company.com/team")
                )
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")
                )
            )
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local development server"),
                new Server()
                    .url("https://api.company.com")
                    .description("Production server")
            ));
    }
}