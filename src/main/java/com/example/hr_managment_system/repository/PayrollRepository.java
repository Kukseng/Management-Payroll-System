package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, String> {
    boolean existsByEmployee_EmployeeIdAndMonthAndYear(String employeeId, Integer month, Integer year);

    Optional<Payroll> findByEmployee_EmployeeIdAndMonthAndYear(String employeeId, Integer month, Integer year);

    List<Payroll> findByEmployee_EmployeeId(String employeeId);
}
