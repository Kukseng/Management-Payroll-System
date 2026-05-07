package com.example.hr_managment_system.controller.view;

import com.example.hr_managment_system.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hr")
@RequiredArgsConstructor
public class HRDashboardViewController {

    private final LeaveService leaveService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        try {
            model.addAttribute("pendingLeaves", leaveService.getPendingLeaveRequests());
            model.addAttribute("username", auth.getName());
            model.addAttribute("role", "MANAGER");
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load dashboard data");
        }
        return "dashboard/hr-dashboard";
    }

    // Leave Management
    @GetMapping("/leaves/pending")
    public String pendingLeaves(Model model) {
        try {
            model.addAttribute("leaves", leaveService.getPendingLeaveRequests());
            model.addAttribute("role", "MANAGER");
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load leaves");
        }
        return "leave/pending";
    }

    @GetMapping("/leaves/{id}")
    public String viewLeave(@PathVariable String id, Model model) {
        try {
            model.addAttribute("role", "MANAGER");
            // Fetch leave details will be done via AJAX
        } catch (Exception e) {
            model.addAttribute("error", "Leave not found");
        }
        return "leave/detail";
    }

    // Attendance Reports
    @GetMapping("/attendance")
    public String attendanceReport(Model model) {
        model.addAttribute("role", "MANAGER");
        return "attendance/hr-report";
    }

    @GetMapping("/performance/create")
    public String createPerformance(Model model) {
        model.addAttribute("role", "MANAGER");
        return "performance/create";
    }

    @GetMapping("/performance/{id}")
    public String viewPerformance(Model model) {
        model.addAttribute("role", "MANAGER");
        return "performance/detail";
    }
}

