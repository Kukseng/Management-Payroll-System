package com.example.hr_managment_system.dto.PerformanceReview;

public record PerformanceUpdateRequest(
        String feedback,
        Double kpiScore
) {
}
