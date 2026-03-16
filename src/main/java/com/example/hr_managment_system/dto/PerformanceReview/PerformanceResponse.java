package com.example.hr_managment_system.dto.PerformanceReview;

import java.time.LocalDate;

public record PerformanceResponse(

        String performanceReviewId,
        String employeeId,
//        String reviewer,
        Double kpiScore,
        LocalDate reviewDate


) {
}
// @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private String performanceReviewId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "employee_id", nullable = false)
//    private Employee employee;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "reviewer_id")
//    private Employee reviewer;
//
//    @Column(name = "feedback", columnDefinition = "TEXT")
//    private String feedback;
//
//    @Column(name = "kpi_score")
//    private Double kpiScore;
//
//    @Column(name = "review_date", nullable = false)
//    private LocalDate reviewDate;