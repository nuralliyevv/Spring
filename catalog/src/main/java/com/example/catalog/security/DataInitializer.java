package com.example.catalog.security;

import com.example.catalog.domain.AppUser;
import com.example.catalog.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createIfMissing("admin", "admin123", "ADMIN");
        createIfMissing("user", "user123", "USER");
    }

    // Demo passwords are for the course only; real systems never hardcode them
    private void createIfMissing(String username, String rawPassword, String role) {
        if (!repository.existsByUsername(username)) {
            repository.save(new AppUser(username, passwordEncoder.encode(rawPassword), role));
        }
    }
}