package com.example.catalog.dto;

import java.util.List;

public record BookDetailsResponse(
        Long id,
        String title,
        String author,
        String isbn,
        Integer publishedYear,
        Integer pages,
        List<String> publishers,
        String source) {
}