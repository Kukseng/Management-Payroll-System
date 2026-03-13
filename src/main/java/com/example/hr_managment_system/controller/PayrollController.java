package com.example.hr_managment_system.controller;


import com.example.hr_managment_system.dto.Payroll.PayrollRequest;
import com.example.hr_managment_system.dto.Payroll.PayrollResponse;
import com.example.hr_managment_system.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/employee/{employeeId}/month")
    public PayrollResponse getPayslipByEmployeeIdAndMonth(@PathVariable String employeeId,
                                                          @RequestParam Integer month,
                                                          @RequestParam Integer year) {
        return payrollService.getPayslipByEmployeeIdAndMonth(employeeId, month, year);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/employee/{employeeId}")
    public List<PayrollResponse> getPayslipByEmployeeId(@PathVariable String employeeId) {
        return payrollService.getPayrollByEmployeeId(employeeId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/process")
    public String processPayroll(@RequestBody PayrollRequest payrollRequest) {
        return payrollService.processPayroll(payrollRequest);
    }

}
//String processPayroll(PayrollRequest payrollRequest);
//    PayrollResponse getPayslipByEmployeeIdAndMonth(Integer employeeId, Integer month, Integer year);
//    List<PayrollResponse> getPayrollByEmployeeId(Integer employeeId);
