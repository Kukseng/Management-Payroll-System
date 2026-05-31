package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.domain.Payroll;
import com.example.hr_managment_system.dto.Payroll.PayrollRequest;
import com.example.hr_managment_system.dto.Payroll.PayrollResponse;
import com.example.hr_managment_system.mapper.PayrollMapper;
import com.example.hr_managment_system.domain.Attendance;
import com.example.hr_managment_system.repository.AttendanceRepository;
import com.example.hr_managment_system.repository.EmployeeRepository;
import com.example.hr_managment_system.repository.PayrollRepository;
import com.example.hr_managment_system.service.PayrollService;
import com.example.hr_managment_system.service.EmailService;
import com.example.hr_managment_system.util.StatusProgressUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollMapper payrollMapper;
    private final AttendanceRepository attendanceRepository;
    private final EmailService emailService;

    @Override
    public String processPayroll(PayrollRequest payrollRequest) {
        validatePayrollRequest(payrollRequest);

        Employee employee = employeeRepository.findByEmployeeId(payrollRequest.employeeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        if (payrollRepository.existsByEmployee_EmployeeIdAndMonthAndYear(
                payrollRequest.employeeId(), payrollRequest.month(), payrollRequest.year())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Payroll already processed for this month and year.");
        }

        // Calculate Overtime from Attendance
        LocalDateTime startOfMonth = LocalDateTime.of(payrollRequest.year(), payrollRequest.month(), 1, 0, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

        List<Attendance> attendances = attendanceRepository.findByEmployee_EmployeeIdAndClockInBetween(
                payrollRequest.employeeId(), startOfMonth, endOfMonth);

        double overtimeHours = attendances.stream()
                .filter(a -> a.getOvertimeHours() != null)
                .mapToDouble(Attendance::getOvertimeHours)
                .sum();

        double baseSalary = employee.getBaseSalary() == null ? 0D : employee.getBaseSalary();
        double hourlyRate = baseSalary / 160.0;
        double overtimeRate = hourlyRate > 0 ? hourlyRate * 1.5 : 25.0; // fallback standard $25/hr
        double overtimePay = overtimeHours * overtimeRate;

        double grossSalary = baseSalary + overtimePay;
        double deductions = payrollRequest.deductions() == null ? 0D : payrollRequest.deductions();
        if (deductions < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "deductions must be greater than or equal to 0.");
        }
        double netPay = grossSalary - deductions;
        if (netPay < 0) {
            netPay = 0D;
        }

        Payroll payroll = payrollMapper.PayrollRequesttoPayroll(payrollRequest);
        payroll.setEmployee(employee);
        payroll.setGrossSalary(grossSalary);
        payroll.setDeductions(deductions);
        payroll.setNetPay(netPay);
        payroll.setOvertimeHours(overtimeHours);
        payroll.setOvertimePay(overtimePay);
        payroll.setStatus(StatusProgressUtil.PENDING);
        payroll.setProcessedAt(LocalDateTime.now());
        payrollRepository.save(payroll);

        // Notify Employee via email
        try {
            String monthYear = payrollRequest.month() + "/" + payrollRequest.year();
            emailService.sendPayrollProcessedEmail(
                    employee.getEmail(),
                    employee.getFirstName() + " " + employee.getLastName(),
                    monthYear,
                    netPay
            );
        } catch (Exception e) {
            log.error("Failed to send payroll email notification: {}", e.getMessage());
        }

        return "Payroll processed for employee " + payrollRequest.employeeId()
                + " (" + payrollRequest.month() + "/" + payrollRequest.year() + ").";
    }

    @Override
    public PayrollResponse getPayslipByEmployeeIdAndMonth(String employeeId, Integer month, Integer year) {
        validateEmployeeId(employeeId);
        validateMonthYear(month, year);

        Payroll payroll = payrollRepository.findByEmployee_EmployeeIdAndMonthAndYear(employeeId, month, year)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payslip not found for employee and period."));
        return payrollMapper.PayrolltoPayrollResponse(payroll);
    }

    @Override
    public List<PayrollResponse> getPayrollByEmployeeId(String employeeId) {
        validateEmployeeId(employeeId);
        return payrollRepository.findByEmployee_EmployeeId(employeeId)
                .stream()
                .map(payrollMapper::PayrolltoPayrollResponse)
                .toList();
    }

    @Override
    public PayrollResponse getPayrollByUuid(String uuid) {
        return payrollRepository.findByPayrollId(uuid)
                .map(payrollMapper::PayrolltoPayrollResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payroll not found for this uuid."));
    }


    @Override
    public PayrollResponse approvePayroll(String uuid) {
        Payroll payroll = payrollRepository.findByPayrollId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payroll record not found."));
        payroll.setStatus(StatusProgressUtil.APPROVED);
        Payroll saved = payrollRepository.save(payroll);
        return payrollMapper.PayrolltoPayrollResponse(saved);
    }

    @Override
    public List<PayrollResponse> getAllPayrolls() {
        return payrollRepository.findAll()
                .stream()
                .map(payrollMapper::PayrolltoPayrollResponse)
                .toList();
    }

    @Override
    public List<PayrollResponse> getPayrollByDepartmentId(String departmentId) {
        if (departmentId == null || departmentId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "departmentId is required.");
        }
        return payrollRepository.findByEmployee_Department_DepartmentId(departmentId)
                .stream()
                .map(payrollMapper::PayrolltoPayrollResponse)
                .toList();
    }

    private void validatePayrollRequest(PayrollRequest payrollRequest) {
        if (payrollRequest == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payroll request is required.");
        }
        validateEmployeeId(payrollRequest.employeeId());
        validateMonthYear(payrollRequest.month(), payrollRequest.year());
    }

    private void validateEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required.");
        }
    }

    private void validateMonthYear(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "month must be between 1 and 12.");
        }
        if (year == null || year < 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "year is invalid.");
        }
    }
}
