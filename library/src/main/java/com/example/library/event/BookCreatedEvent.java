package com.example.library.event;

import java.time.Instant;

public record BookCreatedEvent(
        Long bookId,
        String title,
        String isbn,
        Instant occurredAt
) {
}