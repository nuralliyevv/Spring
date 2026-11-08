package com.example.catalog.service;

import com.example.catalog.config.CatalogProperties;
import org.springframework.stereotype.Service;

@Service
public class DefaultGreetingService implements GreetingService {

    private final CatalogProperties props;

    // Constructor injection: Spring passes CatalogProperties in automatically.
    // There is no @Autowired, because with a single constructor it is not needed.
    public DefaultGreetingService(CatalogProperties props) {
        this.props = props;
    }

    @Override
    public String greet() {
        return props.name() + ": " + props.welcomeMessage();
    }
}