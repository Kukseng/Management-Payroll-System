package com.example.hr_managment_system.dto.Employee;

import com.example.hr_managment_system.domain.Department;

import java.time.LocalDateTime;

public record EmployeeResponse(
        String employeeId,
        String firstName,
        String lastName,
        String email,
        String position,
        String departmentId,
        String departmentName,
        String roleId,
        String roleName,
        String employmentType,
        Double baseSalary,
        LocalDateTime createdAt,
        boolean active
) {
}
// private Integer id;
//    private String firstName;
//    private String lastName;
//    private String email;
//    private LocalDate dateOfBirth;
//    private Integer departmentId;
//    private String departmentName;
//    private Integer roleId;
//    private String roleName;
//    private String employmentType;
//    private Double baseSalary;
//    private LocalDateTime createdAt;