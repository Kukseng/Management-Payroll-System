package com.example.hr_managment_system.domain;

import com.example.hr_managment_system.util.StatusProgressUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table (name = "payrolls")
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String payrollId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "gross_salary", nullable = false)
    private Double grossSalary;

    @Column(name = "deductions", nullable = false)
    private Double deductions;

    @Column(name = "net_pay", nullable = false)
    private Double netPay;

    @Column(name = "status", nullable = false)
    private StatusProgressUtil status; // e.g., "PENDING", "APPROVED", "PAID"

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;
}
