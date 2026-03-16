package com.example.hr_managment_system.service;


import com.example.hr_managment_system.dto.Department.DepartmentRequest;
import com.example.hr_managment_system.dto.Department.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    DepartmentResponse createDepartment(DepartmentRequest departmentRequest);

    DepartmentResponse getDepartmentId(String id);

    List<DepartmentResponse> getAllDepartment();

    void deleteDepartment(String id);


}
