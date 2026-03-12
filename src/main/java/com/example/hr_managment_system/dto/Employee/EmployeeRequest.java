package com.example.hr_managment_system.dto.Employee;

import com.example.hr_managment_system.util.EmplomentType;

import java.time.LocalDate;

public record EmployeeRequest(
        String firstName,
        String lastName,
        String email,
        LocalDate dateOfBirth,
        String departmentId,
        String roleId,
        EmplomentType employmentType,
        Double baseSalary
) {
}
//   private String firstName;
//    private String lastName;
//    private String email;
//    private String password;
//    private LocalDate dateOfBirth;
//    private Integer departmentId;
//    private Integer roleId;
//    private String employmentType;
//    private Double baseSalary;
