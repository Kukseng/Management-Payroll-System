package com.example.hr_managment_system.mapper;

import com.example.hr_managment_system.domain.PerformanceReview;
import com.example.hr_managment_system.dto.PerformanceReview.PerformanceRequest;
import com.example.hr_managment_system.dto.PerformanceReview.PerformanceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PerformanceReviewMapper {

    PerformanceReview CreatePerformanceReview(PerformanceRequest performanceRequest);

    PerformanceResponse requestPerformanceReview(PerformanceReview  performanceReview);

}



