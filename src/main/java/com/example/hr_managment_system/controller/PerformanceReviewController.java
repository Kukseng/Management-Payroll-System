package com.example.hr_managment_system.controller;


import com.example.hr_managment_system.dto.PerformanceReview.PerformanceRequest;
import com.example.hr_managment_system.dto.PerformanceReview.PerformanceResponse;
import com.example.hr_managment_system.repository.PerformanceReviewRepository;
import com.example.hr_managment_system.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performance")
public class PerformanceReviewController {

    private final PerformanceService performanceService;
    private final PerformanceReviewRepository performanceReviewRepository;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PerformanceResponse createPerformanceReview(@RequestBody PerformanceRequest performanceRequest) {
        return performanceService.createPerformance(performanceRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")

    public PerformanceResponse getPerformanceReview(@PathVariable String id) {
        return performanceService.getPerformanceByUuid(id);
    }


}
