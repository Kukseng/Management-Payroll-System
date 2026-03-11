package com.example.hr_managment_system.service;

import com.example.hr_managment_system.dto.Employee.EmployeeRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResponse;

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

    EmployeeResponse getAllEmployees(Boolean isActive);

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse getEmployeeById(String id);

    EmployeeResponse updateEmployee(String id, EmployeeRequest request);

    void deleteEmployee(String id);



}
