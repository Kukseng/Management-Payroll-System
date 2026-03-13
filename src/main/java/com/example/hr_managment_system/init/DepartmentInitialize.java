package com.example.hr_managment_system.init;

import com.example.hr_managment_system.domain.Department;
import com.example.hr_managment_system.repository.DepartmentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

@Component
@RequiredArgsConstructor
public class DepartmentInitialize {

    private final DepartmentRepository departmentRepository;

    @PostConstruct
    public void initializeDepartment() {
        List<Department> missingDepartments = new ArrayList<>();

        addDepartmentIfMissingOrIncomplete(missingDepartments, "IT", "QR-IT-DEFAULT", 23.8103, 90.4125, 150D);
        addDepartmentIfMissingOrIncomplete(missingDepartments, "HR", "QR-HR-DEFAULT", 23.8108, 90.4130, 150D);

        if (!missingDepartments.isEmpty()) {
            departmentRepository.saveAll(missingDepartments);
        }
    }

    private void addDepartmentIfMissingOrIncomplete(
            List<Department> missingDepartments,
            String departmentName,
            String defaultQrCode,
            double defaultOfficeLatitude,
            double defaultOfficeLongitude,
            double defaultGeofenceRadiusMeters
    ) {
        Department existingDepartment = departmentRepository.findByDepartmentName(departmentName).orElse(null);
        if (existingDepartment != null) {
            boolean updated = false;
            if (isBlank(existingDepartment.getQrCode())) {
                existingDepartment.setQrCode(defaultQrCode);
                updated = true;
            }
            if (existingDepartment.getOfficeLatitude() == null) {
                existingDepartment.setOfficeLatitude(defaultOfficeLatitude);
                updated = true;
            }
            if (existingDepartment.getOfficeLongitude() == null) {
                existingDepartment.setOfficeLongitude(defaultOfficeLongitude);
                updated = true;
            }
            if (existingDepartment.getGeofenceRadiusMeters() == null || existingDepartment.getGeofenceRadiusMeters() <= 0) {
                existingDepartment.setGeofenceRadiusMeters(defaultGeofenceRadiusMeters);
                updated = true;
            }
            if (updated) {
                departmentRepository.save(existingDepartment);
            }
            return;
        }

        Department department = new Department();
        department.setDepartmentName(departmentName);
        department.setQrCode(defaultQrCode + randomUUID()); // Ensure unique QR code
        department.setOfficeLatitude(defaultOfficeLatitude);
        department.setOfficeLongitude(defaultOfficeLongitude);
        department.setGeofenceRadiusMeters(defaultGeofenceRadiusMeters);
        missingDepartments.add(department);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
