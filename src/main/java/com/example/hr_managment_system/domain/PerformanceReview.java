package com.example.hr_managment_system.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@Entity
@Table(name = "performance_reviews")
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String performanceReviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private Employee reviewer;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "kpi_score")
    private Double kpiScore;

    @Column(name = "review_date", nullable = false)
    private LocalDate reviewDate;
}
