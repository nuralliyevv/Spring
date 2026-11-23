package com.example.library.service;

import com.example.library.client.OpenLibraryClient;
import com.example.library.domain.Book;
import com.example.library.domain.BookDetails;
import com.example.library.domain.ExternalBookInfo;
import org.springframework.stereotype.Service;

@Service
public class BookDetailsService {

    private final BookService bookService;
    private final OpenLibraryClient openLibraryClient;

    public BookDetailsService(BookService bookService, OpenLibraryClient openLibraryClient) {
        this.bookService = bookService;
        this.openLibraryClient = openLibraryClient;
    }

    public BookDetails getDetails(Long id) {
        Book book = bookService.findById(id);
        ExternalBookInfo external = openLibraryClient.findByIsbn(book.isbn()).orElse(null);
        return new BookDetails(book, external);
    }
}