package com.example.practice2.controller;

import com.example.practice2.config.AppProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    private final AppProperties appProperties;

    public ConfigController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping("/config")
    public String config() {
        return appProperties.getEnvironment()
                + " - "
                + appProperties.getMessage();
    }
}