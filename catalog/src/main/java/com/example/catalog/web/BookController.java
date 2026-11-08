package com.example.catalog.web;

import com.example.catalog.domain.Book;
import com.example.catalog.dto.PageResponse;
import com.example.catalog.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<Book> list(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer size,
                                   @RequestParam(required = false) String sortBy) {
        return PageResponse.from(service.list(page, size, sortBy));
    }

    @GetMapping("/search")
    public PageResponse<Book> search(@RequestParam String q,
                                     @RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer size) {
        return PageResponse.from(service.searchByTitle(q, page, size));
    }

    @GetMapping("/by-author")
    public PageResponse<Book> byAuthor(@RequestParam String author,
                                       @RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer size) {
        return PageResponse.from(service.findByAuthor(author, page, size));
    }

    @GetMapping("/{id}")
    public Book get(@PathVariable Long id) {
        try {
            return service.get(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}