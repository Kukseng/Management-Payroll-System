package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Department;
import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.dto.Department.DepartmentRequest;
import com.example.hr_managment_system.dto.Department.DepartmentResponse;
import com.example.hr_managment_system.repository.DepartmentRepository;
import com.example.hr_managment_system.repository.EmployeeRepository;
import com.example.hr_managment_system.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public DepartmentResponse createDepartment(DepartmentRequest departmentRequest) {
        if (departmentRepository.existsByDepartmentName(departmentRequest.DepartmentName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department name already exists");
        }
        Department department = new Department();
        department.setDepartmentName(departmentRequest.DepartmentName());

        Employee manager = null;
        if (departmentRequest.managerId() != null && departmentRequest.managerId().getEmployeeId() != null) {
            manager = employeeRepository.findById(departmentRequest.managerId().getEmployeeId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            "Manager employee with ID " + departmentRequest.managerId().getEmployeeId() + " not found")
            );
        }
        department.setManagerId(manager);

        department.setQrCode(departmentRequest.qrCode());
        department.setOfficeLatitude(departmentRequest.officeLatitude());
        department.setOfficeLongitude(departmentRequest.officeLongitude());
        department.setGeofenceRadiusMeters(departmentRequest.geofenceRadiusMeters());
        department.setIsActive(true);

        Department saved = departmentRepository.save(department);
        return mapToResponse(saved);
    }

    @Override
    public DepartmentResponse getDepartmentId(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        return mapToResponse(department);
    }

    @Override
    public List<DepartmentResponse> getAllDepartment() {
        return departmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteDepartment(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        department.setIsActive(false);
        departmentRepository.save(department);
    }

    @Override
    public DepartmentResponse enableDepartment(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        department.setIsActive(true);
        Department saved = departmentRepository.save(department);
        return mapToResponse(saved);
    }

    private DepartmentResponse mapToResponse(Department department) {
        return new DepartmentResponse(
                department.getDepartmentId(),
                department.getDepartmentName(),
                department.getManagerId() != null ? department.getManagerId().getEmployeeId() : null,
                department.getManagerId() != null ? (department.getManagerId().getFirstName() + " " + department.getManagerId().getLastName()) : null,
                department.getQrCode(),
                department.getOfficeLatitude(),
                department.getOfficeLongitude(),
                department.getGeofenceRadiusMeters(),
                department.getIsActive()
        );
    }
}
