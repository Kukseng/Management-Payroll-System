package com.example.hr_managment_system.domain;

import com.example.hr_managment_system.util.StatusUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "attendances")

public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;


    @Column(name = "clock_in", nullable = false)
    private LocalDateTime clockIn;

    @Column(name = "clock_out")
    private LocalDateTime clockOut;

    @Column(name = "latitude_in")
    private Double latitudeIn;

    @Column(name = "longitude_in")
    private Double longitudeIn;

    @Column(name = "latitude_out")
    private Double latitudeOut;

    @Column(name = "longitude_out")
    private Double longitudeOut;

    @Column(name = "status", nullable = false)
    private StatusUtil status; // e.g., "PRESENT", "LATE", "ABSENT"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( nullable = false)
    private Department department;
}
