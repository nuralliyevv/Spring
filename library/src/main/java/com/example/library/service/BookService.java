package com.example.library.service;

import com.example.library.domain.Book;
import com.example.library.entity.BookEntity;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.BookRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository repository;
    private final BookMapper mapper;

    public BookService(BookRepository repository, BookMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public Book create(Book book) {
        if (repository.existsByIsbn(book.isbn())) {
            throw new DuplicateResourceException("Book with ISBN " + book.isbn() + " already exists");
        }
        BookEntity saved = repository.save(mapper.toEntity(book));
        return mapper.toDomain(saved);
    }

    public List<Book> findAll(String author) {
        List<BookEntity> entities = (author == null)
                ? repository.findAll(Sort.by("id"))
                : repository.findByAuthorIgnoreCaseOrderById(author);
        return entities.stream().map(mapper::toDomain).toList();
    }

    public Book findById(Long id) {
        return mapper.toDomain(getOrThrow(id));
    }

    @Transactional
    public Book update(Long id, Book book) {
        BookEntity entity = getOrThrow(id);
        repository.findByIsbn(book.isbn())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("Book with ISBN " + book.isbn() + " already exists");
                });
        mapper.updateEntity(book, entity);
        return mapper.toDomain(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Book with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    private BookEntity getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " not found"));
    }
}