package com.example.practice3.controller;

import com.example.practice3.service.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private final GreetingService greetingService;
    private final String developmentBean;

    public GreetingController(
            GreetingService greetingService,
            String developmentBean) {
        this.greetingService = greetingService;
        this.developmentBean = developmentBean;
    }

    @GetMapping("/greet")
    public String greet() {
        return greetingService.greet();
    }

    @GetMapping("/bean")
    public String bean() {
        return developmentBean;
    }
}