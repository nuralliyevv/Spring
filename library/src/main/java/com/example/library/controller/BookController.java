package com.example.library.controller;

import com.example.library.domain.Book;
import com.example.library.dto.BookDetailsResponse;
import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.dto.ErrorResponse;
import com.example.library.mapper.BookMapper;
import com.example.library.service.BookDetailsService;
import com.example.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/books")
@Tag(name = "Books", description = "Manage the library catalogue")
public class BookController {

    private final BookService service;
    private final BookDetailsService detailsService;
    private final BookMapper mapper;

    public BookController(BookService service, BookDetailsService detailsService, BookMapper mapper) {
        this.service = service;
        this.detailsService = detailsService;
        this.mapper = mapper;
    }

    @Operation(summary = "Create a book",
            description = "Creates a book and publishes a BookCreatedEvent to Kafka.")
    @ApiResponse(responseCode = "201", description = "Book created; Location header points to it")
    @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "A book with this ISBN already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        Book created = service.create(mapper.toDomain(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(created));
    }

    @Operation(summary = "List books", description = "Returns all books, optionally filtered by author (case-insensitive).")
    @ApiResponse(responseCode = "200", description = "List of books (may be empty)")
    @GetMapping
    public List<BookResponse> findAll(
            @Parameter(description = "Only return books by this author", example = "Robert Martin")
            @RequestParam(required = false) String author) {
        return service.findAll(author).stream().map(mapper::toResponse).toList();
    }

    @Operation(summary = "Get a book by id", description = "The result is cached until the book is updated or deleted.")
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(responseCode = "400", description = "Invalid id",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Book not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public BookResponse findById(@Parameter(description = "Book id", example = "1") @PathVariable Long id) {
        return mapper.toResponse(service.findById(id));
    }

    @Operation(summary = "Get a book with data from Open Library",
            description = "Adds page count, publisher and cover from the external Open Library API. "
                    + "The external call has timeouts, retry and a circuit breaker; if it fails, "
                    + "the stored book is still returned with externalInfoAvailable=false.")
    @ApiResponse(responseCode = "200", description = "Book details (external part may be missing)")
    @ApiResponse(responseCode = "404", description = "Book not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}/details")
    public BookDetailsResponse details(@Parameter(description = "Book id", example = "1") @PathVariable Long id) {
        return BookDetailsResponse.from(detailsService.getDetails(id));
    }

    @Operation(summary = "Update a book", description = "Replaces all fields of an existing book.")
    @ApiResponse(responseCode = "200", description = "Book updated")
    @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Book not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Another book already has this ISBN",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public BookResponse update(@Parameter(description = "Book id", example = "1") @PathVariable Long id,
                               @Valid @RequestBody BookRequest request) {
        return mapper.toResponse(service.update(id, mapper.toDomain(request)));
    }

    @Operation(summary = "Delete a book")
    @ApiResponse(responseCode = "204", description = "Book deleted")
    @ApiResponse(responseCode = "404", description = "Book not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "Book id", example = "1") @PathVariable Long id) {
        service.delete(id);
    }
}