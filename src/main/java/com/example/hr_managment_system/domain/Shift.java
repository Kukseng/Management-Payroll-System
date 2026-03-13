package com.example.hr_managment_system.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table (name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String shiftId;

    @Column(name = "name", nullable = false, unique = true)
    private String name; // e.g., "Morning Shift", "General Shift"

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "grace_period_minutes")
    private Integer gracePeriodMinutes; // Minutes after start_time considered on-time

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL)
    private Set<Employee> employees;

}
