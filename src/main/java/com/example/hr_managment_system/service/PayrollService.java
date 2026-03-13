package com.example.hr_managment_system.service;

import com.example.hr_managment_system.dto.Payroll.PayrollRequest;
import com.example.hr_managment_system.dto.Payroll.PayrollResponse;

import java.util.List;

public interface PayrollService {
    String processPayroll(PayrollRequest payrollRequest);
    PayrollResponse getPayslipByEmployeeIdAndMonth(String employeeId, Integer month, Integer year);
    List<PayrollResponse> getPayrollByEmployeeId(String employeeId);
}
