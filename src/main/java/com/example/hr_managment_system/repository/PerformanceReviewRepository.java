package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, String> {
}
