package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, String> {

    boolean existsByDepartmentName(String departmentName);

    Optional<Department> findByDepartmentName(String departmentName);
}
