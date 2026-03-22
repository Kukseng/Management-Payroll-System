package com.example.hr_managment_system.controller;


import com.example.hr_managment_system.dto.Employee.EmployeeRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResetPasswordRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeRoleUpdateRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResponse;
import com.example.hr_managment_system.dto.Employee.EmployeeStatusUpdateRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeUpdate;
import com.example.hr_managment_system.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeResponse> getAllEmployees(@RequestParam(defaultValue ="true") Boolean isActive) {
        return employeeService.getAllEmployees(isActive);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(@RequestBody EmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponse getEmployeeById(@PathVariable String id) {
        return employeeService.getEmployeeById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponse updateEmployee(@PathVariable String id, @RequestBody EmployeeUpdate request) {
        return employeeService.updateEmployee(id, request);
    }

    @PatchMapping("/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponse updateEmployeeRole(@PathVariable String id, @RequestBody EmployeeRoleUpdateRequest request) {
        return employeeService.updateEmployeeRole(id, request);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponse updateEmployeeStatus(@PathVariable String id, @RequestBody EmployeeStatusUpdateRequest request) {
        return employeeService.updateEmployeeStatus(id, request);
    }

    @PatchMapping("/{id}/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetEmployeePassword(@PathVariable String id, @RequestBody EmployeeResetPasswordRequest request) {
        employeeService.resetEmployeePassword(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
    }


}
