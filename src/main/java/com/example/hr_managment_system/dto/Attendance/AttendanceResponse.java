package com.example.hr_managment_system.dto.Attendance;

import com.example.hr_managment_system.util.StatusUtil;

import java.time.LocalDateTime;

public record AttendanceResponse(


        String attendanceId,
        String departmentId,
        String departmentName,
        String employeeId,
        LocalDateTime clockIn,
        LocalDateTime clockOut,
        Double latitudeIn,
        Double latitudeOut,
        StatusUtil status,
        String statusName

) {
}
