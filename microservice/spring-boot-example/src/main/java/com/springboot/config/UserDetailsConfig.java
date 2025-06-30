package com.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserDetailsConfig {

    @Value("${app.security.user.username:greeting-user}")
    private String userUsername;

    @Value("${app.security.user.password:#{T(java.util.UUID).randomUUID().toString()}}")
    private String userPassword;

    @Value("${app.security.admin.username:greeting-admin}")
    private String adminUsername;

    @Value("${app.security.admin.password:#{T(java.util.UUID).randomUUID().toString()}}")
    private String adminPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
            User.builder()
                .username(userUsername)
                .password(passwordEncoder.encode(userPassword))
                .roles("USER")
                .build(),
            User.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("USER", "ADMIN")
                .build()
        );
    }
}