package com.example.catalog.service;

import com.example.catalog.config.CatalogProperties;
import com.example.catalog.domain.Book;
import com.example.catalog.repository.BookRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class BookService {

    private static final Set<String> SORTABLE_FIELDS =
            Set.of("id", "title", "author", "publishedYear");

    private final BookRepository repository;
    private final CatalogProperties props;

    public BookService(BookRepository repository, CatalogProperties props) {
        this.repository = repository;
        this.props = props;
    }

    public Page<Book> list(Integer page, Integer size, String sortBy) {
        return repository.findAll(buildPageable(page, size, sortBy));
    }

    public Page<Book> searchByTitle(String q, Integer page, Integer size) {
        return repository.searchByTitle(q, buildPageable(page, size, "title"));
    }

    public Page<Book> findByAuthor(String author, Integer page, Integer size) {
        return repository.findByAuthorContainingIgnoreCase(author, buildPageable(page, size, "title"));
    }

    // Cached: the result is stored in the "books" cache under the key = id.
    // The next call with the same id skips the method body and the database.
    @Cacheable(cacheNames = "books", key = "#id")
    public Book get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book " + id + " not found"));
    }

    private Pageable buildPageable(Integer page, Integer size, String sortBy) {
        int pageNumber = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size < 1)
                ? props.defaultPageSize()
                : Math.min(size, props.maxPageSize());
        String sortField = (sortBy != null && SORTABLE_FIELDS.contains(sortBy)) ? sortBy : "id";
        return PageRequest.of(pageNumber, pageSize, Sort.by(sortField));
    }
}