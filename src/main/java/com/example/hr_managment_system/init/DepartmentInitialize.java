package com.example.hr_managment_system.init;

import com.example.hr_managment_system.domain.Department;
import com.example.hr_managment_system.repository.DepartmentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DepartmentInitialize {

    private final DepartmentRepository departmentRepository;

    @PostConstruct
    public void initializeDepartment() {
        List<Department> missingDepartments = new ArrayList<>();

        addDepartmentIfMissing(missingDepartments, "IT");
        addDepartmentIfMissing(missingDepartments, "HR");

        if (!missingDepartments.isEmpty()) {
            departmentRepository.saveAll(missingDepartments);
        }
    }

    private void addDepartmentIfMissing(List<Department> missingDepartments, String departmentName) {
        if (departmentRepository.existsByDepartmentName(departmentName)) {
            return;
        }

        Department department = new Department();
        department.setDepartmentName(departmentName);
        missingDepartments.add(department);
    }
}
