package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmployeeId(String employeeId);

    Optional<Employee> findByEmployeeId(String employeeId);

    @EntityGraph(attributePaths = {"role"})
    Optional<Employee> findByEmailAndIsActiveTrue(String email);

    @EntityGraph(attributePaths = {"role"})
    Optional<Employee> findByUsernameAndIsActiveTrue(String username);

    @EntityGraph(attributePaths = {"role"})
    Optional<Employee> findByUsername(String username);

    List<Employee> findAllByIsActiveTrue();
}
