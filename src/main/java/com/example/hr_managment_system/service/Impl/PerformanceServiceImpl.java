package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.domain.PerformanceReview;
import com.example.hr_managment_system.dto.PerformanceReview.PerformanceRequest;
import com.example.hr_managment_system.dto.PerformanceReview.PerformanceResponse;
import com.example.hr_managment_system.mapper.PerformanceReviewMapper;
import com.example.hr_managment_system.repository.AttendanceRepository;
import com.example.hr_managment_system.repository.EmployeeRepository;
import com.example.hr_managment_system.repository.PerformanceReviewRepository;
import com.example.hr_managment_system.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class PerformanceServiceImpl implements PerformanceService {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final PerformanceReviewMapper  performanceReviewMapper;

    @Override
    public PerformanceResponse createPerformance(PerformanceRequest performanceRequest) {

        Employee employee = employeeRepository.findByEmployeeId(performanceRequest.employeeId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found")
        );

        PerformanceReview performanceReview = performanceReviewMapper.CreatePerformanceReview(performanceRequest);
        performanceReview.setEmployee(employee);
        performanceReview.setReviewDate(LocalDateTime.now().toLocalDate());
       PerformanceReview performanceReviewSave = performanceReviewRepository.save(performanceReview);


        return performanceReviewMapper.requestPerformanceReview(performanceReviewSave);
    }

    @Override
    public PerformanceResponse getPerformanceByUuid(String Uuid) {



        return performanceReviewRepository.findByPerformanceReviewId(Uuid).map(
                performanceReviewMapper::requestPerformanceReview
        ).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found")
        );
    }
}
