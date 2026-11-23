package com.example.library.dto;

import com.example.library.domain.Book;
import com.example.library.domain.BookDetails;
import com.example.library.domain.ExternalBookInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A stored book enriched with data from Open Library")
public record BookDetailsResponse(
        Long id,
        String title,
        String author,
        String isbn,
        Integer publishedYear,
        @Schema(description = "Number of pages from Open Library, null if unavailable")
        Integer numberOfPages,
        @Schema(description = "Publisher from Open Library, null if unavailable")
        String publisher,
        @Schema(description = "Cover image URL from Open Library, null if unavailable")
        String coverUrl,
        @Schema(description = "False when Open Library had no data or could not be reached")
        boolean externalInfoAvailable
) {

    public static BookDetailsResponse from(BookDetails details) {
        Book book = details.book();
        ExternalBookInfo external = details.externalInfo();
        return new BookDetailsResponse(
                book.id(),
                book.title(),
                book.author(),
                book.isbn(),
                book.publishedYear(),
                external == null ? null : external.numberOfPages(),
                external == null ? null : external.publisher(),
                external == null ? null : external.coverUrl(),
                external != null
        );
    }
}