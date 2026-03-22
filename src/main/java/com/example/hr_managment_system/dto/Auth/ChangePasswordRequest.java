package com.example.hr_managment_system.dto.Auth;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
