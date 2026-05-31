package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.domain.LeaveRequest;
import com.example.hr_managment_system.dto.Leave.LeaveBalanceResponse;
import com.example.hr_managment_system.dto.Leave.LeaveCreateRequest;
import com.example.hr_managment_system.dto.Leave.LeaveResponse;
import com.example.hr_managment_system.repository.EmployeeRepository;
import com.example.hr_managment_system.repository.LeaveRequestRepository;
import com.example.hr_managment_system.service.LeaveService;
import com.example.hr_managment_system.util.StatusProgressUtil;
import com.example.hr_managment_system.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private static final int ANNUAL_ALLOWANCE_DAYS = 20;

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;

    @Override
    public LeaveResponse createLeaveRequest(LeaveCreateRequest request) {
        if (request == null || request.startDate() == null || request.endDate() == null || request.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate, endDate and type are required");
        }
        if (request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate cannot be before startDate");
        }
        if (request.reason() == null || request.reason().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Leave reason/cause is required");
        }

        Employee employee = employeeRepository.findByEmployeeId(request.employeeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setStartDate(request.startDate());
        leave.setEndDate(request.endDate());
        leave.setType(request.type());
        leave.setStatus(StatusProgressUtil.PENDING);
        leave.setReason(request.reason());

        LeaveRequest saved = leaveRequestRepository.save(leave);

        try {
            String managerEmail = "hr@company.com";
            List<Employee> activeEmployees = employeeRepository.findAllByIsActiveTrue();
            for (Employee emp : activeEmployees) {
                if (emp.getRole() != null && emp.getRole().getRoleName() != null && 
                    (com.example.hr_managment_system.util.RoleUtil.ADMIN == emp.getRole().getRoleName() || 
                     com.example.hr_managment_system.util.RoleUtil.MANAGER == emp.getRole().getRoleName())) {
                    managerEmail = emp.getEmail();
                    break;
                }
            }
            emailService.sendLeaveRequestEmail(
                    managerEmail,
                    employee.getFirstName() + " " + employee.getLastName(),
                    saved.getType().toString(),
                    saved.getStartDate().toString(),
                    saved.getEndDate().toString()
            );
        } catch (Exception e) {
            log.error("Failed to send leave request email: {}", e.getMessage());
        }

        return toResponse(saved);
    }

    @Override
    public List<LeaveResponse> getMyLeaveRequests(String principal) {
        Employee employee = findEmployeeByPrincipal(principal);
        return leaveRequestRepository.findByEmployee_EmployeeIdOrderByStartDateDesc(employee.getEmployeeId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<LeaveResponse> getPendingLeaveRequests() {
        return leaveRequestRepository.findByStatusOrderByStartDateAsc(StatusProgressUtil.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public LeaveResponse approveLeave(String leaveId, String approverPrincipal, String remarks) {
        return updateLeaveStatus(leaveId, approverPrincipal, StatusProgressUtil.APPROVED, remarks);
    }

    @Override
    public LeaveResponse rejectLeave(String leaveId, String approverPrincipal, String remarks) {
        return updateLeaveStatus(leaveId, approverPrincipal, StatusProgressUtil.REJECTED, remarks);
    }

    @Override
    public LeaveBalanceResponse getLeaveBalance(String employeeId) {
        LocalDate now = LocalDate.now();
        LocalDate yearStart = now.withDayOfYear(1);
        LocalDate yearEnd = now.withMonth(12).withDayOfMonth(31);

        List<LeaveRequest> approved = leaveRequestRepository.findByEmployee_EmployeeIdAndStatusAndStartDateBetween(
                employeeId,
                StatusProgressUtil.APPROVED,
                yearStart,
                yearEnd
        );

        int approvedDays = approved.stream()
                .mapToInt(leave -> (int) ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1)
                .sum();

        return new LeaveBalanceResponse(
                employeeId,
                ANNUAL_ALLOWANCE_DAYS,
                approvedDays,
                Math.max(0, ANNUAL_ALLOWANCE_DAYS - approvedDays)
        );
    }

    private LeaveResponse updateLeaveStatus(String leaveId, String approverPrincipal, StatusProgressUtil status, String remarks) {
        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Leave request not found"));

        Employee approver = findEmployeeByPrincipal(approverPrincipal);
        leave.setApprovedBy(approver);
        leave.setStatus(status);

        LeaveRequest saved = leaveRequestRepository.save(leave);

        try {
            Employee employee = saved.getEmployee();
            if (employee != null) {
                String finalRemarks = (remarks != null && !remarks.trim().isEmpty()) ? remarks :
                        ("Updated by " + approver.getFirstName() + " " + approver.getLastName());
                emailService.sendLeaveStatusEmail(
                        employee.getEmail(),
                        employee.getFirstName() + " " + employee.getLastName(),
                        status.toString(),
                        finalRemarks
                );
            }
        } catch (Exception e) {
            log.error("Failed to send leave status email: {}", e.getMessage());
        }

        return toResponse(saved);
    }

    @Override
    public List<LeaveResponse> getAllLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private Employee findEmployeeByPrincipal(String principal) {
        return employeeRepository.findByUsernameAndIsActiveTrue(principal)
                .or(() -> employeeRepository.findByEmailAndIsActiveTrue(principal))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private LeaveResponse toResponse(LeaveRequest leave) {
        return new LeaveResponse(
                leave.getLeaveRequestId(),
                leave.getEmployee().getEmployeeId(),
                leave.getEmployee().getFirstName() + " " + leave.getEmployee().getLastName(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getType(),
                leave.getStatus(),
                leave.getApprovedBy() == null ? null : leave.getApprovedBy().getEmployeeId(),
                leave.getReason()
        );
    }
}
