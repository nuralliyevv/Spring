package com.example.catalog.service;

import com.example.catalog.client.OpenLibraryClient;
import com.example.catalog.domain.Book;
import com.example.catalog.dto.BookDetailsResponse;
import com.example.catalog.dto.ExternalBookInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

@Service
public class BookDetailsService {

    private static final Logger log = LoggerFactory.getLogger(BookDetailsService.class);

    private final BookService bookService;
    private final OpenLibraryClient openLibraryClient;

    public BookDetailsService(BookService bookService, OpenLibraryClient openLibraryClient) {
        this.bookService = bookService;
        this.openLibraryClient = openLibraryClient;
    }

    public BookDetailsResponse getDetails(Long id) {
        // our own data (cached by Practice 5); throws NoSuchElementException if missing
        Book book = bookService.get(id);

        try {
            Optional<ExternalBookInfo> info = openLibraryClient.findByIsbn(book.getIsbn());
            if (info.isPresent()) {
                return fromExternal(book, info.get());
            }
            log.info("Open Library has no data for ISBN {}, using fallback", book.getIsbn());
        } catch (RestClientException ex) {
            // timeout, connection error, 4xx/5xx: the endpoint must still work
            log.warn("Open Library call failed ({}: {}), using fallback",
                    ex.getClass().getSimpleName(), ex.getMessage());
        }
        return fallback(book);
    }

    private BookDetailsResponse fromExternal(Book book, ExternalBookInfo info) {
        List<String> publishers = info.publishers() == null
                ? List.of()
                : info.publishers().stream().map(ExternalBookInfo.Publisher::name).toList();
        return new BookDetailsResponse(book.getId(), book.getTitle(), book.getAuthor(),
                book.getIsbn(), book.getPublishedYear(), info.numberOfPages(), publishers, "OPEN_LIBRARY");
    }

    // Fallback: only our own data, no external details
    private BookDetailsResponse fallback(Book book) {
        return new BookDetailsResponse(book.getId(), book.getTitle(), book.getAuthor(),
                book.getIsbn(), book.getPublishedYear(), null, List.of(), "FALLBACK");
    }
}