package com.fatec.estudamaisbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS configuration moved to SecurityConfig.java
    // to avoid conflicts with Spring Security CORS handling
}
