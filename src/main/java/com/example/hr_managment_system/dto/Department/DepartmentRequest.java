package com.example.hr_managment_system.dto.Department;

import com.example.hr_managment_system.domain.Employee;

public record DepartmentRequest(

        String DepartmentName,
        Employee managerId,
        String qrCode,
        Double officeLongitude,
//        Double officeLongitude,
        Double geofenceRadiusMeters

) {
}
