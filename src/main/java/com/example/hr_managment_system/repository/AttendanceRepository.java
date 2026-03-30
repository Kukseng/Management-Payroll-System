package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.Attendance;
import com.example.hr_managment_system.util.StatusUtil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {

    boolean existsByAttendanceId(String id);

    boolean existsByStatus(StatusUtil status);

    List<Attendance> findByEmployee_EmployeeIdAndClockInBetween(
            String employeeId,
            LocalDateTime from,
            LocalDateTime to
    );

    List<Attendance> findByDepartment_DepartmentIdAndClockInBetween(
            String departmentId,
            LocalDateTime from,
            LocalDateTime to
    );

    List<Attendance> findByClockInBetween(
            LocalDateTime from,
            LocalDateTime to
    );

}
