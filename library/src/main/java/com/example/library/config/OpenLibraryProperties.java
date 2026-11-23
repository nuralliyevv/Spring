package com.example.library.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "library.open-library")
public record OpenLibraryProperties(
        String baseUrl,
        Duration connectTimeout,
        Duration readTimeout
) {
}