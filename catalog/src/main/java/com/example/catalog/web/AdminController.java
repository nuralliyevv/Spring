package com.example.catalog.web;

import com.example.catalog.dto.UserSummary;
import com.example.catalog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Endpoints for the ADMIN role only")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "List all users (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Users returned")
    @ApiResponse(responseCode = "401", description = "No or invalid token")
    @ApiResponse(responseCode = "403", description = "Logged in, but not an ADMIN")
    @GetMapping("/users")
    public List<UserSummary> users() {
        return userService.listUsers();
    }
}