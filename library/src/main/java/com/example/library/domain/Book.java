package com.example.library.domain;

public record Book(
        Long id,
        String title,
        String author,
        String isbn,
        Integer publishedYear
) {
}