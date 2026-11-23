package com.example.library.client;

import com.example.library.domain.ExternalBookInfo;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenLibraryClient {

    private static final Logger log = LoggerFactory.getLogger(OpenLibraryClient.class);

    private final RestClient restClient;

    public OpenLibraryClient(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Timeouts: set on the RestClient (see OpenLibraryClientConfig).
     * Retry: up to 3 attempts for I/O errors and 5xx responses.
     * Circuit breaker: stops calling Open Library after repeated failures.
     * Fallback: if everything fails, returns an empty result instead of an error.
     * Cache: successful results are cached by ISBN (empty results are not).
     */
    @Cacheable(cacheNames = "externalBookInfo", key = "#isbn", unless = "#result == null")
    @Retry(name = "openLibrary", fallbackMethod = "fallback")
    @CircuitBreaker(name = "openLibrary")
    public Optional<ExternalBookInfo> findByIsbn(String isbn) {
        log.info("Calling Open Library for ISBN {}", isbn);

        JsonNode root = restClient.get()
                .uri("/api/books?bibkeys=ISBN:{isbn}&format=json&jscmd=data", isbn)
                .retrieve()
                .body(JsonNode.class);

        JsonNode data = (root == null) ? null : root.get("ISBN:" + isbn);
        if (data == null || data.isEmpty()) {
            return Optional.empty();
        }

        Integer pages = data.hasNonNull("number_of_pages") ? data.get("number_of_pages").asInt() : null;
        String publisher = data.path("publishers").path(0).path("name").asText(null);
        String cover = data.path("cover").path("medium").asText(null);
        return Optional.of(new ExternalBookInfo(pages, publisher, cover));
    }

    // Must have the same parameters as findByIsbn plus a Throwable.
    public Optional<ExternalBookInfo> fallback(String isbn, Throwable ex) {
        log.warn("Open Library unavailable for ISBN {} ({}: {}), using fallback",
                isbn, ex.getClass().getSimpleName(), ex.getMessage());
        return Optional.empty();
    }
}