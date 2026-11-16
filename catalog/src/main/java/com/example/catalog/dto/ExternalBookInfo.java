package com.example.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalBookInfo(
        String title,
        @JsonProperty("number_of_pages") Integer numberOfPages,
        List<Publisher> publishers) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Publisher(String name) {
    }
}