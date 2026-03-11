package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRepository extends JpaRepository<Payroll, String> {
}
