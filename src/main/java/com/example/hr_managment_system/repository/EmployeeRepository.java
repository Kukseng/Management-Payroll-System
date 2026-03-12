package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    boolean existsByEmail(String email);



}
