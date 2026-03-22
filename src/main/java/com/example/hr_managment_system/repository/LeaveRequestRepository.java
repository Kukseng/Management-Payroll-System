package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.LeaveRequest;
import com.example.hr_managment_system.util.StatusProgressUtil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String> {
	List<LeaveRequest> findByEmployee_EmployeeIdOrderByStartDateDesc(String employeeId);

	List<LeaveRequest> findByStatusOrderByStartDateAsc(StatusProgressUtil status);

	List<LeaveRequest> findByEmployee_EmployeeIdAndStatusAndStartDateBetween(
			String employeeId,
			StatusProgressUtil status,
			LocalDate start,
			LocalDate end
	);
}
