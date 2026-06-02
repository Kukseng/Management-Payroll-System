package com.example.hr_managment_system.controller;


import com.example.hr_managment_system.dto.Payroll.PayrollRequest;
import com.example.hr_managment_system.dto.Payroll.PayrollResponse;
import com.example.hr_managment_system.service.PayrollService;
import com.example.hr_managment_system.service.PdfService;
import com.example.hr_managment_system.repository.PayrollRepository;
import com.example.hr_managment_system.domain.Payroll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ContentDisposition;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;
    private final PdfService pdfService;
    private final PayrollRepository payrollRepository;

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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/process/batch")
    public String processPayrollBatch(@RequestBody com.example.hr_managment_system.dto.Payroll.PayrollBatchRequest batchRequest) {
        return payrollService.processPayrollBatch(batchRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{uuid}")
    public PayrollResponse getPayrollByUuid(@PathVariable String uuid) {
        return payrollService.getPayrollByUuid(uuid);
    }

    @GetMapping("/{uuid}/download")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable String uuid) {
        Payroll payroll = payrollRepository.findByPayrollId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payroll record not found."));

        byte[] pdfBytes = pdfService.generatePayslipPdf(payroll);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "payslip_" + payroll.getMonth() + "_" + payroll.getYear() + "_" + uuid.substring(0, 8) + ".pdf";
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(filename).build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<PayrollResponse> getPayrolls(@RequestParam(required = false) String departmentId) {
        if (departmentId != null && !departmentId.trim().isEmpty()) {
            return payrollService.getPayrollByDepartmentId(departmentId);
        }
        return payrollService.getAllPayrolls();
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{uuid}/approve")
    public PayrollResponse approvePayroll(@PathVariable String uuid) {
        return payrollService.approvePayroll(uuid);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{uuid}")
    public void deletePayroll(@PathVariable String uuid) {
        payrollService.deletePayroll(uuid);
    }
}
//String processPayroll(PayrollRequest payrollRequest);
//    PayrollResponse getPayslipByEmployeeIdAndMonth(Integer employeeId, Integer month, Integer year);
//    List<PayrollResponse> getPayrollByEmployeeId(Integer employeeId);
