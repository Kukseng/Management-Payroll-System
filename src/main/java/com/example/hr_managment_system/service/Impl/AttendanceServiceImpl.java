package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Attendance;
import com.example.hr_managment_system.domain.Department;
import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.dto.Attendance.AttendanceRequest;
import com.example.hr_managment_system.dto.Attendance.AttendanceResponse;
import com.example.hr_managment_system.mapper.AttendanceMapper;
import com.example.hr_managment_system.repository.AttendanceRepository;
import com.example.hr_managment_system.repository.DepartmentRepository;
import com.example.hr_managment_system.repository.EmployeeRepository;
import com.example.hr_managment_system.repository.RoleRepository;
import com.example.hr_managment_system.service.AttendanceService;
import com.example.hr_managment_system.util.StatusUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    public AttendanceResponse clockIn(AttendanceRequest attendanceRequest) {

      Employee employee  = employeeRepository.findByEmployeeId(attendanceRequest.employeeId()).orElseThrow(
              () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                      "Employee with ID " + attendanceRequest.employeeId() + " does not exist.")
      );

      Department department = departmentRepository.findById(attendanceRequest.departmentId()).orElseThrow(
              () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                      "Department with ID " + attendanceRequest.departmentId() + " does not exist.")
        );

        Attendance attendance = attendanceMapper.attendanceRequestToAttendance(attendanceRequest);
        attendance.setEmployee(employee);
        attendance.setDepartment(department);
        attendance.setClockIn(LocalDate.now().atStartOfDay());
        attendanceRepository.save(attendance);

        return attendanceMapper.attendanceToAttendanceResponse(attendance);
    }

    @Override
    public AttendanceResponse clockOut(AttendanceRequest attendanceRequest) {

        Employee employee  = employeeRepository.findByEmployeeId(attendanceRequest.employeeId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Employee with ID " + attendanceRequest.employeeId() + " does not exist.")
        );

        Department department = departmentRepository.findById(attendanceRequest.departmentId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Department with ID " + attendanceRequest.departmentId() + " does not exist.")
        );

        if (!attendanceRepository.existsByStatus(attendanceRequest.status())){
            Attendance attendance = new Attendance();
            attendance.setStatus(StatusUtil.None);
            attendance.setClockOut(LocalDate.now().atStartOfDay());
            attendanceRepository.save(attendance);
        }

        Attendance attendance = attendanceMapper.attendanceRequestToAttendance(attendanceRequest);
        attendance.setEmployee(employee);
        attendance.setDepartment(department);
        attendance.setClockOut(LocalDate.now().atStartOfDay());
        attendanceRepository.save(attendance);

        return attendanceMapper.attendanceToAttendanceResponse(attendance);
    }

    @Override
    public List<AttendanceResponse> getAttendanceByEmployeeId(Boolean isActive) {
        return attendanceRepository.findAll().stream()
                .filter(attendance -> attendance.getEmployee().getIsActive() == isActive)
                .map(attendanceMapper::attendanceToAttendanceResponse).toList();
    }
}
