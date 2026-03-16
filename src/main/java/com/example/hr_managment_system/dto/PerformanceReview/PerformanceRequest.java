package com.example.hr_managment_system.dto.PerformanceReview;

import java.time.LocalDate;

public record PerformanceRequest(

        String employeeId,
        String reviewId,
        String feedback,
        Double kpiScore


) {
}
//private Employee reviewer;
//
//    @Column(name = "feedback", columnDefinition = "TEXT")
//    private String feedback;
//
//    @Column(name = "kpi_score")
//    private Double kpiScore;
//
//    @Column(name = "review_date", nullable = false)
//    private LocalDate reviewDate;