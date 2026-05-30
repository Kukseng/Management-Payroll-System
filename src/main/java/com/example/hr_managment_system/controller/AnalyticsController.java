package com.example.hr_managment_system.controller;

import com.example.hr_managment_system.domain.Attendance;
import com.example.hr_managment_system.domain.LeaveRequest;
import com.example.hr_managment_system.domain.Payroll;
import com.example.hr_managment_system.repository.AttendanceRepository;
import com.example.hr_managment_system.repository.LeaveRequestRepository;
import com.example.hr_managment_system.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final LeaveRequestRepository leaveRequestRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollRepository payrollRepository;

    @GetMapping("/leaves")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getLeaveAnalytics() {
        List<LeaveRequest> leaves = leaveRequestRepository.findAll();
        
        Map<String, Long> byType = leaves.stream()
                .filter(l -> l.getType() != null)
                .collect(Collectors.groupingBy(l -> l.getType().toString(), Collectors.counting()));
                
        Map<String, Long> byStatus = leaves.stream()
                .filter(l -> l.getStatus() != null)
                .collect(Collectors.groupingBy(l -> l.getStatus().toString(), Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        result.put("byType", byType);
        result.put("byStatus", byStatus);
        return result;
    }

    @GetMapping("/attendance")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Long> getAttendanceAnalytics() {
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(7);
        LocalDateTime endOfWeek = LocalDateTime.now();

        List<Attendance> attendances = attendanceRepository.findByClockInBetween(startOfWeek, endOfWeek);

        return attendances.stream()
                .filter(a -> a.getStatus() != null)
                .collect(Collectors.groupingBy(a -> a.getStatus().toString(), Collectors.counting()));
    }

    @GetMapping("/payroll")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Double> getPayrollAnalytics() {
        int currentYear = LocalDateTime.now().getYear();
        List<Payroll> payrolls = payrollRepository.findAll();

        Map<Integer, Double> byMonth = payrolls.stream()
                .filter(p -> p.getYear() == currentYear)
                .collect(Collectors.groupingBy(
                        Payroll::getMonth,
                        Collectors.summingDouble(Payroll::getNetPay)
                ));

        // Format keys as string names of months for easier rendering in frontend chart
        String[] monthNames = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        Map<String, Double> formatted = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            formatted.put(monthNames[i], byMonth.getOrDefault(i, 0.0));
        }
        return formatted;
    }
}
