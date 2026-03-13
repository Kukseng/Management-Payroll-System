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
import com.example.hr_managment_system.service.AttendanceService;
import com.example.hr_managment_system.util.StatusUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private static final double EARTH_RADIUS_METERS = 6_371_000D;

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    public AttendanceResponse clockIn(AttendanceRequest attendanceRequest) {
        validateClockInRequest(attendanceRequest);

        Employee employee  = employeeRepository.findByEmployeeId(attendanceRequest.employeeId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Employee with ID " + attendanceRequest.employeeId() + " does not exist.")
        );

        Department department = departmentRepository.findById(attendanceRequest.departmentId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Department with ID " + attendanceRequest.departmentId() + " does not exist.")
        );

        validateEmployeeDepartment(employee, department);
        validateDepartmentQrCode(attendanceRequest.qrCode(), department);
        validateGeoFence(attendanceRequest.latitudeIn(), attendanceRequest.longitudeIn(), department);

        Attendance attendance = attendanceMapper.attendanceRequestToAttendance(attendanceRequest);
        attendance.setEmployee(employee);
        attendance.setDepartment(department);
        attendance.setClockIn(LocalDateTime.now());
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
            attendance.setClockOut(LocalDateTime.now());
            attendanceRepository.save(attendance);
        }

        Attendance attendance = attendanceMapper.attendanceRequestToAttendance(attendanceRequest);
        attendance.setEmployee(employee);
        attendance.setDepartment(department);
        attendance.setClockOut(LocalDateTime.now());
        attendanceRepository.save(attendance);

        return attendanceMapper.attendanceToAttendanceResponse(attendance);
    }

    @Override
    public List<AttendanceResponse> getAttendanceByEmployeeId(Boolean isActive) {
        return attendanceRepository.findAll().stream()
                .filter(attendance -> attendance.getEmployee().getIsActive() == isActive)
                .map(attendanceMapper::attendanceToAttendanceResponse).toList();
    }

    private void validateClockInRequest(AttendanceRequest attendanceRequest) {
        if (isBlank(attendanceRequest.employeeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required for clock-in.");
        }
        if (isBlank(attendanceRequest.departmentId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "departmentId is required for clock-in.");
        }
        if (isBlank(attendanceRequest.qrCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "qrCode is required for clock-in.");
        }
        if (attendanceRequest.latitudeIn() == null || attendanceRequest.longitudeIn() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "latitudeIn and longitudeIn are required for clock-in.");
        }
    }

    private void validateEmployeeDepartment(Employee employee, Department department) {
        Department employeeDepartment = employee.getDepartment();
        if (employeeDepartment == null || employeeDepartment.getDepartmentId() == null
                || !employeeDepartment.getDepartmentId().equals(department.getDepartmentId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Employee is not assigned to the selected department.");
        }
    }

    private void validateDepartmentQrCode(String requestQrCode, Department department) {
        if (isBlank(department.getQrCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department QR code is not configured.");
        }
        if (!department.getQrCode().trim().equals(requestQrCode.trim())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid department QR code.");
        }
    }

    private void validateGeoFence(Double requestLatitude, Double requestLongitude, Department department) {
        if (department.getOfficeLatitude() == null || department.getOfficeLongitude() == null
                || department.getGeofenceRadiusMeters() == null || department.getGeofenceRadiusMeters() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department geofence is not configured.");
        }
        double distanceMeters = calculateDistanceMeters(
                requestLatitude,
                requestLongitude,
                department.getOfficeLatitude(),
                department.getOfficeLongitude()
        );
        if (distanceMeters > department.getGeofenceRadiusMeters()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are outside the department geofence.");
        }
    }

    private double calculateDistanceMeters(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        double latitudeDeltaRadians = Math.toRadians(latitudeB - latitudeA);
        double longitudeDeltaRadians = Math.toRadians(longitudeB - longitudeA);
        double latitudeARadians = Math.toRadians(latitudeA);
        double latitudeBRadians = Math.toRadians(latitudeB);

        double a = Math.sin(latitudeDeltaRadians / 2) * Math.sin(latitudeDeltaRadians / 2)
                + Math.cos(latitudeARadians) * Math.cos(latitudeBRadians)
                * Math.sin(longitudeDeltaRadians / 2) * Math.sin(longitudeDeltaRadians / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
