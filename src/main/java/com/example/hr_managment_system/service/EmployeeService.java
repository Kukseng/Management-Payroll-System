package com.example.hr_managment_system.service;

import com.example.hr_managment_system.dto.Employee.EmployeeRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResetPasswordRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeRoleUpdateRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResponse;
import com.example.hr_managment_system.dto.Employee.EmployeeStatusUpdateRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeUpdate;

import java.util.List;

public interface EmployeeService {
    /**
     * Employee Management API Endpoints:
     *
            | --- | --- | --- |
            | `/api/employees` | POST | Create a new employee profile. |
            | `/api/employees/{id}` | GET | Retrieve a single employee profile by ID. |
            | `/api/employees/{id}` | PUT | Update an existing employee profile. |
            | `/api/employees/{id}` | DELETE | Deactivate an employee profile. |

     **/

    List<EmployeeResponse> getAllEmployees(Boolean isActive);

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse getEmployeeById(String id);

    EmployeeResponse updateEmployee(String id, EmployeeUpdate employeeUpdate);

    EmployeeResponse updateEmployeeRole(String id, EmployeeRoleUpdateRequest request);

    EmployeeResponse updateEmployeeStatus(String id, EmployeeStatusUpdateRequest request);

    void resetEmployeePassword(String id, EmployeeResetPasswordRequest request);

    void deleteEmployee(String id);

    EmployeeResponse uploadProfileImage(String id, org.springframework.web.multipart.MultipartFile file);
}
