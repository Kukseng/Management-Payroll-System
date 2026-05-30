package com.example.hr_managment_system.dto.Department;

public record DepartmentResponse(
        String departmentId,
        String departmentName,
        String managerId,
        String qrCode,
        Double officeLatitude,
        Double officeLongitude,
        Double geofenceRadiusMeters,
        Boolean isActive
) {
}