package com.example.library.service;

import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public BookResponse create(BookRequest request) {
        repository.findByIsbn(request.isbn()).ifPresent(existing -> {
            throw new DuplicateResourceException("Book with ISBN " + request.isbn() + " already exists");
        });
        Book book = new Book(null, request.title(), request.author(), request.isbn(), request.publishedYear());
        return toResponse(repository.save(book));
    }

    public List<BookResponse> findAll(String author) {
        return repository.findAll().stream()
                .filter(book -> author == null || book.getAuthor().equalsIgnoreCase(author))
                .map(this::toResponse)
                .toList();
    }

    public BookResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public BookResponse update(Long id, BookRequest request) {
        Book book = getOrThrow(id);
        repository.findByIsbn(request.isbn())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("Book with ISBN " + request.isbn() + " already exists");
                });
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setIsbn(request.isbn());
        book.setPublishedYear(request.publishedYear());
        return toResponse(repository.save(book));
    }

    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new ResourceNotFoundException("Book with id " + id + " not found");
        }
    }

    private Book getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " not found"));
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPublishedYear());
    }
}