package com.example.catalog.web;

import com.example.catalog.domain.Book;
import com.example.catalog.dto.BookDetailsResponse;
import com.example.catalog.dto.PageResponse;
import com.example.catalog.service.BookDetailsService;
import com.example.catalog.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Books", description = "Browse and search the book catalog")
public class BookController {

    private final BookService service;
    private final BookDetailsService detailsService;

    public BookController(BookService service, BookDetailsService detailsService) {
        this.service = service;
        this.detailsService = detailsService;
    }

    @Operation(summary = "List books (paged)")
    @GetMapping
    public PageResponse<Book> list(
            @Parameter(description = "Page number, starting from 0") @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size (max 50)") @RequestParam(required = false) Integer size,
            @Parameter(description = "Sort field: id, title, author, publishedYear") @RequestParam(required = false) String sortBy) {
        return PageResponse.from(service.list(page, size, sortBy));
    }

    @Operation(summary = "Search books by title")
    @GetMapping("/search")
    public PageResponse<Book> search(@RequestParam String q,
                                     @RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer size) {
        return PageResponse.from(service.searchByTitle(q, page, size));
    }

    @Operation(summary = "Find books by author")
    @GetMapping("/by-author")
    public PageResponse<Book> byAuthor(@RequestParam String author,
                                       @RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer size) {
        return PageResponse.from(service.findByAuthor(author, page, size));
    }

    @Operation(summary = "Get one book by id")
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/{id}")
    public Book get(@PathVariable Long id) {
        try {
            return service.get(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Get a book with details from Open Library",
            description = "Calls the external Open Library API (2 s connect / 3 s read timeout). "
                    + "If the call fails or times out, returns local data with source=FALLBACK.")
    @ApiResponse(responseCode = "200", description = "Details returned (from Open Library or fallback)")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/{id}/details")
    public BookDetailsResponse details(@PathVariable Long id) {
        try {
            return detailsService.getDetails(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}