package com.example.hr_managment_system.dto.Leave;

import com.example.hr_managment_system.util.StatusProgressUtil;
import com.example.hr_managment_system.util.TypeUtil;

import java.time.LocalDate;

public record LeaveResponse(
        String leaveRequestId,
        String employeeId,
        String employeeName,
        LocalDate startDate,
        LocalDate endDate,
        TypeUtil type,
        StatusProgressUtil status,
        String approvedByEmployeeId,
        String reason
) {
}
