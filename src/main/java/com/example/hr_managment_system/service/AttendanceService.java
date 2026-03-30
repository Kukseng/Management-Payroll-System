package com.example.hr_managment_system.service;

import com.example.hr_managment_system.dto.Attendance.AttendanceRequest;
import com.example.hr_managment_system.dto.Attendance.AttendanceResponse;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceResponse clockIn(AttendanceRequest attendanceRequest);
    AttendanceResponse clockOut(AttendanceRequest attendanceRequest);
    List<AttendanceResponse> getAttendanceByEmployeeId(Boolean isActive);
    List<AttendanceResponse> getAttendanceForAdmin(Boolean isActive, LocalDate from, LocalDate to);
    List<AttendanceResponse> getAttendanceForHr(String departmentId, Integer month, Integer year);
    List<AttendanceResponse> getMyAttendance(String principal, LocalDate from, LocalDate to);
    List<AttendanceResponse> getAttendanceReport(String departmentId, Integer month, Integer year);

}
