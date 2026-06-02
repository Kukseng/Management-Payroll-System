package com.example.hr_managment_system.controller.view;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class EmployeeDashboardViewController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        try {
            model.addAttribute("username", auth.getName());
            model.addAttribute("role", "EMPLOYEE");
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load dashboard");
        }
        return "dashboard/employee-dashboard";
    }

    // My Profile
    @GetMapping("/profile")
    public String myProfile(Model model, Authentication auth) {
        model.addAttribute("role", "EMPLOYEE");
        return "employee/view";
    }

    // Attendance
    @GetMapping("/attendance")
    public String myAttendance(Model model) {
        model.addAttribute("role", "EMPLOYEE");
        return "attendance/my-attendance";
    }

    @GetMapping("/attendance/clock-inout")
    public String clockInOut(Model model) {
        model.addAttribute("role", "EMPLOYEE");
        return "attendance/clock-inout";
    }

    // Leave
    @GetMapping("/leaves")
    public String myLeaves(Model model) {
        model.addAttribute("role", "EMPLOYEE");
        return "leave/my-leaves";
    }

    @GetMapping("/leaves/request")
    public String requestLeave(Model model) {
        model.addAttribute("role", "EMPLOYEE");
        return "leave/request";
    }

    @GetMapping("/leaves/balance")
    public String leaveBalance(Model model) {
        model.addAttribute("role", "EMPLOYEE");
        return "leave/balance";
    }

    // Payroll
    @GetMapping("/payroll")
    public String myPayroll(Model model) {
        model.addAttribute("role", "EMPLOYEE");
        return "payroll/my-payroll";
    }

    @GetMapping("/payroll/{uuid}")
    public String payrollDetail(@PathVariable String uuid, Model model) {
        model.addAttribute("role", "EMPLOYEE");
        return "payroll/detail";
    }

    // Performance
    @GetMapping("/performance")
    public String myPerformance(Model model) {
        model.addAttribute("role", "EMPLOYEE");
        return "performance/my-reviews";
    }

    @GetMapping("/performance/{id}")
    public String performanceDetail(@PathVariable String id, Model model) {
        model.addAttribute("role", "EMPLOYEE");
        return "performance/detail";
    }
}

