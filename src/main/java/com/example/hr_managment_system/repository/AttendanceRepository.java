package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {
}
