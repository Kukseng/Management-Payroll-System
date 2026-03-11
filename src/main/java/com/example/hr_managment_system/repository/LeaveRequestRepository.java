package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String> {
}
