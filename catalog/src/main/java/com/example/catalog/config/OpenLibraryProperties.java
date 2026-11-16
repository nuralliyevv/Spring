package com.example.catalog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "external.open-library")
public record OpenLibraryProperties(
        String baseUrl,
        Duration connectTimeout,
        Duration readTimeout) {
}