package com.example.library.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.domain.Book;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.BookMapperImpl;
import com.example.library.service.BookService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
@Import(BookMapperImpl.class)
class BookControllerTest {

    private static final String VALID_BODY = """
            {
              "title": "Clean Code",
              "author": "Robert Martin",
              "isbn": "9780132350884",
              "publishedYear": 2008
            }
            """;

    private static final String INVALID_BODY = """
            {
              "title": "",
              "author": "Someone",
              "isbn": "123",
              "publishedYear": 1200
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService service;

    private static Book book(long id) {
        return new Book(id, "Clean Code", "Robert Martin", "9780132350884", 2008);
    }

    @Test
    void create_validRequest_returns201WithLocation() throws Exception {
        when(service.create(any(Book.class))).thenReturn(book(1L));

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/books/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.isbn").value("9780132350884"));
    }

    @Test
    void create_invalidRequest_returns400WithViolations() throws Exception {
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.violations", hasSize(3)));

        verifyNoInteractions(service);
    }

    @Test
    void create_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Broken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void create_duplicateIsbn_returns409() throws Exception {
        when(service.create(any(Book.class)))
                .thenThrow(new DuplicateResourceException("Book with ISBN 9780132350884 already exists"));

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Book with ISBN 9780132350884 already exists"));
    }

    @Test
    void findAll_returnsList() throws Exception {
        when(service.findAll(null)).thenReturn(List.of(book(1L), book(2L)));

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void findAll_withAuthorParam_passesAuthorToService() throws Exception {
        when(service.findAll("Robert Martin")).thenReturn(List.of(book(1L)));

        mockMvc.perform(get("/api/v1/books").param("author", "Robert Martin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findById_existing_returns200() throws Exception {
        when(service.findById(1L)).thenReturn(book(1L));

        mockMvc.perform(get("/api/v1/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Robert Martin"));
    }

    @Test
    void findById_missing_returns404InUnifiedFormat() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Book with id 99 not found"));

        mockMvc.perform(get("/api/v1/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Book with id 99 not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/books/99"));
    }

    @Test
    void findById_invalidIdType_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/books/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_validRequest_returns200() throws Exception {
        when(service.update(any(Long.class), any(Book.class))).thenReturn(book(1L));

        mockMvc.perform(put("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void delete_existing_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/books/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void delete_missing_returns404() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Book with id 99 not found"))
                .when(service).delete(99L);

        mockMvc.perform(delete("/api/v1/books/99"))
                .andExpect(status().isNotFound());
    }
}