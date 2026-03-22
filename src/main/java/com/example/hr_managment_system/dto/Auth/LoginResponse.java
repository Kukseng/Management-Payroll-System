package com.example.hr_managment_system.dto.Auth;

public record LoginResponse(
        String accessToken,
        String tokenType
) {
}
