package com.example.hr_managment_system.dto.attendance;

import com.example.hr_managment_system.util.StatusUtil;

import java.time.LocalDateTime;

public record AttendanceRequest(

        String employeeId,
        String departmentId,
        LocalDateTime clockIn,
        LocalDateTime clockOut,
        String qrCode,
        Double latitudeIn,
        Double longitudeIn,
        Double latitudeOut,
        Double longitudeOut,
        StatusUtil status

) {
}

