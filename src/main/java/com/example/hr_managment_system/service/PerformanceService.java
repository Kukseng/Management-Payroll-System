package com.example.hr_managment_system.service;

import com.example.hr_managment_system.dto.PerformanceReview.PerformanceRequest;
import com.example.hr_managment_system.dto.PerformanceReview.PerformanceResponse;

public interface PerformanceService {

    PerformanceResponse createPerformance (PerformanceRequest performanceRequest);

    PerformanceResponse getPerformanceByUuid(String Uuid);

}
