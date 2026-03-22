package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, String> {

    Optional<PerformanceReview> findByPerformanceReviewId(String Id);

    List<PerformanceReview> findByEmployee_EmployeeIdOrderByReviewDateDesc(String employeeId);
}
