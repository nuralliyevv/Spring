package com.example.library.domain;

/**
 * A stored book plus optional data from an external service.
 * externalInfo is null when the external service had nothing or was unavailable.
 */
public record BookDetails(
        Book book,
        ExternalBookInfo externalInfo
) {
}