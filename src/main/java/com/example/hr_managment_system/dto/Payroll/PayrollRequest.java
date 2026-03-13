package com.example.hr_managment_system.dto.Payroll;

public record PayrollRequest(
        String employeeId,
        Integer month,
        Integer year,
        Double deductions
) {
}
