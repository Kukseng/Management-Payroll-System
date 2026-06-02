package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Attendance;
import com.example.hr_managment_system.domain.Department;
import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.dto.attendance.AttendanceRequest;
import com.example.hr_managment_system.dto.attendance.AttendanceResponse;
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

import java.time.LocalDate;
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

        // 1. Check if employee is already clocked in (any open attendance record)
        boolean isAlreadyClockedIn = attendanceRepository.findAll().stream()
                .anyMatch(a -> a.getEmployee().getEmployeeId().equals(attendanceRequest.employeeId()) && a.getClockOut() == null);
        if (isAlreadyClockedIn) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee is already clocked in. Please clock out first.");
        }

        // 2. Check if employee has already clocked in and out today
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay().minusNanos(1);
        List<Attendance> todayAttendances = attendanceRepository.findByEmployee_EmployeeIdAndClockInBetween(
                attendanceRequest.employeeId(), startOfDay, endOfDay
        );
        if (!todayAttendances.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee has already completed attendance for today.");
        }

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

        // Auto-calculate status (LATE or PRESENT) based on the employee's assigned shift
        StatusUtil calculatedStatus = StatusUtil.PRESENT;
        if (employee.getShift() != null && employee.getShift().getStartTime() != null) {
            java.time.LocalTime nowTime = java.time.LocalTime.now();
            int graceMinutes = employee.getShift().getGracePeriodMinutes() != null ? employee.getShift().getGracePeriodMinutes() : 0;
            java.time.LocalTime thresholdTime = employee.getShift().getStartTime().plusMinutes(graceMinutes);
            if (nowTime.isAfter(thresholdTime)) {
                calculatedStatus = StatusUtil.LATE;
            }
        }
        attendance.setStatus(calculatedStatus);

        attendanceRepository.save(attendance);

        return attendanceMapper.attendanceToAttendanceResponse(attendance);
    }

    @Override
    public AttendanceResponse clockOut(AttendanceRequest attendanceRequest) {
        validateClockOutRequest(attendanceRequest);

        Employee employee  = employeeRepository.findByEmployeeId(attendanceRequest.employeeId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Employee with ID " + attendanceRequest.employeeId() + " does not exist.")
        );

        Department department = departmentRepository.findById(attendanceRequest.departmentId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Department with ID " + attendanceRequest.departmentId() + " does not exist.")
        );

        validateEmployeeDepartment(employee, department);

        Attendance attendance = attendanceRepository
            .findTopByEmployee_EmployeeIdAndDepartment_DepartmentIdAndClockOutIsNullOrderByClockInDesc(
                attendanceRequest.employeeId(),
                attendanceRequest.departmentId()
            )
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No open clock-in record found for this employee and department."
            ));

        if (attendanceRequest.latitudeOut() != null || attendanceRequest.longitudeOut() != null) {
            if (attendanceRequest.latitudeOut() == null || attendanceRequest.longitudeOut() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both latitudeOut and longitudeOut are required together.");
            }
            validateGeoFence(attendanceRequest.latitudeOut(), attendanceRequest.longitudeOut(), department);
            attendance.setLatitudeOut(attendanceRequest.latitudeOut());
            attendance.setLongitudeOut(attendanceRequest.longitudeOut());
        }

        LocalDateTime clockOutTime = LocalDateTime.now();
        attendance.setClockOut(clockOutTime);
        if (attendance.getClockIn() != null) {
            java.time.Duration duration = java.time.Duration.between(attendance.getClockIn(), clockOutTime);
            double totalHours = duration.toMinutes() / 60.0;
            attendance.setTotalHours(totalHours);
            double overtime = totalHours > 8.0 ? totalHours - 8.0 : 0.0;
            attendance.setOvertimeHours(overtime);
        } else {
            attendance.setTotalHours(0.0);
            attendance.setOvertimeHours(0.0);
        }
        attendanceRepository.save(attendance);

        return attendanceMapper.attendanceToAttendanceResponse(attendance);
    }

    @Override
    public List<AttendanceResponse> getAttendanceByEmployeeId(Boolean isActive) {
        return attendanceRepository.findAll().stream()
                .filter(attendance -> hasMatchingActiveStatus(attendance, isActive))
                .map(attendanceMapper::attendanceToAttendanceResponse).toList();
    }

    @Override
    public List<AttendanceResponse> getAttendanceForAdmin(Boolean isActive, LocalDate from, LocalDate to) {
        LocalDate start = from == null ? LocalDate.now().minusDays(30) : from;
        LocalDate end = to == null ? LocalDate.now() : to;

        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from must be before or equal to to");
        }

        return attendanceRepository.findByClockInBetween(
                        start.atStartOfDay(),
                        end.plusDays(1).atStartOfDay().minusSeconds(1)
                )
                .stream()
                .filter(attendance -> hasMatchingActiveStatus(attendance, isActive))
                .map(attendanceMapper::attendanceToAttendanceResponse)
                .toList();
    }

    @Override
    public List<AttendanceResponse> getAttendanceForHr(String departmentId, Integer month, Integer year) {
        return getAttendanceReport(departmentId, month, year);
    }

    @Override
    public List<AttendanceResponse> getMyAttendance(String principal, LocalDate from, LocalDate to) {
        Employee employee = employeeRepository.findByUsernameAndIsActiveTrue(principal)
                .or(() -> employeeRepository.findByEmailAndIsActiveTrue(principal))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        LocalDate start = from == null ? LocalDate.now().minusDays(30) : from;
        LocalDate end = to == null ? LocalDate.now() : to;

        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from must be before or equal to to");
        }

        return attendanceRepository.findByEmployee_EmployeeIdAndClockInBetween(
                        employee.getEmployeeId(),
                        start.atStartOfDay(),
                        end.plusDays(1).atStartOfDay().minusSeconds(1)
                )
                .stream()
                .map(attendanceMapper::attendanceToAttendanceResponse)
                .toList();
    }

    @Override
    public List<AttendanceResponse> getAttendanceReport(String departmentId, Integer month, Integer year) {
        if (departmentId == null || departmentId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "departmentId is required");
        }
        if (month == null || month < 1 || month > 12 || year == null || year < 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid month and year are required");
        }

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return attendanceRepository.findByDepartment_DepartmentIdAndClockInBetween(
                        departmentId,
                        start.atStartOfDay(),
                        end.plusDays(1).atStartOfDay().minusSeconds(1)
                )
                .stream()
                .map(attendanceMapper::attendanceToAttendanceResponse)
                .toList();
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

    private void validateClockOutRequest(AttendanceRequest attendanceRequest) {
        if (isBlank(attendanceRequest.employeeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required for clock-out.");
        }
        if (isBlank(attendanceRequest.departmentId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "departmentId is required for clock-out.");
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

    private boolean hasMatchingActiveStatus(Attendance attendance, Boolean isActive) {
        if (attendance == null || attendance.getEmployee() == null) {
            return false;
        }
        if (isActive == null) {
            return true;
        }
        return isActive.equals(attendance.getEmployee().getIsActive());
    }
}
