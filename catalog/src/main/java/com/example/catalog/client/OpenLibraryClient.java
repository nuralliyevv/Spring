package com.example.catalog.client;

import com.example.catalog.dto.ExternalBookInfo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

@Component
public class OpenLibraryClient {

    private final RestClient restClient;

    public OpenLibraryClient(RestClient openLibraryRestClient) {
        this.restClient = openLibraryRestClient;
    }

    /**
     * Calls Open Library. Throws RestClientException on timeout, network
     * problems or HTTP errors; the caller decides what to do (fallback).
     */
    public Optional<ExternalBookInfo> findByIsbn(String isbn) {
        String key = "ISBN:" + isbn;

        Map<String, ExternalBookInfo> body = restClient.get()
                .uri(uri -> uri.path("/api/books")
                        .queryParam("bibkeys", key)
                        .queryParam("format", "json")
                        .queryParam("jscmd", "data")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, ExternalBookInfo>>() {
                });

        return Optional.ofNullable(body).map(map -> map.get(key));
    }
}