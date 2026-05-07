package com.example.hr_managment_system.controller;

import com.example.hr_managment_system.dto.attendance.AttendanceRequest;
import com.example.hr_managment_system.dto.attendance.AttendanceResponse;
import com.example.hr_managment_system.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/clock-in")
    public AttendanceResponse clockIn(@RequestBody AttendanceRequest attendanceRequest) {
        return attendanceService.clockIn(attendanceRequest);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/clock-out")
    public AttendanceResponse clockOut(@RequestBody AttendanceRequest attendanceRequest) {
        return attendanceService.clockOut(attendanceRequest);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/employees")
    public List<AttendanceResponse> getAttendanceByEmployeeId(
            @RequestParam(defaultValue = "true") Boolean isActive,
            @RequestParam(required = false) LocalDate date) {
        return attendanceService.getAttendanceByEmployeeId(isActive);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/my")
    public List<AttendanceResponse> getMyAttendance(
            Authentication authentication,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return attendanceService.getMyAttendance(authentication.getName(), from, to);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/hr")
    public List<AttendanceResponse> getAttendanceForHr(
            @RequestParam String departmentId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return attendanceService.getAttendanceForHr(departmentId, month, year);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin")
    public List<AttendanceResponse> getAttendanceForAdmin(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return attendanceService.getAttendanceForAdmin(isActive, from, to);
    }

}
