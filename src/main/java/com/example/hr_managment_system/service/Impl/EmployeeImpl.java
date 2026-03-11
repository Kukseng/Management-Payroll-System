package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.dto.Employee.EmployeeRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResponse;
import com.example.hr_managment_system.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeImpl implements EmployeeService {


    @Override
    public EmployeeResponse getAllEmployees(Boolean isActive) {
        return null;
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        return null;
    }

    @Override
    public EmployeeResponse getEmployeeById(String id) {
        return null;
    }

    @Override
    public EmployeeResponse updateEmployee(String id, EmployeeRequest request) {
        return null;
    }

    @Override
    public void deleteEmployee(String id) {

    }
}
