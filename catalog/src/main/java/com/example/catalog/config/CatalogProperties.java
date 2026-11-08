package com.example.catalog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "catalog")
public record CatalogProperties(
        String name,
        String welcomeMessage,
        int defaultPageSize,
        int maxPageSize) {
}