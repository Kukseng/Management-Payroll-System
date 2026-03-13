package com.example.hr_managment_system.controller;

import com.example.hr_managment_system.dto.Attendance.AttendanceRequest;
import com.example.hr_managment_system.dto.Attendance.AttendanceResponse;
import com.example.hr_managment_system.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            LocalDate date      ){
        return attendanceService.getAttendanceByEmployeeId(isActive);
    }

}
