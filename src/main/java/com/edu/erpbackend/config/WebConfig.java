package com.edu.erpbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply to all endpoints
                .allowedOriginPatterns("*") // ✅ USE THIS instead of allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH") // ✅ Added PATCH and HEAD
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // ✅ Critical for React/Axios frontend communication
    }
}