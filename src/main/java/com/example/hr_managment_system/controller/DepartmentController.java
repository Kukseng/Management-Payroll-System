package com.example.hr_managment_system.controller;

import com.example.hr_managment_system.dto.Department.DepartmentRequest;
import com.example.hr_managment_system.dto.Department.DepartmentResponse;
import com.example.hr_managment_system.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentService.getAllDepartment();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse createDepartment(@RequestBody DepartmentRequest departmentRequest) {
        return departmentService.createDepartment(departmentRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDepartment(@PathVariable String id) {
        departmentService.deleteDepartment(id);
    }

    @PostMapping("/{id}/enable")
    @ResponseStatus(HttpStatus.OK)
    public DepartmentResponse enableDepartment(@PathVariable String id) {
        return departmentService.enableDepartment(id);
    }
}
