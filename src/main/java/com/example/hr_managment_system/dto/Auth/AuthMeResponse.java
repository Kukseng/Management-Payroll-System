package com.example.hr_managment_system.dto.Auth;

public record AuthMeResponse(
        String employeeId,
        String username,
        String email,
        String role,
        String departmentId
) {
}
