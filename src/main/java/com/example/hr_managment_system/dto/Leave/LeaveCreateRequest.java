package com.example.hr_managment_system.dto.Leave;

import com.example.hr_managment_system.util.TypeUtil;

import java.time.LocalDate;

public record LeaveCreateRequest(
        String employeeId,
        LocalDate startDate,
        LocalDate endDate,
        TypeUtil type
) {
}
