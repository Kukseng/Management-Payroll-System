package com.example.hr_managment_system.domain;

import com.example.hr_managment_system.util.StatusProgressUtil;
import com.example.hr_managment_system.util.TypeUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table (name = "leave_requests")
public class LeaveRequest {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String leaveRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "type", nullable = false)
    private TypeUtil type; // e.g., "SICK", "CASUAL", "ANNUAL"

    @Column(name = "status", nullable = false)
    private StatusProgressUtil status; // e.g., "PENDING", "APPROVED", "REJECTED"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

}
