package com.example.hr_managment_system.mapper;

import com.example.hr_managment_system.domain.Attendance;
import com.example.hr_managment_system.dto.Attendance.AttendanceRequest;
import com.example.hr_managment_system.dto.Attendance.AttendanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {


    @Mapping(source = "department.departmentId", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    @Mapping(source = "employee.employeeId", target = "employeeId")
    @Mapping(source = "status", target = "statusName")
    AttendanceResponse attendanceToAttendanceResponse(Attendance attendance);


//    @Mapping(source = "employee.employeeId", target = "employeeId")
//    @Mapping(source = "department.departmentId", target = "departmentId")
    Attendance attendanceRequestToAttendance(AttendanceRequest attendanceRequest);

}
