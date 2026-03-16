package com.example.hr_managment_system.dto.Department;

public record DepartmentResponse(

        String DepartmentName,
        String managerId,
        String qrCode,
        Double OfficeLongitude,
        Double geofenceRadiusMeters

) {
}


// @Column(name = "department_name", nullable = false, unique = true)
//    private String departmentName;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "manager_id")
//    private Employee managerId;
//
//    @Column(name = "qr_code", unique = true)
//    private String qrCode;
//
//    @Column(name = "office_latitude")
//    private Double officeLatitude;
//
//    @Column(name = "office_longitude")
//    private Double officeLongitude;
//
//    @Column(name = "geofence_radius_meters")
//    private Double geofenceRadiusMeters;
//
//    @OneToMany(cascade = CascadeType.ALL)
//    @JoinColumn(name = "department_id")
//    private Set<Employee> employees;
//
//    @OneToMany(cascade = CascadeType.ALL)
//    @JoinColumn(name = "department_id")
//    private Set<Attendance> attendance;