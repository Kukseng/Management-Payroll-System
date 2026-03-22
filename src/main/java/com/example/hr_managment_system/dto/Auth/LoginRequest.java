package com.example.hr_managment_system.dto.Auth;

public record LoginRequest(
        String username,
        String password
) {
}
