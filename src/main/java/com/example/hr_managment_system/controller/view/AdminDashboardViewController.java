package com.example.hr_managment_system.controller.view;

import com.example.hr_managment_system.service.EmployeeService;
import com.example.hr_managment_system.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardViewController {

    private final EmployeeService employeeService;
    private final LeaveService leaveService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        try {
            model.addAttribute("employees", employeeService.getAllEmployees(true));
            model.addAttribute("username", auth.getName());
            model.addAttribute("totalEmployees", employeeService.getAllEmployees(true).size());
            model.addAttribute("role", "ADMIN");
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load dashboard data");
        }
        return "dashboard/admin-dashboard";
    }

    // Employee Management
    @GetMapping("/employees")
    public String listEmployees(Model model) {
        try {
            java.util.List<com.example.hr_managment_system.dto.Employee.EmployeeResponse> all = new java.util.ArrayList<>();
            all.addAll(employeeService.getAllEmployees(true));
            all.addAll(employeeService.getAllEmployees(false));
            model.addAttribute("employees", all);
            model.addAttribute("role", "ADMIN");
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load employees");
        }
        return "employee/list";
    }

    @GetMapping("/employees/create")
    public String createEmployeeForm(Model model) {
        model.addAttribute("role", "ADMIN");
        return "employee/create";
    }

    @GetMapping("/employees/{id}")
    public String viewEmployee(@PathVariable String id, Model model) {
        try {
            model.addAttribute("employee", employeeService.getEmployeeById(id));
            model.addAttribute("role", "ADMIN");
        } catch (Exception e) {
            model.addAttribute("error", "Employee not found");
        }
        return "employee/detail";
    }

    @GetMapping("/employees/{id}/edit")
    public String editEmployeeForm(@PathVariable String id, Model model) {
        try {
            model.addAttribute("employee", employeeService.getEmployeeById(id));
            model.addAttribute("role", "ADMIN");
        } catch (Exception e) {
            model.addAttribute("error", "Employee not found");
        }
        return "employee/edit";
    }

    // Department Management
    @GetMapping({"/departments", "/department"})
    public String manageDepartments(Model model) {
        model.addAttribute("role", "ADMIN");
        return "department/manage";
    }

    // Holiday Management
    @GetMapping("/holidays")
    public String manageHolidays(Model model) {
        model.addAttribute("role", "ADMIN");
        return "holiday/manage";
    }

    // Attendance Reports
    @GetMapping("/attendance")
    public String attendanceReport(Model model) {
        model.addAttribute("role", "ADMIN");
        return "attendance/admin-report";
    }

    // Leave Management
    @GetMapping("/leaves/pending")
    public String pendingLeaves(Model model) {
        try {
            model.addAttribute("leaves", leaveService.getPendingLeaveRequests());
            model.addAttribute("role", "ADMIN");
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load leaves");
        }
        return "leave/pending";
    }

    // Payroll Management
    @GetMapping("/payroll")
    public String payrollPage(Model model) {
        model.addAttribute("role", "ADMIN");
        return "payroll/reports";
    }

    @GetMapping("/payroll/process")
    public String processPayrollForm(Model model) {
        model.addAttribute("role", "ADMIN");
        return "payroll/process";
    }

    @GetMapping("/shifts")
    public String shiftsPage(Model model) {
        model.addAttribute("role", "ADMIN");
        return "shift/manage";
    }
}

