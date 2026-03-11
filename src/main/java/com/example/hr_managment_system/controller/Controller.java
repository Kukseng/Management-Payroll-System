package com.example.hr_managment_system.controller;


import com.example.hr_managment_system.dto.Employee.EmployeeRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResponse;
import com.example.hr_managment_system.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class Controller {

    private final EmployeeService employeeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponse getAllEmployees(@RequestParam Boolean isActive) {
        return employeeService.getAllEmployees(isActive);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        return employeeService.createEmployee(request);
    }


}
