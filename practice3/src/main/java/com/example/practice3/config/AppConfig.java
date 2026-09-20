package com.example.practice3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Conditional;

@Configuration
public class AppConfig {

    @Bean
    @Conditional(DevelopmentCondition.class)
    public String developmentBean() {
        return "Development Bean";
    }
}