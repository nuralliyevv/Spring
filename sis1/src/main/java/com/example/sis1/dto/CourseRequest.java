package com.example.sis1.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CourseRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    private String description;

    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits must be at most 10")
    private int credits;

    public CourseRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}