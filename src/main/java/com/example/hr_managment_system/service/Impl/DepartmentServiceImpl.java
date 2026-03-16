package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.dto.Department.DepartmentRequest;
import com.example.hr_managment_system.dto.Department.DepartmentResponse;
import com.example.hr_managment_system.service.DepartmentService;

import java.util.List;

public class DepartmentServiceImpl implements DepartmentService {

    @Override
    public DepartmentResponse createDepartment(DepartmentRequest departmentRequest) {
        return null;
    }

    @Override
    public DepartmentResponse getDepartmentId(String id) {
        return null;
    }

    @Override
    public List<DepartmentResponse> getAllDepartment() {
        return List.of();
    }

    @Override
    public void deleteDepartment(String id) {

    }
}
