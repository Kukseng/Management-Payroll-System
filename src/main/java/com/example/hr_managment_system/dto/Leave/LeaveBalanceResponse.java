package com.example.hr_managment_system.dto.Leave;

public record LeaveBalanceResponse(
        String employeeId,
        int annualAllowanceDays,
        int approvedDays,
        int remainingDays
) {
}
