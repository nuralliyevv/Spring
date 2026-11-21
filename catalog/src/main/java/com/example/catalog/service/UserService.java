package com.example.catalog.service;

import com.example.catalog.dto.UserSummary;
import com.example.catalog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<UserSummary> listUsers() {
        return repository.findAll().stream()
                .map(user -> new UserSummary(user.getUsername(), user.getRole()))
                .toList();
    }
}