package com.example.library.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BookRequest(

        @NotBlank(message = "title must not be blank")
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        @NotBlank(message = "author must not be blank")
        @Size(max = 100, message = "author must be at most 100 characters")
        String author,

        @NotBlank(message = "isbn must not be blank")
        @Pattern(regexp = "^\\d{13}$", message = "isbn must consist of exactly 13 digits")
        String isbn,

        @NotNull(message = "publishedYear is required")
        @Min(value = 1450, message = "publishedYear must be at least 1450")
        @Max(value = 2100, message = "publishedYear must be at most 2100")
        Integer publishedYear
) {
}