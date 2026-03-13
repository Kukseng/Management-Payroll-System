package com.example.hr_managment_system.dto.Attendance;

import com.example.hr_managment_system.util.StatusUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceRequest(

        String employeeId,
        String departmentId,
        LocalDateTime clockIn,
        LocalDateTime clockOut,
        Double latitudeIn,
        Double latitudeOut,
        StatusUtil status

) {
}

// @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private String attendanceId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "employee_id", nullable = false)
//    private Employee employee;
//
//    @Column(name = "clock_in", nullable = false)
//    private LocalDateTime clockIn;
//
//    @Column(name = "clock_out")
//    private LocalDateTime clockOut;
//
//    @Column(name = "latitude_in")
//    private Double latitudeIn;
//
//    @Column(name = "longitude_in")
//    private Double longitudeIn;
//
//    @Column(name = "latitude_out")
//    private Double latitudeOut;
//
//    @Column(name = "longitude_out")
//    private Double longitudeOut;
//
//    @Column(name = "status", nullable = false)
//    private StatusUtil status; // e.g., "PRESENT", "LATE", "ABSENT"
//}