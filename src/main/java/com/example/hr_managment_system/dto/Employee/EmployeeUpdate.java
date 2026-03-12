package com.example.hr_managment_system.dto.Employee;

import java.time.LocalDate;

public record EmployeeUpdate(
        String firstName,
        String lastName,
        String email,
        LocalDate dateOfBirth
) {
}
