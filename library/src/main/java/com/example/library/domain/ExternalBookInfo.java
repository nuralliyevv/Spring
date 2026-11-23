package com.example.library.domain;

public record ExternalBookInfo(
        Integer numberOfPages,
        String publisher,
        String coverUrl
) {
}