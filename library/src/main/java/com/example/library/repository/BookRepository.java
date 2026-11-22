package com.example.library.repository;

import com.example.library.model.Book;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * Temporary in-memory storage for SIS 1.
 * It will be replaced by a Spring Data JPA repository in SIS 2.
 */
@Repository
public class BookRepository {

    private final Map<Long, Book> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public List<Book> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Book::getId))
                .toList();
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<Book> findByIsbn(String isbn) {
        return storage.values().stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst();
    }

    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(sequence.incrementAndGet());
        }
        storage.put(book.getId(), book);
        return book;
    }

    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
}