package com.example.hr_managment_system.dto.Payroll;

public record PayrollBatchRequest(
        Integer month,
        Integer year,
        Double defaultDeductions
) {
}
